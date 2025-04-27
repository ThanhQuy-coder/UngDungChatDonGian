package com.simplechat.models;

public class ChatSession {
    private String sessionID;
    private final User user1;
    private final User user2;

    public ChatSession(User user1, User user2){
        this.user1 = user1;
        this.user2 = user2;
    }

    public final User getUser1(){
        return user1;
    }

    public final User getUser2(){
        return user2;
    }
}
