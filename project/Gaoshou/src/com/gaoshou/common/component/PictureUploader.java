package com.gaoshou.common.component;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import com.gaoshou.android.R;
import com.gaoshou.common.base.BaseActivity;
import com.gaoshou.common.utils.FileUtil;
import com.gaoshou.common.utils.ImageUtil;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;

public abstract class PictureUploader implements OnClickListener {

    protected boolean isCancelable = true;

    protected static final String PIC_TYPE_JPG = ".jpg";
    protected static final String CACHE_PIC_PATH = "CachePics";

    /** 相机拍照request code **/
    public static final int REQUESTCODE_TAKE_PHOTO = 3866;
    /** 相册选择图片request code **/
    public static final int REQUESTCODE_PICK_PHOTO = 3868;

    protected Context context;
    protected String dialogTitle;

    protected BasicDialog pickerPicDialog;
    protected BaseProgressDialog baseProgressDialog;

    public PictureUploader(Context context, String dialogTitle) {
        super();
        this.context = context;
        this.dialogTitle = dialogTitle;

        init();
    }

    private void init() {
        BasicDialog.Builder bBuilder = new BasicDialog.Builder(context);
        View contentView = LayoutInflater.from(context).inflate(R.layout.view_picker_pic, null);
        contentView.findViewById(R.id.ll_take_pic).setOnClickListener(this);
        contentView.findViewById(R.id.ll_select_pic).setOnClickListener(this);

        bBuilder.setContentView(contentView);

        bBuilder.setTitle(!TextUtils.isEmpty(dialogTitle) ? dialogTitle : "添加图片");

        pickerPicDialog = bBuilder.create();
    }

    public void showPickerPicDialog() {
        if (null != pickerPicDialog && !pickerPicDialog.isShowing()) {
            pickerPicDialog.show();
        }

    }

    public void dimissPickerPicDialog() {
        if (null != pickerPicDialog && pickerPicDialog.isShowing()) {
            pickerPicDialog.dismiss();
        }
    }

    public void showProgressDialog() {
        showProgressDialog(null);
    }

    public void showProgressDialog(String msg) {
        if (null == baseProgressDialog) {
            baseProgressDialog = new BaseProgressDialog(context);
            baseProgressDialog.setCancelable(isCancelable);
        }

        if (null != baseProgressDialog && !baseProgressDialog.isShowing()) {
            if (!TextUtils.isEmpty(msg)) {
                baseProgressDialog.setMessage(msg);
            }
            baseProgressDialog.show();
        }
    }

    protected void dimissProgressDialog() {
        if (null != baseProgressDialog && baseProgressDialog.isShowing()) {
            baseProgressDialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_take_pic:
                Intent openTakePic = ImageUtil.getStartCameraIntent();
                ((BaseActivity) context).startActivityForResult(openTakePic, REQUESTCODE_TAKE_PHOTO);

                dimissPickerPicDialog();
                break;
            case R.id.ll_select_pic:
                Intent openGallery = ImageUtil.getStartGalleryIntent();
                ((BaseActivity) context).startActivityForResult(openGallery, REQUESTCODE_PICK_PHOTO);

                dimissPickerPicDialog();
                break;

            default:
                break;
        }
    }

    public void uploadPicture(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUESTCODE_TAKE_PHOTO) {
                Bundle bundle = data.getExtras();
                Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式  

                if (null != bitmap) {
                    File tmpImageFile = FileUtil.getCacheFile(context, CACHE_PIC_PATH, bitmap.hashCode() + PIC_TYPE_JPG);//创建压缩图片文件
                    ImageUtil.saveEncodeJpegImageFile(tmpImageFile, bitmap, getEncodePercent(bitmap));

                    uploadPictureRequest(tmpImageFile);
                }

            } else if (requestCode == REQUESTCODE_PICK_PHOTO) {
                Uri imvUri = data.getData();
                String imagePath = ImageUtil.getAbsoluteImagePathByUri(context, imvUri);

                if (null != imagePath) {
                    File imageFile = new File(imagePath);
                    if (null != imageFile && imageFile.isFile()) {
                        HashCodeFileNameGenerator generator = new HashCodeFileNameGenerator();
                        String tmpImageFileName = generator.generate(imageFile.getName());
                        File tmpImageFile = FileUtil.getCacheFile(context, CACHE_PIC_PATH, tmpImageFileName + PIC_TYPE_JPG);//创建压缩图片文件

                        Bitmap imageBitmap = BitmapFactory.decodeFile(imagePath);
                        ImageUtil.saveEncodeJpegImageFile(tmpImageFile, imageBitmap, getEncodePercent(imageBitmap));

                        uploadPictureRequest(tmpImageFile);
                    }
                }
            }
        }
    }

    private int getEncodePercent(Bitmap bitmap) {
        int encodePercent = 100;
        if (bitmap.getByteCount() > 100 * 1000 * 1000) {
            encodePercent = 5;
        } else if (bitmap.getByteCount() > 50 * 1000 * 1000) {
            encodePercent = 10;
        } else if (bitmap.getByteCount() > 30 * 1000 * 1000) {
            encodePercent = 30;
        } else if (bitmap.getByteCount() > 10 * 1000 * 1000) {
            encodePercent = 50;
        } else if (bitmap.getByteCount() > 1000 * 1000) {
            encodePercent = 70;
        } else if (bitmap.getByteCount() > 500 * 1000) {
            encodePercent = 90;
        }
        return encodePercent;
    }

    public abstract void uploadPictureRequest(File pictureFile);

    public interface OnUploadFinishCallback {
        void onUpload(File pictureFile);
    }
}
