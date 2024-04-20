package com.example.mad1;

public class BookingModel {
    private String bookingId;
    private String booking_capacity;
    private String booking_datetime;
    private String booking_distance;
    private String booking_endlocation;
    private String booking_leader;
    private String booking_driver;
    private String booking_startlocation;
    private String booking_status;
    private String booking_ttlprice;
    private String booking_user_price;
    private Boolean review_status;
    private String driver_nickname;
    private String leader_nickname;

    // Default constructor required for Firebase deserialization
    public BookingModel() {}

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingCapacity() {
        return booking_capacity;
    }

    public void setBookingCapacity(String booking_capacity) {
        this.booking_capacity = booking_capacity;
    }

    public String getBookingDatetime() {
        return booking_datetime;
    }

    public void setBookingDatetime(String booking_datetime) {
        this.booking_datetime = booking_datetime;
    }

    public String getBookingDistance() {
        return booking_distance;
    }

    public void setBookingDistance(String booking_distance) {
        this.booking_distance = booking_distance;
    }

    public String getBookingEndlocation() {
        return booking_endlocation;
    }

    public void setBookingEndlocation(String booking_endlocation) {
        this.booking_endlocation = booking_endlocation;
    }

    public String getBookingLeader() {
        return booking_leader;
    }

    public void setBookingLeader(String booking_leader) {
        this.booking_leader = booking_leader;
    }

    public String getBookingDriver() {
        return booking_driver;
    }

    public void setBookingDriver(String booking_driver) {
        this.booking_driver = booking_driver;
    }

    public String getBookingStartlocation() {
        return booking_startlocation;
    }

    public void setBookingStartlocation(String booking_startlocation) {
        this.booking_startlocation = booking_startlocation;
    }

    public String getBookingStatus() {
        return booking_status;
    }

    public void setBookingStatus(String booking_status) {
        this.booking_status = booking_status;
    }

    public String getBookingTtlprice() {
        return booking_ttlprice;
    }

    public void setBookingTtlprice(String booking_ttlprice) {
        this.booking_ttlprice = booking_ttlprice;
    }

    public String getBookingUserPrice() { return booking_user_price; }

    public void setBookingUserPrice(String booking_user_price) {
        this.booking_user_price = booking_user_price;
    }

    public Boolean getReviewStatus() {
        return review_status;
    }

    public void setReviewStatus(Boolean review_status) {
        this.review_status = review_status;
    }

    public String getDriverNickname() { return driver_nickname; }

    public void setDriverNickname(String driver_nickname) {
        this.driver_nickname = driver_nickname;
    }

    public String getLeaderNickname() { return leader_nickname; }

    public void setLeaderNickname(String leader_nickname) {
        this.leader_nickname = leader_nickname;
    }
}
