package com.simplechat.models;

public class User {
    private String userID;
    private final String username;
    private final String email;
    private final String password;
    private String status;

    public User(String username, String email, String password){
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public final String getUserID(){
        return userID;
    }

    public final String getUsername(){
        return username;
    }

    public final String getEmail(){
        return email;
    }

    public final String getPassword(){
        return password;
    }

    public final String getStatus(){
        return status;
    }
}
