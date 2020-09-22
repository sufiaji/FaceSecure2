package com.merkaba.facesecure2.model;

import android.graphics.Bitmap;

import com.merkaba.facesecure2.utils.DateUtils;
import com.merkaba.facesecure2.utils.Utils;

public class Attendance {

    private String userId;
    private long createdDateTime;
    private String createdDateTimeFormatted;
    private String createdAt;
    private String createdOn;
    private String status;
    private String location;
    private String unSent;
    private int id;
    private byte[] blobImage;
//    private String base64Image;

    public Attendance(int id, String userId, long createdDateTime, String status, String location, String unSent) {
        // is being used to download last attendance data from server, so need to download image
        this.id = id;
        this.userId = userId;
        this.createdDateTime = createdDateTime;
        this.createdDateTimeFormatted = new DateUtils("-").epochToDateString(createdDateTime);
        String[] dateTime = this.createdDateTimeFormatted.split(DateUtils.SEPARATOR);
        this.createdAt = dateTime[0];
        this.createdOn = dateTime[1];
        this.status = status;
        this.location = location;
        this.unSent = unSent;
    }

    public Attendance(String userId, long createdDateTime, String status, String location, Bitmap bitmap, String unSent) {
        // is being used to create attendance to save into local sqlite anyway
        this.userId = userId;
        this.createdDateTime = createdDateTime;
        this.createdDateTimeFormatted = new DateUtils("-").epochToDateString(createdDateTime); //"dd-MM-yyyy HH:mm:ss.SSSSSS";
        String[] dateTime = this.createdDateTimeFormatted.split(DateUtils.SEPARATOR);
        this.createdAt = dateTime[0];
        this.createdOn = dateTime[1];
        this.status = status;
        this.location = location;
        Bitmap bitmapRescale = Utils.scaleImageKeepAspectRatio(bitmap, Utils.ATTENDANCE_THUMB_MAX_WIDTH);
        this.blobImage = Utils.getBitmapAsByteArray(bitmapRescale);
//        this.base64Image = Utils.getByteArrayAsString64(this.blobImage);
        this.unSent = unSent;
    }

    public Attendance(int id, String userId, long createdDateTime, String status, String location, byte[] bitmapBlob, String unSent) {
        // is being used to upload attendance data to server
        /**
         * THIS!!!
         */
        this.id = id;
        this.userId = userId;
        this.createdDateTime = createdDateTime;
        this.createdDateTimeFormatted = new DateUtils("-").epochToDateString(createdDateTime); //"dd-MM-yyyy HH:mm:ss.SSSSSS";
        String[] dateTime = this.createdDateTimeFormatted.split(DateUtils.SEPARATOR);
        this.createdAt = dateTime[0];
        this.createdOn = dateTime[1];
        this.status = status;
        this.location = location;
//        this.blobImage = bitmapBlob;
//        this.base64Image = Utils.getByteArrayAsString64(this.blobImage);
        Bitmap bitmap = Utils.getBitmapFromByteArray(bitmapBlob);
        Bitmap bitmapRescale = Utils.scaleImageKeepAspectRatio(bitmap, Utils.ATTENDANCE_THUMB_MAX_WIDTH);
        byte[] bitmapByte = Utils.getBitmapAsByteArray(bitmapRescale);
//        this.base64Image = Utils.getByteArrayAsString64(bitmapByte);
        this.unSent = unSent;
    }

    public byte[] getBlobImage() {
        return blobImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public long getEpochDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(long createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public String getCreatedDateTimeFormatted() {
        // "dd-MM-yyyy HH:mm:ss.SSSSSS";
        return createdDateTimeFormatted;
    }

    public String getCreatedAt() {
        // "dd-MM-yyyy
        return createdAt;
    }

    public String getCreatedOn() {
        // HH:mm:ss.SSSSSS";
        return createdOn;
    }

    public String getStatus() {
        return status;
    }

    public String getLocation() {
        return location;
    }

    public String getUnSent() {
        return unSent;
    }

}
