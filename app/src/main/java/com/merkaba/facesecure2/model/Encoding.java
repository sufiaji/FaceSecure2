package com.merkaba.facesecure2.model;

import com.merkaba.facesecure2.activity.MainActivity;

public class Encoding {

    private String userId;
    private String encodingArray;

    public Encoding(String userId, String encodingArray) {
        this.userId = userId;
        this.encodingArray = encodingArray;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEncodingArray() {
        return encodingArray;
    }

    public void setEncodingArray(String encodingArray) {
        this.encodingArray = encodingArray;
    }

    public float[] getEncodings() {
        float[] encodings = new float[MainActivity.ENCODING_LENGTH];
        String[] encodingSplit = encodingArray.split(",");
        for(int i=0; i<encodingSplit.length; i++) {
            encodings[i] = Float.parseFloat(encodingSplit[i]);
        }
        return encodings;
    }

}
