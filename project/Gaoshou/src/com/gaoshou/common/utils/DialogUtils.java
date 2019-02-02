package com.gaoshou.common.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.gaoshou.android.R;
import com.gaoshou.common.component.BasicDialog;

public class DialogUtils {
    public static BasicDialog showAlertDialog(Context context, String msg) {
        return showAlertDialog(context, msg, null);
    }

    public static BasicDialog showAlertDialog(Context context, String msg, String title) {
        BasicDialog dialog = new BasicDialog.Builder(context).setTitle(title).setMessage(msg).setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();

        dialog.show();

        return dialog;
    }

    public static BasicDialog showAlertDialog(Context context, String msg, String pBtnText, DialogInterface.OnClickListener pBtnClickListener) {
        return showAlertDialog(context, null, msg, pBtnText, pBtnClickListener);
    }

    public static BasicDialog showAlertDialog(Context context, String title, String msg, String pBtnText, DialogInterface.OnClickListener pBtnClickListener) {
        if (null == pBtnClickListener) {
            pBtnClickListener = new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            };
        }
        BasicDialog dialog = new BasicDialog.Builder(context).setTitle(title).setMessage(msg).setPositiveButton(pBtnText, pBtnClickListener).create();

        dialog.show();

        return dialog;
    }

    public static BasicDialog showAlertDialog(Context context, String msg, String pBtnText, DialogInterface.OnClickListener pBtnClickListener, String nBtnText,
            DialogInterface.OnClickListener nBtnClickListener) {
        return showAlertDialog(context, null, msg, pBtnText, pBtnClickListener, nBtnText, nBtnClickListener);
    }

    /**
     * click事件传null为默认的关闭dialog功能
     * @param context
     * @param msg
     * @param pBtnText     确定按键的text
     * @param pBtnClickListener  确定按键的click事件
     * @param nBtnText     取消按键的text
     * @param nBtnClickListener   取消按键的click事件
     */
    public static BasicDialog showAlertDialog(Context context, String title, String msg, String pBtnText, DialogInterface.OnClickListener pBtnClickListener, String nBtnText,
            DialogInterface.OnClickListener nBtnClickListener) {
        if (null == pBtnClickListener) {
            pBtnClickListener = new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            };
        }
        if (null == nBtnClickListener) {
            nBtnClickListener = new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            };
        }
        BasicDialog dialog = new BasicDialog.Builder(context).setTitle(title).setMessage(msg).setPositiveButton(pBtnText, pBtnClickListener).setNegativeButton(nBtnText, nBtnClickListener).create();
        dialog.show();

        return dialog;
    }

    public static BasicDialog showContentDialog(Context context, View contentView) {
        return showContentDialog(context, null, contentView);
    }

    public static BasicDialog showContentDialog(Context context, String title, View contentView) {
        BasicDialog dialog = new BasicDialog.Builder(context).setTitle(title).setContentView(contentView).create();
        dialog.show();

        return dialog;
    }
}
