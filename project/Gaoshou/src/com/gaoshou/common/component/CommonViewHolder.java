package com.gaoshou.common.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gaoshou.android.R;
import com.gaoshou.common.component.DefaultImageLoadingListener.IImageOnLoadingCompleteListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * 
 * @author CTH
 *
 */
public class CommonViewHolder {

    private DisplayImageOptions.Builder displayImageOptionsBuilder = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.loading_bg).showImageForEmptyUri(R.drawable.loading_bg).showImageOnFail(R.drawable.loading_bg).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Bitmap.Config.RGB_565);

    private Context mContext;
    private int mPosition;

    private View mConvertView;

    private SparseArray<View> viewArray;

    private int defaultImage = 0;

    private CommonViewHolder(Context context, int position, View convertView, ViewGroup parent, int layoutId) {
        this.mContext = context;
        this.mPosition = position;
        this.mConvertView = convertView;

        viewArray = new SparseArray<View>();

        mConvertView = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        mConvertView.setTag(this);
    }

    public static CommonViewHolder getViewHolder(Context context, int position, View convertView, ViewGroup parent, int layoutId) {
        CommonViewHolder holder = null;
        if (null == convertView) {
            return new CommonViewHolder(context, position, convertView, parent, layoutId);
        } else {
            holder = (CommonViewHolder) convertView.getTag();
        }

        return holder;

    }

    public <T extends View> T getView(int viewId) {
        View mView = viewArray.get(viewId);

        if (null == mView) {
            mView = mConvertView.findViewById(viewId);
            viewArray.put(viewId, mView);
        }

        return (T) mView;
    }

    public View getConvertView() {
        return this.mConvertView;
    }

    /******************************* 设置TextView *******************************/
    public CommonViewHolder setTextView(int viewId, String text) {
        return setTextView(viewId, text, false);
    }

    public CommonViewHolder setTextView(int viewId, String text, boolean isEmptyGone) {
        TextView tv = getView(viewId);
        if (null != tv) {
            if (!TextUtils.isEmpty(text)) {
                tv.setText(text);
            } else {
                if (isEmptyGone) {
                    tv.setVisibility(View.GONE);
                } else {
                    tv.setText(null);
                }
            }
        }
        return this;
    }

    /******************************* 设置ImageView *******************************/
    public void setImageView(int imageViewId, String imageUri) {
        setImageView(imageViewId, imageUri, 0, false, null);
    }

    public void setImageView(int imageViewId, String imageUri, boolean isOpenLoadAnimation) {
        setImageView(imageViewId, imageUri, 0, isOpenLoadAnimation, null);
    }

    public void setImageView(int imageViewId, String imageUri, IImageOnLoadingCompleteListener iImageOnLoadingCompleteListener) {
        setImageView(imageViewId, imageUri, 0, false, iImageOnLoadingCompleteListener);
    }

    public void setImageView(int imageViewId, String imageUri, int defaultImage) {
        setImageView(imageViewId, imageUri, defaultImage, false, null);
    }

    public void setImageView(int imageViewId, String imageUri, int defaultImage, boolean isOpenLoadAnimation) {
        setImageView(imageViewId, imageUri, defaultImage, isOpenLoadAnimation, null);
    }

    public void setImageView(int imageViewId, String imageUri, int defaultImage, IImageOnLoadingCompleteListener iImageOnLoadingCompleteListener) {
        setImageView(imageViewId, imageUri, defaultImage, false, iImageOnLoadingCompleteListener);
    }

    private void setImageView(int imageViewId, String imageUri, int defaultImage, boolean isOpenLoadAnimation, IImageOnLoadingCompleteListener iImageOnLoadingCompleteListener) {
        ImageView imageView = getView(imageViewId);
        if (null != imageView && mContext != null) {
            if (defaultImage != 0) {
                displayImageOptionsBuilder.showImageOnFail(defaultImage).showImageForEmptyUri(defaultImage);

                Drawable defaultDrawable = mContext.getResources().getDrawable(defaultImage);
                if (null != defaultDrawable) {
                    imageView.setImageDrawable(defaultDrawable);
                }
            } else {
                if (0 != this.defaultImage) {
                    displayImageOptionsBuilder.showImageOnFail(this.defaultImage).showImageForEmptyUri(this.defaultImage);
                }
            }

            DefaultImageLoadingListener defaultImageLoadingListener = null;
            if (null == iImageOnLoadingCompleteListener) {
                defaultImageLoadingListener = new DefaultImageLoadingListener(mContext, imageView, isOpenLoadAnimation);
            } else {
                defaultImageLoadingListener = new DefaultImageLoadingListener(mContext, imageView, isOpenLoadAnimation, iImageOnLoadingCompleteListener);
            }
            ImageLoader.getInstance().displayImage(imageUri, imageView, displayImageOptionsBuilder.build(), defaultImageLoadingListener);
        }
    }

    public void setDefaultImage(int defaultImage) {
        this.defaultImage = defaultImage;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    protected void showToast(final CharSequence text) {
        if (null != text && 0 != text.toString().trim().length()) {
            Toast toast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
            //            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    protected void showLongToast(final CharSequence text) {
        if (null != text && 0 != text.toString().trim().length()) {
            Toast toast = Toast.makeText(mContext, text, Toast.LENGTH_LONG);
            //            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

}
