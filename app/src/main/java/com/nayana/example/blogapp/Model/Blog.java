package com.nayana.example.blogapp.Model;

public class Blog {

    public String postImage;
    public String postTitle;
    public String postDescription;
    public String userID;
    public String timestamp;

    public Blog(){
    }

    public Blog(String postImage, String postTitle, String postDescription, String timestamp, String userID) {
        this.postImage = postImage;
        this.postTitle = postTitle;
        this.postDescription = postDescription;
        this.userID = userID;
        this.timestamp = timestamp;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
