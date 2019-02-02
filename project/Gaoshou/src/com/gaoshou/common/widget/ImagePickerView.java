package com.gaoshou.common.widget;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaoshou.android.R;
import com.gaoshou.common.base.BaseActivity;
import com.gaoshou.common.component.BasicDialog;
import com.gaoshou.common.utils.FileUtil;
import com.gaoshou.common.utils.ImageUtil;
import com.gaoshou.common.utils.OpenFileUtil;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;

/**
 * 
 * @author Hoang created_at:20150705
 */
public class ImagePickerView extends FrameLayout implements OnClickListener {

    private static final String PIC_TYPE_JPG = ".jpg";
    private static final String CACHE_PIC_PATH = "CachePics";

    private OnImageChangedListener onImageChangedListener;

    /** 相机拍照request code **/
    public static final int REQUESTCODE_TAKE_PHOTO = 1;
    /** 相册选择图片request code **/
    public static final int REQUESTCODE_PICK_PHOTO = 2;

    private ImageButton delPicImv;
    private TextView addPicTv;
    private ImageView postPicImv;
    private BasicDialog pickerPicDialog;

    private String imagePath;

    public ImagePickerView(Context context) {
        super(context);

        initLayout();
    }

    public ImagePickerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initLayout();
    }

    private void initLayout() {
        View layout = LayoutInflater.from(getContext()).inflate(R.layout.view_image_picker, null);

        delPicImv = (ImageButton) layout.findViewById(R.id.imvbtn_del_pic);
        addPicTv = (TextView) layout.findViewById(R.id.tv_add_pic);
        postPicImv = (ImageView) layout.findViewById(R.id.imv_post_pic);

        addPicTv.setOnClickListener(this);
        delPicImv.setOnClickListener(this);
        postPicImv.setOnClickListener(this);

        addView(layout);

    }

    public void setImageByPath(String imagePath) {
        this.imagePath = imagePath;

        int width = 280;
        int height = 280;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//不加载bitmap到内存中  
        BitmapFactory.decodeFile(imagePath, options);
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inSampleSize = 1;

        if (outWidth != 0 && outHeight != 0) {
            options.inSampleSize = (outWidth / width + outHeight / height) / 2;
        }
        options.inJustDecodeBounds = false;

        Bitmap bm = BitmapFactory.decodeFile(imagePath, options);
        postPicImv.setImageBitmap(bm);

        hasSetImageResource(true);
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImageResource(Bitmap bitmap) {
        postPicImv.setImageBitmap(bitmap);

        String filePath = null;

        if (null != onImageChangedListener) {
            onImageChangedListener.onAddImage(filePath);
        }

        hasSetImageResource(true);
    }

    public void clearImageView() {
        postPicImv.setImageBitmap(null);

        if (!TextUtils.isEmpty(imagePath)) {
            File imageFile = new File(imagePath);
            if (null != imageFile && imageFile.isFile()) {
                imageFile.delete();
            }
        }

        if (null != onImageChangedListener) {
            onImageChangedListener.onDelImage();
        }

        hasSetImageResource(false);
    }

    private void hasSetImageResource(boolean isHasSet) {
        if (isHasSet) {
            postPicImv.setVisibility(View.VISIBLE);
            delPicImv.setVisibility(View.VISIBLE);
            addPicTv.setVisibility(View.GONE);
        } else {
            postPicImv.setVisibility(View.GONE);
            delPicImv.setVisibility(View.GONE);
            addPicTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_add_pic:
                showPickerPicDialog();
                break;
            case R.id.imvbtn_del_pic:
                clearImageView();
                break;
            case R.id.ll_take_pic:
                Log.i("testYJ", "onImageChangedListener = " + onImageChangedListener);
                Intent openTakePic = ImageUtil.getStartCameraIntent();
                ((BaseActivity) getContext()).startActivityForResult(openTakePic, REQUESTCODE_TAKE_PHOTO);
                Log.i("testYJ", "onImageChangedListener = " + onImageChangedListener);
                if (null != onImageChangedListener) {
                    onImageChangedListener.onStartSelectImage(getId(), REQUESTCODE_TAKE_PHOTO);
                }
                dimissPickerPicDialog();
                break;
            case R.id.ll_select_pic:
                Log.i("testYJ", "onImageChangedListener = " + onImageChangedListener);
                Intent openGallery = ImageUtil.getStartGalleryIntent();
                ((BaseActivity) getContext()).startActivityForResult(openGallery, REQUESTCODE_PICK_PHOTO);
                Log.i("testYJ", "onImageChangedListener = " + onImageChangedListener);
                if (null != onImageChangedListener) {
                    onImageChangedListener.onStartSelectImage(getId(), REQUESTCODE_PICK_PHOTO);
                }
                dimissPickerPicDialog();
                break;
            case R.id.imv_post_pic:
                OpenFileUtil.openFile(getContext(), imagePath);
                break;
            default:
                break;
        }
    }

    private void showPickerPicDialog() {
        if (null == pickerPicDialog) {
            BasicDialog.Builder bBuilder = new BasicDialog.Builder(getContext());
            View contentView = LayoutInflater.from(getContext()).inflate(R.layout.view_picker_pic, null);
            contentView.findViewById(R.id.ll_take_pic).setOnClickListener(this);
            contentView.findViewById(R.id.ll_select_pic).setOnClickListener(this);

            bBuilder.setContentView(contentView);
            bBuilder.setTitle("添加图片");

            pickerPicDialog = bBuilder.create();
        }
        pickerPicDialog.show();

    }

    public OnImageChangedListener getOnImageChangedListener() {
        return onImageChangedListener;
    }

    public void setOnImageChangedListener(OnImageChangedListener onImageChangedListener) {
        this.onImageChangedListener = onImageChangedListener;
    }

    private void dimissPickerPicDialog() {
        if (null != pickerPicDialog && pickerPicDialog.isShowing()) {
            pickerPicDialog.dismiss();
        }
    }

    public interface OnImageChangedListener {
        void onStartSelectImage(int viewId, int requestId);

        void onAddImage(String filePath);

        void onDelImage();
    }

    public void dealPickPicData(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ImagePickerView.REQUESTCODE_TAKE_PHOTO) {
                Bundle bundle = data.getExtras();
                Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式  

                if (null != bitmap) {
                    File tmpImageFile = FileUtil.getCacheFile(getContext(), CACHE_PIC_PATH, bitmap.hashCode() + PIC_TYPE_JPG);//创建压缩图片文件
                    ImageUtil.saveEncodeJpegImageFile(tmpImageFile, bitmap, getEncodePercent(bitmap));
                    setImageByPath(tmpImageFile.getAbsolutePath());

                    if (onImageChangedListener != null) {
                        onImageChangedListener.onAddImage(tmpImageFile.getAbsolutePath());
                    }
                }

            } else if (requestCode == ImagePickerView.REQUESTCODE_PICK_PHOTO) {
                Uri imvUri = data.getData();
                String imagePath = ImageUtil.getAbsoluteImagePathByUri(getContext(), imvUri);

                if (null != imagePath) {
                    File imageFile = new File(imagePath);
                    if (null != imageFile && imageFile.isFile()) {
                        HashCodeFileNameGenerator generator = new HashCodeFileNameGenerator();
                        String tmpImageFileName = generator.generate(imageFile.getName());
                        File tmpImageFile = FileUtil.getCacheFile(getContext(), CACHE_PIC_PATH, tmpImageFileName + PIC_TYPE_JPG);//创建压缩图片文件

                        Bitmap imageBitmap = BitmapFactory.decodeFile(imagePath);
                        ImageUtil.saveEncodeJpegImageFile(tmpImageFile, imageBitmap, getEncodePercent(imageBitmap));
                        setImageByPath(tmpImageFile.getAbsolutePath());

                        if (onImageChangedListener != null) {
                            onImageChangedListener.onAddImage(tmpImageFile.getAbsolutePath());
                        }
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

}
