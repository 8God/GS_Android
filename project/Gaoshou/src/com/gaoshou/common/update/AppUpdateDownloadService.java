package com.gaoshou.common.update;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.gaoshou.android.R;
import com.gaoshou.common.base.GsApplication;
import com.gaoshou.common.utils.FileDownloadUtil;
import com.gaoshou.common.utils.FileDownloadUtil.OnDownLoadListener;


public class AppUpdateDownloadService extends Service {

    private static final Random random = new Random(System.currentTimeMillis());

    private int notifiID;
    private FileDownloadUtil fileDownloadUtil;
    private NotificationManager notificationManager;
    private Notification notification;

    public static final String APP_DOWNLOAD_PATH = "APP_DOWNLOAD_PATH";
    public static final String APP_LOCAL_PATH = "APP_LOCAL_PATH";
    public static final String APP_DOWNLOAD_TITLE = "APP_DOWNLOAD_TITLE";

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null != intent) {
            String appDownloadPath = intent.getExtras().getString(APP_DOWNLOAD_PATH);
            String appLocalPath = intent.getExtras().getString(APP_LOCAL_PATH);
            String appDownloadTitle = intent.getExtras().getString(APP_DOWNLOAD_TITLE);
            Log.i("cth", "onStartCommand:appDownloadPath = " + appDownloadPath);
            Log.i("cth", "onStartCommand:appLocalPath = " + appLocalPath);
            Log.i("cth", "onStartCommand:appDownloadTitle = " + appDownloadTitle);
            if (!TextUtils.isEmpty(appDownloadPath) && !TextUtils.isEmpty(appLocalPath)) {
                startDownloadApp(appDownloadPath, appLocalPath, appDownloadTitle);
            }
        }

        return super.onStartCommand(intent, flags, startId);
    };

    private void startDownloadApp(final String appDownloadPath, final String appLocalPath, final String appDownloadTitle) {

        GsApplication.getInstance(this).setUpdateDownloading(true);
        notifiID = random.nextInt();
        notification = new Notification();
        notification.icon = R.drawable.ic_launcher;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        //        RemoteViews remoteView = new RemoteViews(getPackageName(), R.layout.videoview_notification);
        //        String title = null;
        //        if (!TextUtils.isEmpty(appDownloadTitle)) {
        //            title = appDownloadTitle;
        //
        //        } else {
        //            title = getString(R.string.app_name);
        //        }
        //
        //        remoteView.setTextViewText(R.id.videoview_notification_title, title);
        //        notification.contentView = remoteView;
        notification.contentView = createRemoteView(appDownloadTitle);

        NotifiHolder notifiHolder = new NotifiHolder();
        notifiHolder.setNotifiID(notifiID);
        notifiHolder.setFileTitle(appDownloadTitle + " 下载中，请稍后");
        notifiHolder.setTotalByte(0);
        notifiHolder.setCurReadByte(0);
        notifiHolder.setCurProgress(0);
        notifiHolder.setCurDownloadSpeed(null);
        notifiHolder.setDownloadState(FileDownloadUtil.DOWNLOAD_FILE_START);

        fileDownloadUtil = new FileDownloadUtil(this, notifiHolder);
        fileDownloadUtil.setOnDownLoadListener(new OnDownLoadListener() {

            @Override
            public void onStop(NotifiHolder notifiHolder) {
                GsApplication.getInstance(AppUpdateDownloadService.this).setUpdateDownloading(false);
                stopSelf();
            }

            @Override
            public void onStart(NotifiHolder notifiHolder) {
                updateNotifi(notifiHolder, appDownloadTitle + " 下载中，请稍后");
            }

            @Override
            public void onSpeedChange(NotifiHolder notifiHolder) {

            }

            @Override
            public void onLoading(NotifiHolder notifiHolder) {
                updateNotifi(notifiHolder, appDownloadTitle + " 下载中，请稍后");
            }

            @SuppressWarnings("deprecation")
            @Override
            public void onComplete(NotifiHolder notifiHolder) {
                updateNotifi(notifiHolder, appDownloadTitle);
                notifiHolder.setCurProgress(notifiHolder.getTotalByte());
                notifiHolder.setCurDownloadSpeed("0KB/s");

                String datetime = DateFormat.format("kk:mm", System.currentTimeMillis()).toString();
                notification.contentView.setTextViewText(R.id.download_notification_time, datetime);
                notification.contentView.setViewVisibility(R.id.download_notification_progress, View.INVISIBLE);

                //下载完成修改文件名，去掉dtmp
                File downloadFile = new File(appLocalPath);
                File localFile = null;
                if (downloadFile.exists()) {
                    String[] filenameArray = downloadFile.getName().split("\\.");
                    String newFilename = filenameArray[0] + "." + filenameArray[filenameArray.length - 1];
                    localFile = new File(downloadFile.getParent(), newFilename);
                    downloadFile.renameTo(localFile);
                }

                Intent installIntent = new Intent();
                installIntent.setAction(android.content.Intent.ACTION_VIEW);
                installIntent.setDataAndType(Uri.fromFile(localFile), "application/vnd.android.package-archive");
                installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pIntent = PendingIntent.getActivity(AppUpdateDownloadService.this, notifiHolder.getNotifiID(), installIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                notification.setLatestEventInfo(AppUpdateDownloadService.this, appDownloadTitle + "下载完成", "下载完成，点击安装。", pIntent);
                notificationManager.notify(notifiHolder.getNotifiID(), notification);

                GsApplication.getInstance(AppUpdateDownloadService.this).setUpdateDownloading(false);

                if (localFile.exists()) {
                    installApp(Uri.fromFile(localFile));
                }

                //超过APK文件时删除
                if (null != localFile) {
                    File[] downloadFiles = localFile.getParentFile().listFiles();
                    if (null != downloadFiles && downloadFiles.length >= 5) {
                        Arrays.sort(downloadFiles, new CompratorByLastModified());
                        for (int i = 0; i < downloadFiles.length - 1; i++) {
                            File tempFile = downloadFiles[i];
                            if (tempFile.exists()) {
                                tempFile.delete();
                            }
                        }
                    }
                }

                stopSelf();

            }

            @Override
            public void onPause(NotifiHolder notifiHolder) {
                // TODO Auto-generated method stub

            }

        });
        fileDownloadUtil.downloadFile(appDownloadPath, appLocalPath);
    }

    private void updateNotifi(NotifiHolder notifiHolder, String appDownloadTitle) {

        int totalByte = notifiHolder.getTotalByte();
        int progress = notifiHolder.getCurProgress();

        RemoteViews viewBuf = createRemoteView(appDownloadTitle);
        viewBuf.setProgressBar(R.id.download_notification_progress, totalByte, progress, false);
        String datetime = DateFormat.format("kk:mm", System.currentTimeMillis()).toString();
        viewBuf.setTextViewText(R.id.download_notification_time, datetime);

        notification.contentView = viewBuf;
        notificationManager.notify(notifiHolder.getNotifiID(), notification);

    }

    private void installApp(final Uri appFileUri) {

        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(appFileUri, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }

    private RemoteViews createRemoteView(String appDownloadTitle) {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.download_notification);
        String title = null;
        if (!TextUtils.isEmpty(appDownloadTitle)) {
            title = appDownloadTitle;

        } else {
            title = getString(R.string.app_name);
        }

        remoteViews.setTextViewText(R.id.download_notification_title, title);

        return remoteViews;
    }

    class CompratorByLastModified implements Comparator<File> {
        public int compare(File f1, File f2) {
            long diff = f1.lastModified() - f2.lastModified();
            if (diff > 0)
                return 1;
            else if (diff == 0)
                return 0;
            else
                return -1;
        }

        public boolean equals(Object obj) {
            return true;
        }
    }

}
