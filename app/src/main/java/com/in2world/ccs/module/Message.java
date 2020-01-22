package com.in2world.ccs.module;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Aymen on 08/06/2018.
 */

public class Message {

    @SerializedName("nickname")
    @Expose
    private String nickname;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("UserId")
    @Expose
    private int UserId ;
    @SerializedName("sender")
    @Expose
    private int sender;
    @SerializedName("receiver")
    @Expose
    private int receiver;
    @SerializedName("createdAt")
    @Expose
    private String createdAt ;

    public Message(){

    }


    public Message(String nickname, String message, int userId, int sender, int receiver, String createdAt) {
        this.nickname = nickname;
        this.message = message;
        UserId = userId;
        this.sender = sender;
        this.receiver = receiver;
        this.createdAt = createdAt;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public int getReceiver() {
        return receiver;
    }

    public void setReceiver(int receiver) {
        this.receiver = receiver;
    }

    public long getCreatedAt() {
        return Long.parseLong(createdAt);
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
