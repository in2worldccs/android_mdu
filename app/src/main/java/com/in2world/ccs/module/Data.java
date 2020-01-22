package com.in2world.ccs.module;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.in2world.ccs.helper.ValidationHelper;

public class Data {


    @SerializedName("messageType")
    @Expose
    private String messageType;
    @SerializedName("message")
    @Expose
    private Message message;
    @SerializedName("userdata")
    @Expose
    private Auth auth;

    public Data(String messageType,Message message) {
        this.message = message;
        this.messageType = messageType;
    }
    public Data(String messageType,Auth auth) {
        this.auth = auth;
        this.messageType = messageType;
    }
    public String getMessageType() {
        return (ValidationHelper.validObject(messageType) ? messageType:"null");
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }


    public Message getMessage() {
        return (ValidationHelper.validObject(message) ?message:new Message());
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Auth getAuth() {
        return (ValidationHelper.validObject(auth) ?auth:new Auth());
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }
}
