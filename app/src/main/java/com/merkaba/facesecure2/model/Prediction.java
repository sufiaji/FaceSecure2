package com.merkaba.facesecure2.model;

import java.util.List;

public class Prediction {

    private float[] encoding;
    private List<Encoding> encodingList;
    private float threshold;
    private float matchDistance;
    private String matchUserId;

    public Prediction(float[] encoding, List<Encoding> encodingList, float threshold) {
        this.encoding = encoding;
        this.encodingList = encodingList;
        this.threshold = threshold;
        matchDistance = 99.99f;
        matchUserId = "";
    }

    public void process() {
        String lastUserId = "";
        float lastDistance = 99.0f;
        for(Encoding encodingMember : encodingList) {
            float distance = 0;
            for(int i=0; i<encoding.length; i++) {
                float delta = encoding[i] - encodingMember.getEncodings()[i];
                float pow = (float) Math.pow(delta, 2);
                distance = distance + pow;
            }
            distance = (float) Math.sqrt(distance);
            if(distance < lastDistance) {
                lastDistance = distance;
                lastUserId = encodingMember.getUserId();
            }
        }
        if(lastDistance < threshold) {
            matchDistance = lastDistance;
            matchUserId = lastUserId;
        }
    }

    public String getMatchUserId() {
        return matchUserId;
    }

    public float getMatchDistance() {
        return matchDistance;
    }
}
