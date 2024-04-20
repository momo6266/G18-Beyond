package com.example.mad1;

public class ReviewModel {
    private String distance;
    private String price;
    private String dateTime;
    private String location;

    private String bookingID;

    private String userID;


    public ReviewModel() {

    }


    public ReviewModel(String distance, String price, String dateTime, String location,String bookingID,String userID) {
        this.distance = distance;
        this.price = price;
        this.dateTime = dateTime;
        this.location = location;
        this.bookingID = bookingID;
        this.userID = userID;
    }

    // Getters and setters

    public String getBookingId() {
        return bookingID;
    }

    public void setBookingId(String bookingId) {
        this.bookingID = bookingId;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
