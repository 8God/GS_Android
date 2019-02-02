package com.gaoshou.common.network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.gaoshou.common.utils.FileUtil;
import com.gaoshou.common.utils.MD5UUtil;
import com.gaoshou.common.utils.Validator;

public class FileDownloadAsyncTask extends AsyncTask<String, Integer, File> {

    public interface IFileDownloadStatusListener {
        public void onFileDownloadCompleted(File downloadFile);
    }

    private static final int READ_TIMEOUT_MILLIS = 10000;

    private static final int CONNECTION_TIMEOUT_MILLIS = 5000;

    private static final String REQUEST_METHOD = "GET";

    private static final boolean IS_DEBUG_PRINTING = false;

    private static final String FILE_DOWNLOAD_DIR_NAME = "filedownload";

    private Context notApplicationContext;

    private IFileDownloadStatusListener fileDownloadStatusListener;

    public void setFileDownloadStatusListener(IFileDownloadStatusListener fileDownloadStatusListener) {
        this.fileDownloadStatusListener = fileDownloadStatusListener;
    }

    public FileDownloadAsyncTask(Context notApplicationContext) {
        this.notApplicationContext = notApplicationContext;
    }

    private File getCachedFile(String imageURL) {
        File file = null;
        String[] uriParts = imageURL.split("\\?e=");
        if (null != uriParts && uriParts.length > 1) {
            imageURL = uriParts[0];
        }        
        String name = MD5UUtil.generate(imageURL);
        File fileDownloadDir = FileUtil.getCacheDir(notApplicationContext, FILE_DOWNLOAD_DIR_NAME);
        if (null != fileDownloadDir) {
            file = new File(fileDownloadDir, name);
        }

        return file;
    }

    private File downloadFileFromHTTP(String fileHttpURL) throws Exception {

        File downloadFile = null;
        InputStream is = null;
        FileOutputStream fos = null;

        File downloadFileTmp = getCachedFile(fileHttpURL + "tmp");
        if (null != downloadFileTmp) {
            try {
                URL url = new URL(fileHttpURL.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT_MILLIS);
                conn.setConnectTimeout(CONNECTION_TIMEOUT_MILLIS);
                conn.setRequestMethod(REQUEST_METHOD);
                conn.setDoInput(true);

                conn.connect();

                if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {

                    is = conn.getInputStream();

                    //		bitmap = BitmapFactory.decodeStream(is);
                    if (is == null) {
                        throw new RuntimeException("stream is null");
                    } else {

                        fos = new FileOutputStream(downloadFileTmp);
                        byte[] buffer = new byte[1024];
                        int len = 0;
                        while ((len = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }

                        is.close();
                        fos.close();

                        downloadFile = getCachedFile(fileHttpURL);
                        downloadFileTmp.renameTo(downloadFile);

                    }

                } else {

                    throw new Exception();
                }
            } finally {
                if (null != is) {
                    is.close();
                }
                if (null != fos) {
                    fos.close();
                }
            }
        } // if (null != downloadFileTmp) 

        return downloadFile;
    }

    @Override
    protected File doInBackground(String... params) {

        File downloadFile = null;
        if (null != params[0] && 0 != params[0].trim().length()) {

            if (IS_DEBUG_PRINTING) {
                Log.d("FileDownloadAsyncTask", "Start to download file from " + params[0]);
            }
            
            File file = getCachedFile(params[0]);
            if (Validator.isLocalFileValid(file)) {
                downloadFile = file;
            } else if (Validator.isLocalFilePathValid(params[0])) {
                File sourceFile = new File(params[0]);
                copyFile(sourceFile, file);
                downloadFile = file;
            } else {
                try {
                    downloadFile = downloadFileFromHTTP(params[0]);
                    if (null != downloadFile) {

                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        } else {
            if (IS_DEBUG_PRINTING) {
                Log.d("FileDownloadAsyncTask", "Invalid http file path: " + params[0]);
            }
        }
        return downloadFile;
    }

    @Override
    protected void onPostExecute(File result) {
        super.onPostExecute(result);

        if (IS_DEBUG_PRINTING) {
            if (null != result) {
                Log.d("FileDownloadAsyncTask", "Download successfully and save it in " + result.getAbsolutePath());
            } else {
                Log.d("FileDownloadAsyncTask", "Fail in downloading!");
            }

        }

        if (null != fileDownloadStatusListener) {
            try {
                fileDownloadStatusListener.onFileDownloadCompleted(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 复制文件
    private static void copyFile(File sourceFile, File targetFile) {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            // 关闭流
            if (null != inBuff) {
                try {
                    inBuff.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            if (null != outBuff) {
                try {
                    outBuff.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}
