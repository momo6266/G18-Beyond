package com.example.mad1;

public class RDWriteModel {

    private String userID;

    public RDWriteModel() {

    }

    public RDWriteModel(String userID,float rating) {
        this.userID = userID;

    }

    // Getters and setters

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
