package com.example.instagram.model;

import com.google.firebase.database.ServerValue;

public class Post {
    private String postKey;
    private String title;
    private String description;
    private String image;
    private String profile_image;
    private String userId;
    private Object timeStamp;

    public Post() {
    }

    public Post(String title, String description, String image, String profile_image, String userId) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.profile_image = profile_image;
        this.userId = userId;
        this.timeStamp = ServerValue.TIMESTAMP;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }
}
