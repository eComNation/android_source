package com.ecomnationmobile.library.Data;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;
/**
 * Created by User on 5/25/2016.
 */

/**
 * Transformate the loaded image to avoid OutOfMemoryException
 */
public class BitmapTransform implements Transformation {

    public static final int MAX_IMAGE_WIDTH = 1080;
    public static final int MAX_IMAGE_HEIGHT = 1080;


    public BitmapTransform() {
    }

    @Override
    public Bitmap transform(Bitmap source) {
        if (source == null)
            return null;

        Bitmap result = null;
        boolean is_scaled = false;
        int targetWidth = source.getWidth(), targetHeight = source.getHeight();

        double ratio = (double) targetWidth / (double) targetHeight;

        if (targetWidth > MAX_IMAGE_WIDTH) {
            targetWidth = MAX_IMAGE_WIDTH;
            targetHeight = (int) (MAX_IMAGE_WIDTH / ratio);
            is_scaled = true;
        } else if (targetHeight > MAX_IMAGE_HEIGHT) {
            targetHeight = MAX_IMAGE_HEIGHT;
            targetWidth = (int) (MAX_IMAGE_HEIGHT * ratio);
            is_scaled = true;
        }

        if (is_scaled) {
            try {
                result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                if (result != source) {
                    source.recycle();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        } else
            return source;
    }

    @Override
    public String key() {
        return MAX_IMAGE_WIDTH + "x" + MAX_IMAGE_HEIGHT;
    }

    public static int getSize() {
        return (int) Math.ceil(Math.sqrt(MAX_IMAGE_WIDTH * MAX_IMAGE_HEIGHT));
    }

}