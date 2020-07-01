package com.merkaba.facesecure2.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.loopj.android.http.Base64;
import com.merkaba.facesecure2.model.Encoding;

import java.io.ByteArrayOutputStream;
import java.util.List;

public final class Utils {

    public static final int IMG_MAX_WIDTH = 64;

    private Utils() {}

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream baosthumb = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baosthumb);
        byte[] byteArray = baosthumb.toByteArray();
        return byteArray;
    }

    public static Bitmap getBitmapFromByteArray(byte[] blob) {
        Bitmap bm = BitmapFactory.decodeByteArray(blob, 0 , blob.length);
        return bm;
    }

    public static String getByteArrayAsString64(byte[] byteArray) {
        if(byteArray!=null && byteArray.length > 0)
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        else
            return "";
    }

    public static Bitmap scaleImageKeepAspectRatio(Bitmap originalBitmap, int newWidth)
    {
        int imageWidth = originalBitmap.getWidth();
        int imageHeight = originalBitmap.getHeight();
        int newHeight = (imageHeight * newWidth)/imageWidth;
        return Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, false);
    }

    public static int[] toIntArray(List<Integer> list) {
        int[] ret = new int[list.size()];
        for(int i = 0;i < ret.length;i++)
            ret[i] = list.get(i);
        return ret;
    }

    public static String[] toStringArray(List<String> list) {
        String[] ret = new String[list.size()];
        for(int i = 0;i < ret.length;i++)
            ret[i] = list.get(i);
        return ret;
    }

    public static long convertHoursToMillis(int hours) {
        return (hours * 60 * 60 * 1000);
    }
}
