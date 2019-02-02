package com.gaoshou.common.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.gaoshou.common.component.ImageLoaderFileNameGenerator;
import com.gaoshou.common.network.NetworkTrafficDetector;
import com.gaoshou.common.update.NotifiHolder;

public class FileDownloadUtil {
    private static final int MAX_NUM_OF_FILES_CACHED = 10;

    private static final int NET_SPEED_SLOW_CHECK_DELAY = 90000;

    private static final int DOWNLOAD_SPEED_PER_TIME = 1000;

    private static final String DEFAULT_DOWNLOAD_DIR = "default";

    /**
     * 进度条更新频率,不能更新过快，否则会造成通知栏假死
     */
    private static final int UPDATE_FREQUENCY = 10;
    private static final int UPDATE_PROGRESS = 1;

    public static final int DOWNLOAD_FILE_START = 0;
    public static final int DOWNLOAD_FILE_PROCESSING = 1;
    public static final int DOWNLOAD_FILE_END = 2;

    private static int maxReceivingKBPerSecond = (int) Math.ceil(100 / 0.8 / 0.8);

    private boolean isCompleted;

    private boolean pause = false;

    private int currentByteRead = 0;
    private int preByteRead = 0;
    private int prePercent = 0;
    private long preDownloadTime = 0;

    File DLTempFile = null;

    private NotifiHolder notifiHolder;
    private Handler netSpeedSlowHandler;

    private Runnable netSpeedSlowRunnable;

    private Thread downloadThread;

    private Context netVideoPlayerContext;

    private volatile boolean isRunnable = true;

    private void checkNetSpeedSlow() {
        isCompleted = false;

        netSpeedSlowRunnable = new Runnable() {

            @Override
            public void run() {
                if (!isCompleted) {
                    Toast toast = Toast.makeText(netVideoPlayerContext, "现在网速比较慢，请耐心等待.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        };
        netSpeedSlowHandler = new Handler();
        netSpeedSlowHandler.postDelayed(netSpeedSlowRunnable, NET_SPEED_SLOW_CHECK_DELAY);

    }

    public FileDownloadUtil(Context netVideoPlayerContext) {
        this.netVideoPlayerContext = netVideoPlayerContext;
    }

    public FileDownloadUtil(Context netVideoPlayerContext, NotifiHolder notifiHolder) {
        this.netVideoPlayerContext = netVideoPlayerContext;
        this.notifiHolder = notifiHolder;
    }

    public void pauseDownload() {
        if (null != netSpeedSlowRunnable && null != netSpeedSlowHandler) {
            netSpeedSlowHandler.removeCallbacks(netSpeedSlowRunnable);
        }

        pause = true;
    }

    public void restartDownload() {
        if (null != netSpeedSlowRunnable && null != netSpeedSlowHandler) {
            netSpeedSlowHandler.removeCallbacks(netSpeedSlowRunnable);
        }

        pause = false;
    }

    public void stopDownload() {
        if (null != netSpeedSlowRunnable && null != netSpeedSlowHandler) {
            netSpeedSlowHandler.removeCallbacks(netSpeedSlowRunnable);
        }

        isRunnable = false;
    }

    public boolean isDownloadThreadRunning() {
        return downloadThread.isAlive();
    }


    /**
     * 文件下载方法, 支持断点下载
     * 
     * @param downloadUrl
     *            下载文件的网络地址
     * @param localFilePath
     *            下载文件的本地保存路径
     */
    public void downloadFile(final String downloadUrl, final String localFilePath) {
        Log.i("cth", "FileDownloadUtil:downloadFile");
        checkNetSpeedSlow();

        downloadThread = new Thread() {
            public void run() {
                isRunnable = true;

                URL videoUrl = null;
                HttpURLConnection httpURLConnection = null;
                InputStream inStream = null;
                FileOutputStream outStream = null;

                String filePath = null;
                String fileName = null;

                File localFile = null;

                int startLength;
                int totalLength;

                try {
                    if (TextUtils.isEmpty(localFilePath)) {
                        fileName = new ImageLoaderFileNameGenerator().generate(downloadUrl);
                        filePath = FileUtil.getCacheDir(netVideoPlayerContext, DEFAULT_DOWNLOAD_DIR).getPath() + fileName;
                    } else {
                        filePath = localFilePath;
                    }

                    localFile = new File(filePath);
                    fileName = localFile.getName();

                    // TODO 文件长度可能超过int的最大范围，建议改为long类型
                    startLength = (int) localFile.length();
                    preByteRead = startLength;
                    currentByteRead = startLength;

                    videoUrl = new URL(downloadUrl);
                    httpURLConnection = (HttpURLConnection) videoUrl.openConnection();
                    httpURLConnection.setConnectTimeout(5 * 1000);
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setRequestProperty("Range", "bytes=" + startLength + "-"); //设置获取实体数据的开始位置
                    httpURLConnection.connect();
                    Log.i("cth", "openConnection pre");
                    inStream = httpURLConnection.getInputStream();
                    Log.i("cth", "openConnection after");
                    outStream = new FileOutputStream(filePath, true); // boolean append = true;

                    if (inStream == null) {
                        return;
                    }

                    //                  int totalByte = httpURLConnection.getContentLength();      /* 最新获得不是视频原来的长度 */
                    totalLength = startLength + httpURLConnection.getContentLength();
                    int totalByte = totalLength;
                    notifiHolder.setTotalByte(totalByte);
                    notifiHolder.setCurProgress(calculateDownloadProgress(preByteRead, totalByte));
                    notifiHolder.setDownloadFilePath(filePath);
                    notifiHolder.setDownloadState(DOWNLOAD_FILE_START);

                    if (downLoadListener != null) {
                        calculateDownLoadSpeed();
                        downLoadListener.onStart(notifiHolder);
                    }

                    byte buf[] = new byte[1024 * 10]; // 10kb
                    int readLength = 0;
                    int maxSpeed = 0;
                    while (readLength != -1) {
                        if (isRunnable) {
                            if (pause) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                continue;
                            }

                            readLength = inStream.read(buf);
                            if (readLength > 0) {
                                try {
                                    outStream.write(buf, 0, readLength);
                                    outStream.flush();
                                    currentByteRead += readLength;

                                    notifiHolder.setCurReadByte(currentByteRead);
                                    prePercent = (int) preByteRead * 100 / totalByte;
                                    int curPercent = (int) currentByteRead * 100 / totalByte;

                                    if (curPercent - prePercent >= UPDATE_PROGRESS) {
                                        prePercent = curPercent;
                                        if (null != downLoadListener && isRunnable) { // isRunnable = false (过滤最后一次的notification更新)
                                            notifiHolder.setCurReadByte(currentByteRead);
                                            notifiHolder.setCurProgress(calculateDownloadProgress(currentByteRead, totalByte));
                                            downLoadListener.onLoading(notifiHolder);
                                        }
                                    }

                                    int restSpeed = NetworkTrafficDetector.getInstance().getPreReceivingSpeed() - maxSpeed;
                                    if (restSpeed > maxReceivingKBPerSecond * 4 / 5) {
                                        maxSpeed = 1; // 相当于 sleep(1000);
                                    } else if (restSpeed > maxReceivingKBPerSecond / 5) {
                                        maxSpeed = maxReceivingKBPerSecond * 4 / 25; // 相当于 sleep(40);
                                    } else {
                                        maxSpeed = maxReceivingKBPerSecond * 16 / 25; // 相当于 sleep(10);
                                    }
                                    Thread.sleep(1000 / maxSpeed);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            if (null != downLoadListener) {
                                notifiHolder.setDownloadState(DOWNLOAD_FILE_START);
                                notifiHolder.setCurReadByte(currentByteRead);
                                notifiHolder.setDownloadFilePath(filePath);

                                downLoadListener.onStop(notifiHolder);
                            }
                            break;
                        }
                    }

                    // it is better to put the method here
                    if (isRunnable) {

                        isCompleted = true;
                        isRunnable = false;

                        notifiHolder.setDownloadFilePath(filePath);
                        if (downLoadListener != null) {
                            notifiHolder.setDownloadState(DOWNLOAD_FILE_END);
                            downLoadListener.onComplete(notifiHolder);
                        }
                    }
                } catch (MalformedURLException e) {
                    Log.e("error", "MalformedURLException : " + e);
                    isRunnable = false;
                } catch (IOException e) { /* 下载线程异常退出的情况，比如断网 */
                    Log.e("error", "IOException : " + e);
                    isRunnable = false;
                } finally {
                    if (outStream != null) {
                        try {
                            outStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (inStream != null) {
                        try {
                            inStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        };
        downloadThread.setDaemon(true);
        downloadThread.setPriority(Thread.MIN_PRIORITY);
        downloadThread.start();
    }

    private int calculateDownloadProgress(int currentByteRead, int totalByteRead) {
        int progress = currentByteRead * 2;
        if (0 != totalByteRead && currentByteRead > 0.1f * totalByteRead) {
            progress = (int) (totalByteRead * 0.2 + (currentByteRead - 0.1f * totalByteRead) * 8 / 9);
        }
        return progress;
    }

    private void calculateDownLoadSpeed() {
        new Thread(new Runnable() {

            private long preRead = preByteRead;
            private long curRead = currentByteRead;
            private String initSpeed = "0B/s";

            @Override
            public void run() {
                String unit = null;
                String speed = null;
                long pauseTime = 0;

                while (isRunnable) {
                    if (pause) {
                        try {
                            Thread.sleep(1000);
                            pauseTime += 1000;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (null != downLoadListener && pauseTime <= 1000) {
                            downLoadListener.onPause(notifiHolder);
                        }
                        continue;
                    } else {
                        pauseTime = 0;
                    }

                    try {
                        Thread.sleep(DOWNLOAD_SPEED_PER_TIME);

                        preRead = curRead;
                        curRead = currentByteRead;
                        double downloadByte = (curRead - preRead) / 1024.0;
                        double downloadTime = DOWNLOAD_SPEED_PER_TIME / 1000.0;

                        if (downloadByte >= 1.0) {
                            unit = "KB/s";
                        } else if (downloadByte >= 0.0 && downloadByte < 1.0) {
                            unit = "B/s";
                        }
                        speed = new DecimalFormat("##0.0").format(downloadByte / downloadTime) + unit;

                        if (null != speed && downLoadListener != null && notifiHolder != null) {
                            notifiHolder.setCurDownloadSpeed(speed);
                            downLoadListener.onSpeedChange(notifiHolder);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (null != speed && downLoadListener != null && notifiHolder != null) {
                    speed = initSpeed;
                    notifiHolder.setCurDownloadSpeed(speed);
                    downLoadListener.onSpeedChange(notifiHolder);
                }
            }
        }).start();
    }

    public String calculateDownLoadSpeed(int currentByteRead) {
        String unit = null;
        String speed = null;

        long currentDownloadTime = System.currentTimeMillis();
        double downloadTime = (currentDownloadTime - preDownloadTime) / 1000.0;
        double downloadByte = (currentByteRead - preByteRead) / 1024.0;

        if (downloadTime >= 1.0) {
            if (downloadByte >= 1.0) {
                unit = "KB/s";
            } else if (downloadByte >= 0.0 && downloadByte < 1.0) {
                unit = "B/s";
            }

            preDownloadTime = currentDownloadTime;
            preByteRead = currentByteRead;
            speed = new DecimalFormat("##0.0").format(downloadByte / downloadTime) + unit;
        }
        return speed;
    }

    private OnDownLoadListener downLoadListener;

    public void setOnDownLoadListener(OnDownLoadListener downLoadListener) {
        this.downLoadListener = downLoadListener;
    }

    public interface OnDownLoadListener {

        void onStart(NotifiHolder notifiHolder);

        void onLoading(NotifiHolder notifiHolder);

        void onPause(NotifiHolder notifiHolder);

        void onStop(NotifiHolder notifiHolder);

        void onComplete(NotifiHolder notifiHolder);

        void onSpeedChange(NotifiHolder notifiHolder);
    }
}
