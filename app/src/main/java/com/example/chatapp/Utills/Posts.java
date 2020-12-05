package com.example.chatapp.Utills;

public class Posts {
    private String datePost,postDesc,postImageUri,userProfileImage,username;

    public Posts() {
    }

    public Posts(String datePost, String postDesc, String postImageUri, String userProfileImage, String username) {
        this.datePost = datePost;
        this.postDesc = postDesc;
        this.postImageUri = postImageUri;
        this.userProfileImage = userProfileImage;
        this.username = username;
    }

    public String getDatePost() {
        return datePost;
    }

    public void setDatePost(String datePost) {
        this.datePost = datePost;
    }

    public String getPostDesc() {
        return postDesc;
    }

    public void setPostDesc(String postDesc) {
        this.postDesc = postDesc;
    }

    public String getPostImageUri() {
        return postImageUri;
    }

    public void setPostImageUri(String postImageUri) {
        this.postImageUri = postImageUri;
    }

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public void setUserProfileImage(String userProfileImage) {
        this.userProfileImage = userProfileImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
