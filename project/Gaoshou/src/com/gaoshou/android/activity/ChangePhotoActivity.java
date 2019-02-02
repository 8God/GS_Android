package com.gaoshou.android.activity;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.gaoshou.android.R;
import com.gaoshou.common.base.BaseActivity;
import com.gaoshou.common.utils.FileUtil;
import com.gaoshou.common.utils.ImageUtil;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;

public class ChangePhotoActivity extends BaseActivity implements OnClickListener {
    /** 相机拍照request code **/
    private static final int REQUESTCODE_TAKE_PHOTO = 1;
    /** 相册选择图片request code **/
    private static final int REQUESTCODE_PICK_PHOTO = 2;

    private static final String PIC_TYPE_JPG = ".jpg";
    private static final String CACHE_PIC_PATH = "CachePics";


    private String title;
    private String photoUri;

    private MenuItem edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_photo);

        init();
    }

    private void init() {
        boolean isPhotoChanged = false;
        Intent intent = getIntent();
        if (intent != null) {
            title = intent.getStringExtra(getString(R.string.intent_change_photo_title));
            photoUri = intent.getStringExtra(getString(R.string.intent_change_photo_uri));
            isPhotoChanged = intent.getBooleanExtra(getString(R.string.intent_is_photo_changed), false);
        }


        initToolbar(title);

        if (!isPhotoChanged) {
            setImageView(R.id.iv_photo, photoUri, R.drawable.photo, true);
        } else {
            ((ImageView) findView(R.id.iv_photo)).setImageBitmap(BitmapFactory.decodeFile(photoUri));
        }

        findView(R.id.btn_album).setOnClickListener(this);
        findView(R.id.btn_take_photo).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_update_pwd, menu);
        edit = menu.findItem(R.id.item_update_pwd);
        edit.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                finish();
                break;
            case R.id.item_update_pwd:
                Intent intent = new Intent(getContext(), UserSettingActivity.class);
                intent.putExtra(getString(R.string.intent_change_photo_uri), photoUri);
                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        Intent intent = null;
        switch (viewId) {
            case R.id.btn_album:
                intent = ImageUtil.getStartGalleryIntent();
                startActivityForResult(intent, REQUESTCODE_PICK_PHOTO);
                break;
            case R.id.btn_take_photo:
                intent = ImageUtil.getStartCameraIntent();
                startActivityForResult(intent, REQUESTCODE_TAKE_PHOTO);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == REQUESTCODE_TAKE_PHOTO) { // 摄像头照相
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式  
                    if (null != bitmap) {
                        File tmpImageFile = FileUtil.getCacheFile(getContext(), CACHE_PIC_PATH, bitmap.hashCode() + PIC_TYPE_JPG);//创建压缩图片文件
                        ImageUtil.saveEncodeJpegImageFile(tmpImageFile, bitmap, getEncodePercent(bitmap));

                        photoUri = tmpImageFile.getAbsolutePath();

                        ((ImageView) findView(R.id.iv_photo)).setImageBitmap(BitmapFactory.decodeFile(photoUri));

                        if (edit != null)
                            edit.setVisible(true);
                    }
                }
            } else if (requestCode == REQUESTCODE_PICK_PHOTO) {// 相册
                if (resultCode == Activity.RESULT_OK) {
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

                            photoUri = tmpImageFile.getAbsolutePath();

                            ((ImageView) findView(R.id.iv_photo)).setImageBitmap(BitmapFactory.decodeFile(photoUri));

                            if (edit != null)
                                edit.setVisible(true);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("testYJ", "onActivityResult Exception:" + e.getMessage());
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
