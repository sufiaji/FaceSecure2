package com.merkaba.facesecure2.model;

public class User {

    private String userId;
    private String name;
    private long createdAt;
    private String isLocal = " ";

    public User(String userId, String name, long createdAt, String isLocal) {
        this.userId = userId;
        this.name = name;
        this.createdAt = createdAt;
        this.isLocal = isLocal;
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

}
