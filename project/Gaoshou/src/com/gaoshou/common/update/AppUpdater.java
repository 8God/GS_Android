package com.gaoshou.common.update;

import java.io.File;
import java.net.URI;

import com.gaoshou.android.R;
import com.gaoshou.common.component.BasicDialog;
import com.gaoshou.common.utils.FileUtil;
import com.gaoshou.common.utils.NetworkUtil;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.webkit.MimeTypeMap;
import android.widget.Toast;


public class AppUpdater {

    private Context notApplicationContext;
    private DownloadManager downloadManager;

    private static boolean IS_DEVELOPMENT_SUPPORTED = false;

    private long downloadID = -1L;

    private IAppUpdatingListener appUpdatingListener;

    private ProgressDialog downloadProgressDialog;

    public interface IAppUpdatingListener {
        public void onAppUpdatingCompleted();
    }

    public AppUpdater(final Context notApplicationContext) {
        this.notApplicationContext = notApplicationContext;
        this.downloadManager = (DownloadManager) notApplicationContext.getSystemService(Context.DOWNLOAD_SERVICE);

    }

    public void setAppUpdatingListener(IAppUpdatingListener appUpdatingListener) {
        this.appUpdatingListener = appUpdatingListener;
    }

    public IAppUpdatingListener getAppUpdatingListener() {
        return this.appUpdatingListener;
    }

    public boolean update(final PackageInfo packageInfo, final String appName, final String versionCode, final String versionName, final String updateNote, final String downloadURL) {
        boolean isHasNewVersion = false;
        if (IS_DEVELOPMENT_SUPPORTED && packageInfo.versionCode != Float.parseFloat(versionCode) || packageInfo.versionCode < Float.parseFloat(versionCode)) {
            isHasNewVersion = true;
            if (isAppFileExistent(appName, versionCode)) {

                BasicDialog.Builder installDialogBuilder = new BasicDialog.Builder(notApplicationContext);
                installDialogBuilder.setTitle("安装" + appName + " " + versionName);

                StringBuffer messageStrBuf = new StringBuffer();
                messageStrBuf.append("当前正使用的版本为" + packageInfo.versionName + ", 最新版本为" + versionName + "\n\n");
                if (null != updateNote && 0 != updateNote.length()) {
                    messageStrBuf.append(appName + " " + versionName);
                    messageStrBuf.append("版更新说明：\n");
                    messageStrBuf.append(updateNote);
                    messageStrBuf.append("\n是否现在就安装");
                    messageStrBuf.append(appName + " " + versionName);
                    messageStrBuf.append("版的apk？");
                } else {
                    messageStrBuf.append("\n\n是否现在就安装");
                    messageStrBuf.append(appName + " " + versionName);
                    messageStrBuf.append("版的apk？");
                }
                installDialogBuilder.setMessage(messageStrBuf.toString());
                installDialogBuilder.setPositiveButton("是", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        installApp(Uri.fromFile(getAppUpdateAPKFile(appName, versionCode)));
                    }
                });
                installDialogBuilder.setNegativeButton("否", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (null != appUpdatingListener) {
                            appUpdatingListener.onAppUpdatingCompleted();
                        }
                    }
                });

                BasicDialog installDialog = installDialogBuilder.create();
                installDialog.setOnDismissListener(new OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        // TODO Auto-generated method stub
                        // CommonUtils.layoutFullscreen(notApplicationContext);
                    }
                });
                installDialog.show();

            } else {
                BasicDialog.Builder downloadDialogBuilder = new BasicDialog.Builder(notApplicationContext);
                //                downloadDialogBuilder.setIcon(R.drawable.ic_launcher);
                downloadDialogBuilder.setTitle("下载更新 " + appName + " " + versionName);
                StringBuffer messageStrBuf = new StringBuffer();
                messageStrBuf.append("当前正使用的版本为" + packageInfo.versionName + ", 最新版本为" + versionName + "\n\n");
                if (null != updateNote && 0 != updateNote.length()) {
                    messageStrBuf.append(appName + " " + versionName);
                    messageStrBuf.append("版更新说明：\n");
                    messageStrBuf.append(updateNote);
                    messageStrBuf.append("\n是否现在就下载");
                    messageStrBuf.append(appName + " " + versionName);
                    messageStrBuf.append("版的apk？");
                } else {
                    messageStrBuf.append("\n\n是否现在就下载");
                    messageStrBuf.append(appName + " " + versionName);
                    messageStrBuf.append("版的apk？");
                }
                downloadDialogBuilder.setMessage(messageStrBuf.toString());
                downloadDialogBuilder.setPositiveButton("是", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        int networkState = NetworkUtil.getNetworkState(notApplicationContext);
                        switch (networkState) {
                            case NetworkUtil.TYPE_NULL:
                                Toast toast = Toast.makeText(notApplicationContext, "网络未连接，请检查网络", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                break;
                            case NetworkUtil.TYPE_MOBILE:
                                BasicDialog.Builder alertDialogBuilder = new BasicDialog.Builder(notApplicationContext);
                                alertDialogBuilder.setTitle("提示");
                                alertDialogBuilder.setMessage(R.string.isMobileNetworkTips);
                                alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                alertDialogBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startUpdateDownload(appName, versionCode, versionName, downloadURL);
                                        dialog.dismiss();
                                    }
                                });
                                alertDialogBuilder.create().show();
                                break;
                            case NetworkUtil.TYPE_WIFI:
                                startUpdateDownload(appName, versionCode, versionName, downloadURL);
                                break;
                            default:
                                break;
                        }

                    }

                });
                downloadDialogBuilder.setNegativeButton("否", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (null != appUpdatingListener) {
                            appUpdatingListener.onAppUpdatingCompleted();
                        }
                    }
                });

                BasicDialog downloadDialog = downloadDialogBuilder.create();
                //                downloadDialog.setIcon(R.drawable.ic_launcher);
                downloadDialog.setOnDismissListener(new OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        // CommonUtils.layoutFullscreen(notApplicationContext);
                    }
                });
                downloadDialog.show();
            }

        } else {
            isHasNewVersion = false;
            if (null != appUpdatingListener) {
                appUpdatingListener.onAppUpdatingCompleted();
            }
        }

        return isHasNewVersion;
    }

    /**
     * 开启自定义下载器进行下载
     * 
     * @param appName
     * @param versionCode
     * @param versionName
     * @param downloadURL
     */
    public void startUpdateDownload(final String appName, final String versionCode, final String versionName, final String downloadURL) {
        String versionNameTmp = null;
        if (TextUtils.isEmpty(versionName)) {
            versionNameTmp = "最新版";
        } else {
            versionNameTmp = versionName;
        }

        Toast toast = Toast.makeText(notApplicationContext, "开始下载，下载结束自动安装，请稍后", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        String title = appName + " " + versionNameTmp;
        startDownloadService(downloadURL, title, getDesitinationPath(appName, versionCode));

    }

    private void startDownloadService(String downloadURL, String title, String desitinationPath) {
        Intent updateIntent = new Intent("com.zcmedical.common.update.AppUpdateDownloadService");
        updateIntent.putExtra(AppUpdateDownloadService.APP_DOWNLOAD_PATH, downloadURL);
        updateIntent.putExtra(AppUpdateDownloadService.APP_DOWNLOAD_TITLE, title);
        updateIntent.putExtra(AppUpdateDownloadService.APP_LOCAL_PATH, desitinationPath);
        notApplicationContext.startService(updateIntent);
    }

    /**
     * 调用系统下载器进行下载
     * 
     * @param downloadURL
     * @param downloadFileURI
     */
    private void download(final String downloadURL, final Uri downloadFileURI) {

        Uri resource = Uri.parse(downloadURL);
        DownloadManager.Request request = new DownloadManager.Request(resource);
        request.setAllowedNetworkTypes(Request.NETWORK_MOBILE | Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(false);
        // 设置文件类型
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(downloadURL));
        request.setMimeType(mimeString);
        // 不在通知栏中显示
        //		request.setShowRunningNotification(false);
        request.setVisibleInDownloadsUi(false);
        request.setDestinationUri(downloadFileURI);
        request.setTitle("程序下载中...");
        downloadID = this.downloadManager.enqueue(request);

        notApplicationContext.registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                queryDownloadStatus(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0), this);
            }
        }, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

    private File getAppUpdateCacheDir() {
        return FileUtil.getCacheDir(this.notApplicationContext, "AppUpdateCache");
    }

    private File getAppUpdateAPKFile(final String appName, final String versionName) {

        File appUpdateAPKFile = new File(getAppUpdateCacheDir(), File.separator + appName + "-" + versionName + ".apk");

        return appUpdateAPKFile;
    }

    private File getAppUpdateAPKDownloadTemporaryFile(final String appName, final String versionName) {
        File appUpdateAPKDownloadTemporaryFile = new File(getAppUpdateCacheDir(), File.separator + appName + "-" + versionName + ".dtmp.apk");

        return appUpdateAPKDownloadTemporaryFile;
    }

    private boolean isAppFileExistent(final String appName, final String versionName) {
        boolean isAppFileExistent = false;
        File appUpdateAPKFile = getAppUpdateAPKFile(appName, versionName);
        if (appUpdateAPKFile.exists()) {
            isAppFileExistent = true;
        }

        return isAppFileExistent;
    }

    private String getDesitinationPath(final String appName, final String versionName) {

        File appUpdateAPKDownloadTemporaryFile = getAppUpdateAPKDownloadTemporaryFile(appName, versionName);

        return appUpdateAPKDownloadTemporaryFile.getPath();
    }

    private Uri getDesitinationURI(final String appName, final String versionName) {

        File appUpdateAPKDownloadTemporaryFile = getAppUpdateAPKDownloadTemporaryFile(appName, versionName);

        return Uri.fromFile(appUpdateAPKDownloadTemporaryFile);
    }

    private void queryDownloadStatus(final long downloadID, final BroadcastReceiver receiver) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadID);
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                    //					Log.i("JNSTesting", "STATUS_PAUSED");
                case DownloadManager.STATUS_PENDING:
                    //					Log.i("JNSTesting", "STATUS_PENDING");
                case DownloadManager.STATUS_RUNNING:
                    // 正在下载，不做任何事情
                    //					Log.i("JNSTesting", "STATUS_RUNNING");
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    // 完成
                    //					Log.i("JNSTesting", "下载完成");
                    if (null != downloadProgressDialog) {
                        downloadProgressDialog.dismiss();
                    }
                    String localURI = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

                    //					String localFileName = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));

                    try {
                        File appUpdateAPKDownloadTemporaryFile = new File(URI.create(localURI));
                        File appUpdateAPKFile = new File(URI.create(localURI.replaceAll(".dtmp.apk", ".apk")));
                        appUpdateAPKDownloadTemporaryFile.renameTo(appUpdateAPKFile);
                        if (appUpdateAPKDownloadTemporaryFile.exists()) {
                            appUpdateAPKDownloadTemporaryFile.delete();
                        }
                        installApp(Uri.fromFile(appUpdateAPKFile));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case DownloadManager.STATUS_FAILED:
                    // 清除已下载的内容，重新下载
                    //					Log.i("JNSTesting", "STATUS_FAILED");
                    if (null != downloadProgressDialog) {
                        downloadProgressDialog.dismiss();
                    }
                    //					int reason = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON));
                    //					switch (reason) {
                    //						case DownloadManager.ERROR_CANNOT_RESUME:
                    //							Log.i("JNSTesting", "ERROR_CANNOT_RESUME");
                    //							break;
                    //						case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                    //							Log.i("JNSTesting", "ERROR_DEVICE_NOT_FOUND");
                    //							break;
                    //						case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                    //							Log.i("JNSTesting", "ERROR_FILE_ALREADY_EXISTS");
                    //							break;
                    //						case DownloadManager.ERROR_FILE_ERROR:
                    //							Log.i("JNSTesting", "ERROR_FILE_ERROR");
                    //							break;
                    //						case DownloadManager.ERROR_HTTP_DATA_ERROR:
                    //							Log.i("JNSTesting", "ERROR_HTTP_DATA_ERROR");
                    //							break;
                    //						case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                    //							Log.i("JNSTesting", "ERROR_INSUFFICIENT_SPACE");
                    //							break;
                    //						case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                    //							Log.i("JNSTesting", "ERROR_TOO_MANY_REDIRECTS");
                    //							break;
                    //						case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                    //							Log.i("JNSTesting", "ERROR_UNHANDLED_HTTP_CODE");
                    //							break;
                    //						case DownloadManager.ERROR_UNKNOWN:
                    //							Log.i("JNSTesting", "ERROR_UNKNOWN");
                    //							break;
                    //
                    //						default:
                    //							break;
                    //					}

                    String localURI2 = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

                    //					String localFileName = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));

                    try {
                        File appUpdateAPKDownloadTemporaryFile = new File(URI.create(localURI2));
                        if (appUpdateAPKDownloadTemporaryFile.exists()) {
                            appUpdateAPKDownloadTemporaryFile.delete();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Toast toast = Toast.makeText(notApplicationContext, "下载失败！", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    downloadManager.remove(downloadID);

                    break;
            }
        }

        notApplicationContext.unregisterReceiver(receiver);
    }

    private void installApp(final Uri appFileUri) {

        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(appFileUri, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notApplicationContext.startActivity(intent);
    }
}
