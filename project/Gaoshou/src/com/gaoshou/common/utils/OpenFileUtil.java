package com.gaoshou.common.utils;

import java.io.File;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import com.gaoshou.android.R;
import com.gaoshou.common.network.FileDownloadAsyncTask;
import com.gaoshou.common.network.FileDownloadAsyncTask.IFileDownloadStatusListener;

public class OpenFileUtil {

    //android获取一个用于打开HTML文件的intent
    private static Intent getHtmlFileIntent(File file) {
        Uri uri = Uri.parse(file.toString()).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(file.toString()).build();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "text/html");
        return intent;
    }

    //android获取一个用于打开图片文件的intent
    private static Intent getImageFileIntent(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "image/*");
        return intent;
    }

    //android获取一个用于打开PDF文件的intent
    private static Intent getPdfFileIntent(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }

    //android获取一个用于打开文本文件的intent
    private static Intent getTextFileIntent(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "text/plain");
        return intent;
    }

    //android获取一个用于打开音频文件的intent
    private static Intent getAudioFileIntent(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }

    //android获取一个用于打开视频文件的intent
    private static Intent getVideoFileIntent(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "video/*");
        return intent;
    }

    //android获取一个用于打开CHM文件的intent
    private static Intent getChmFileIntent(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/x-chm");
        return intent;
    }

    //android获取一个用于打开Word文件的intent
    private static Intent getWordFileIntent(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }

    //android获取一个用于打开Excel文件的intent
    private static Intent getExcelFileIntent(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }

    //android获取一个用于打开PPT文件的intent
    private static Intent getPPTFileIntent(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return intent;
    }

    //android获取一个用于打开apk文件的intent
    private static Intent getApkFileIntent(File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        return intent;
    }

    private static boolean endsWith(String name, String[] fileSuffixes) {

        boolean endsWith = false;

        if (null != name && null != fileSuffixes && 0 != fileSuffixes.length) {
            for (String fileSuffix : fileSuffixes) {
                if (name.endsWith(fileSuffix)) {
                    endsWith = true;
                    break;
                } // if (name.endsWith(fileSuffix))
            } // for (String fileSuffix : fileSuffixes)
        } // if (null != name && null != fileSuffixes && 0 != fileSuffixes.length)

        return endsWith;
    }

    public static void openFile(final Context context, final String fileURIStr) {
        
        if (!TextUtils.isEmpty(fileURIStr)) {
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("文件下载中...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(true);
            
            if (null != progressDialog && !progressDialog.isShowing()) {
                progressDialog.show();
            } // if (null != progressDialog && !progressDialog.isShowing())

            FileDownloadAsyncTask fileDownloadAsyncTask = new FileDownloadAsyncTask(context);
            fileDownloadAsyncTask.setFileDownloadStatusListener(new IFileDownloadStatusListener() {

                @Override
                public void onFileDownloadCompleted(File downloadFile) {
                    if (null != progressDialog && progressDialog.isShowing()) {
                        progressDialog.cancel();
                        openLocalFile(context, downloadFile);
                    } // if (null != progressDialog && progressDialog.isShowing())
                }
            });
            fileDownloadAsyncTask.execute(fileURIStr);
        } // if (!TextUtils.isEmpty(fileURIStr))

    }

    public static void openLocalFile(final Context context, final File localFile) {

        if (Validator.isLocalFileValid(localFile)) {
            Intent openFileIntent = null;
            String localFilePath = localFile.getAbsolutePath();
//            if(!TextUtils.isEmpty(localFilePath)) {
//                int subIndex = localFilePath.indexOf("?e=");
//                if(-1 != subIndex) {
//                    localFilePath = localFilePath.substring(0, subIndex);
//                } 
//            }
            
            if (endsWith(localFilePath, context.getResources().getStringArray(R.array.fileSuffixImage))) {
                openFileIntent = getImageFileIntent(localFile);
                context.startActivity(openFileIntent);
            } else if (endsWith(localFilePath, context.getResources().getStringArray(R.array.fileSuffixWebText))) {
                openFileIntent = getHtmlFileIntent(localFile);
                context.startActivity(openFileIntent);
            } else if (endsWith(localFilePath, context.getResources().getStringArray(R.array.fileSuffixPackage))) {
                openFileIntent = getApkFileIntent(localFile);
                context.startActivity(openFileIntent);
            } else if (endsWith(localFilePath, context.getResources().getStringArray(R.array.fileSuffixAudio))) {
                openFileIntent = getAudioFileIntent(localFile);
                context.startActivity(openFileIntent);
            } else if (endsWith(localFilePath, context.getResources().getStringArray(R.array.fileSuffixVideo))) {
                openFileIntent = getVideoFileIntent(localFile);
                context.startActivity(openFileIntent);
            } else if (endsWith(localFilePath, context.getResources().getStringArray(R.array.fileSuffixText))) {
                openFileIntent = getTextFileIntent(localFile);
                context.startActivity(openFileIntent);
            } else if (endsWith(localFilePath, context.getResources().getStringArray(R.array.fileSuffixPdf))) {
                openFileIntent = getPdfFileIntent(localFile);
                context.startActivity(openFileIntent);
            } else if (endsWith(localFilePath, context.getResources().getStringArray(R.array.fileSuffixWord))) {
                openFileIntent = getWordFileIntent(localFile);
                context.startActivity(openFileIntent);
            } else if (endsWith(localFilePath, context.getResources().getStringArray(R.array.fileSuffixExcel))) {
                openFileIntent = getExcelFileIntent(localFile);
                context.startActivity(openFileIntent);
            } else if (endsWith(localFilePath, context.getResources().getStringArray(R.array.fileSuffixPPT))) {
                openFileIntent = getPPTFileIntent(localFile);
                context.startActivity(openFileIntent);
            } else {
                showToast(context, "无法打开，请安装相应的软件！");
            }

        } else {
            showToast(context, "对不起，这不是文件！");
        }

    }

    private static void showToast(Context context, final CharSequence text) {
        if (null != text && 0 != text.toString().trim().length()) {
            Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } // if (null != text && 0 != text.toString().trim().length())
    }

}
