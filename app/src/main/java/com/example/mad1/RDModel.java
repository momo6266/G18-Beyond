package com.example.mad1;

public class RDModel {

    private String userID;
    private String nickname;
    private float rating;

    public RDModel() {

    }

    public RDModel(String userID, String nickname, float rating) {
        this.userID = userID;
        this.nickname = nickname;
        this.rating = rating;
    }

    // Getters and setters

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}