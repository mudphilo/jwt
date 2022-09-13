package com.daride.jwt.model;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("user_id")
    int userID;

    @SerializedName("user_status")
    int userStatus;

    @SerializedName("username")
    String username;

    @SerializedName("role")
    Role role;

    public User(int userID, int userStatus, String username, Role role) {

        this.userID = userID;
        this.userStatus = userStatus;
        this.username = username;
        this.role = role;
    }

    public User(){


    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(int userStatus) {
        this.userStatus = userStatus;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
