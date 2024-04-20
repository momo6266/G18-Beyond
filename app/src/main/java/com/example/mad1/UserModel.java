package com.example.mad1;

public class UserModel {

    String user_email;
    String user_gender;
    String user_nickname;
    String user_password;
    String user_phone;
    String user_photoURL;
    String user_realname;

    public String getUser_email() {
        return user_email;
    }

    public String getUser_gender() {
        return user_gender;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public String getUser_password() {
        return user_password;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public String getUser_photoURL() {
        return user_photoURL;
    }

    public String getUser_realname() {
        return user_realname;
    }

    public UserModel(String user_email, String user_gender, String user_nickname, String user_password, String user_phone, String user_photoURL, String user_realname) {
        this.user_email = user_email;
        this.user_gender = user_gender;
        this.user_nickname = user_nickname;
        this.user_password = user_password;
        this.user_phone = user_phone;
        this.user_photoURL = user_photoURL;
        this.user_realname = user_realname;
    }

}
