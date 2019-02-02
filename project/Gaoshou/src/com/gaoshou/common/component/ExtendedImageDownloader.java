package com.gaoshou.common.component;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

import com.gaoshou.common.utils.Validator;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

public class ExtendedImageDownloader extends BaseImageDownloader {

    public ExtendedImageDownloader(Context context) {
        super(context);
    }

    public ExtendedImageDownloader(Context context, int connectTimeout, int readTimeout) {
        super(context, connectTimeout, readTimeout);
    }

    @Override
    protected InputStream getStreamFromOtherSource(String imageUri, Object extra) throws IOException {

        if (Validator.isLocalFilePathValid(imageUri)) {
            return new BufferedInputStream(new FileInputStream(imageUri), BUFFER_SIZE);
        } // if (Validator.isLocalFilePathValid(imageUri))

        return super.getStreamFromOtherSource(imageUri, extra);
    }
}
