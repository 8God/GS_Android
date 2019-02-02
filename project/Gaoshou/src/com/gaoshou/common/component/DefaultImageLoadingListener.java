package com.gaoshou.common.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.gaoshou.android.R;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class DefaultImageLoadingListener implements ImageLoadingListener {

    private Context notApplicationContext;

    private ImageView imageView;

    private View imageViewLoadingProgressBarContainerView;

    private View imageViewOrnament;

    private IImageOnLoadingCompleteListener loadingCompleteListener;

    private boolean isCircle = false;
    private boolean isOpenLoadAnimation = false;
    private int imgWidth = 0;
    private int imgHeight = 0;

    public DefaultImageLoadingListener(Context notApplicationContext, IImageOnLoadingCompleteListener loadingCompleteListener) {
        this.notApplicationContext = notApplicationContext;
        this.loadingCompleteListener = loadingCompleteListener;
    }

    public DefaultImageLoadingListener(Context notApplicationContext, ImageView imageView, boolean isCircle, int imgWidth, int imgHeight) {
        this.notApplicationContext = notApplicationContext;
        this.imageView = imageView;
        this.isCircle = isCircle;
        this.imgWidth = imgWidth;
        this.imgHeight = imgHeight;
    }

    public DefaultImageLoadingListener(Context notApplicationContext, ImageView imageView, View imageViewLoadingProgressBarContainerView, boolean isCircle, int imgWidth, int imgHeight) {
        this.notApplicationContext = notApplicationContext;
        this.imageView = imageView;
        this.imageViewLoadingProgressBarContainerView = imageViewLoadingProgressBarContainerView;
        this.isCircle = isCircle;
        this.imgWidth = imgWidth;
        this.imgHeight = imgHeight;
    }

    public DefaultImageLoadingListener(Context notApplicationContext, ImageView imageView) {
        this.notApplicationContext = notApplicationContext;
        this.imageView = imageView;
    }

    public DefaultImageLoadingListener(Context notApplicationContext, ImageView imageView, boolean isOpenLoadAnimation) {
        this.notApplicationContext = notApplicationContext;
        this.imageView = imageView;
        this.isOpenLoadAnimation = isOpenLoadAnimation;
    }

    public DefaultImageLoadingListener(Context notApplicationContext, ImageView imageView, IImageOnLoadingCompleteListener loadingCompleteListener) {
        this.notApplicationContext = notApplicationContext;
        this.imageView = imageView;
        this.loadingCompleteListener = loadingCompleteListener;
    }

    public DefaultImageLoadingListener(Context notApplicationContext, ImageView imageView, boolean isOpenLoadAnimation, IImageOnLoadingCompleteListener loadingCompleteListener) {
        this.notApplicationContext = notApplicationContext;
        this.imageView = imageView;
        this.isOpenLoadAnimation = isOpenLoadAnimation;
        this.loadingCompleteListener = loadingCompleteListener;
    }

    public DefaultImageLoadingListener(Context notApplicationContext, ImageView imageView, View imageViewLoadingProgressBarContainerView) {
        this.notApplicationContext = notApplicationContext;
        this.imageView = imageView;
        this.imageViewLoadingProgressBarContainerView = imageViewLoadingProgressBarContainerView;
    }

    public DefaultImageLoadingListener(Context notApplicationContext, ImageView imageView, View imageViewLoadingProgressBarContainerView, IImageOnLoadingCompleteListener loadingCompleteListener) {
        this.notApplicationContext = notApplicationContext;
        this.imageView = imageView;
        this.imageViewLoadingProgressBarContainerView = imageViewLoadingProgressBarContainerView;
        this.loadingCompleteListener = loadingCompleteListener;
    }

    public DefaultImageLoadingListener(Context notApplicationContext, ImageView imageView, View imageViewOrnament, View imageViewLoadingProgressBarContainerView) {
        this.notApplicationContext = notApplicationContext;
        this.imageView = imageView;
        this.imageViewOrnament = imageViewOrnament;
        this.imageViewLoadingProgressBarContainerView = imageViewLoadingProgressBarContainerView;
    }

    public DefaultImageLoadingListener(Context notApplicationContext, ImageView imageView, View imageViewOrnament, View imageViewLoadingProgressBarContainerView, IImageOnLoadingCompleteListener loadingCompleteListener) {
        this.notApplicationContext = notApplicationContext;
        this.imageView = imageView;
        this.imageViewOrnament = imageViewOrnament;
        this.imageViewLoadingProgressBarContainerView = imageViewLoadingProgressBarContainerView;
        this.loadingCompleteListener = loadingCompleteListener;
    }

    private Bitmap createCirclePhoto(Bitmap bitmap) {

        float outerRadiusRat = 10;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int srcW = imgWidth;
        int srcH = imgHeight;

        int srcLeft = (width - srcW) / 2;
        int srcRight = srcLeft + srcW;
        int srcTop = (height - srcH) / 2;
        int bottome = height - srcTop;

        Bitmap roundConcerImage = Bitmap.createBitmap(srcW, srcH, Config.ARGB_8888);
        Canvas canvas = new Canvas(roundConcerImage);

        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, srcW, srcH);
        Rect src = new Rect(srcLeft, srcTop, srcRight, bottome);
        RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);

        canvas.drawRoundRect(rectF, outerRadiusRat, outerRadiusRat, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, rect, paint);

        return roundConcerImage;
    }

    @Override
    public void onLoadingStarted(String imageUri, View view) {
        if (null != imageViewLoadingProgressBarContainerView) {
            imageViewLoadingProgressBarContainerView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

        String message = null;
        switch (failReason.getType()) {
            case IO_ERROR:
                message = "Input/Output error";
                break;
            case DECODING_ERROR:
                message = "Image can't be decoded";
                break;
            case NETWORK_DENIED:
                message = "Downloads are denied";
                break;
            case OUT_OF_MEMORY:
                message = "Out Of Memory error";
                break;
            case UNKNOWN:
                message = "Unknown error";
                break;
        }
        //        Toast.makeText(notApplicationContext, message, Toast.LENGTH_SHORT).show();

        if (null != imageViewLoadingProgressBarContainerView) {
            imageViewLoadingProgressBarContainerView.setVisibility(View.GONE);
        }
        if (null != imageView) {
            //      imageView.setImageResource(android.R.drawable.ic_delete);
        }
    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        if (null != loadedImage) {
            if (null != imageViewLoadingProgressBarContainerView) {
                imageViewLoadingProgressBarContainerView.setVisibility(View.GONE);
            }
            if (null != imageView) {
                imageView.setVisibility(View.VISIBLE);
            }

            if (null != imageViewOrnament) {
                imageViewOrnament.setVisibility(View.VISIBLE);
            }

            if (null != loadingCompleteListener && null != imageView) {
                loadingCompleteListener.onLoadingComplete(imageView, loadedImage);
            }

            if (isCircle && imageView != null) {
                imageView.setImageBitmap(createCirclePhoto(loadedImage));
            }
        } // if (null != loadedImage)

        if (isOpenLoadAnimation) {
            Animation anim = AnimationUtils.loadAnimation(notApplicationContext, R.anim.anim_fade_in);
            imageView.setAnimation(anim);
            anim.start();
        }

    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {
        // TODO Auto-generated method stub

    }

    public interface IImageOnLoadingCompleteListener {
        public void onLoadingComplete(ImageView imageView, Bitmap bitmap);
    }

}
