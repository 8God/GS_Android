package com.gaoshou.common.component;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;

import com.gaoshou.android.R;
import com.gaoshou.common.base.BaseActivity;
import com.gaoshou.common.utils.ImageUtil;


public abstract class PictureShowingActivity<E> extends BaseActivity {

    protected E entity;

    protected BasicDialog basicDialog;

    protected ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_picture_showing);

        initUI();
    }

    private void initUI() {
        imageView = findView(R.id.simv_showing_pic);

        imageView.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if (null == basicDialog) {
                    View contentView = LayoutInflater.from(getContext()).inflate(R.layout.view_picture_showing_menu, null);
                    BasicDialog.Builder builder = new BasicDialog.Builder(getContext());
                    builder.setTitle("操作");
                    builder.setContentView(contentView);

                    findView(contentView, R.id.ll_save_to_local).setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            savePicToLocal();
                            if (null != basicDialog) {
                                basicDialog.dismiss();
                            }
                        }
                    });
                    findView(contentView, R.id.ll_delete_pic).setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            deletePic();

                            if (null != basicDialog) {
                                basicDialog.dismiss();
                            }
                        }
                    });

                    basicDialog = builder.create();
                }

                basicDialog.show();
                return true;
            }
        });

    }

    @Override
    protected final void onStart() {
        super.onStart();

        showPic(imageView);
    }

    protected void savePicToLocal() {
        Drawable drawable = imageView.getDrawable();
        if (null != drawable) {
            Bitmap bitmap = ImageUtil.drawableToBitmap(drawable);

            MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "" + bitmap.hashCode(), "");

            showToast("保存成功");
        }

    }

    protected abstract void showPic(ImageView imageView);

    protected abstract void deletePic();

}
