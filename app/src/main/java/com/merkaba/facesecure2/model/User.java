package com.merkaba.facesecure2.model;

import android.graphics.Bitmap;

import com.merkaba.facesecure2.utils.Utils;

public class User {

    private String userId;
    private String name;
    private long createdAt;
    private String isLocal = "";
    private String division = "";
    private byte[] blobCrop;
    private byte[] blobFull;
    private Bitmap crop;
    private Bitmap full;

    public User(String userId, String name, String division, long createdAt, String isLocal, Bitmap cropped, Bitmap full) {
        this.userId = userId;
        this.name = name;
        this.createdAt = createdAt;
        this.isLocal = isLocal;
        this.division = division;

        this.crop = cropped;
        blobCrop = Utils.getBitmapAsByteArray(cropped);

        this.full = full;
        Bitmap bitmapRescale = Utils.scaleImageKeepAspectRatio(full, Utils.USER_IMAGE_MAX_WIDTH);
        blobFull = Utils.getBitmapAsByteArray(bitmapRescale);

    }

    public String getIsLocal() {
        return isLocal;
    }

    public void setIsLocal(String isLocal) {
        this.isLocal = isLocal;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(int createdAt) {
        this.createdAt = createdAt;
    }

    public byte[] getBlobCrop() {
        return blobCrop;
    }

    public byte[] getBlobFull() {
        return blobFull;
    }

}
