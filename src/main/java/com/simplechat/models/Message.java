package com.simplechat.models;

import java.sql.Date;

public class Message {
    private String messageID;
    private final User sender;
    private final User receiver;
    private final String content;
    private Date timestamp;
    private String status;

    public Message(User sender, User receiver, String content){
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
    }

    public final String getMessageID(){
        return messageID;
    }
    public final User getSender(){
        return sender;
    }
    public final User getReceiver(){
        return receiver;
    }
    public final String getContent(){
        return content;
    }
}
