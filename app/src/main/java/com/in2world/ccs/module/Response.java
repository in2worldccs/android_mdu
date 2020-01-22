package com.in2world.ccs.module;

import androidx.annotation.IntDef;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Response {

    @SerializedName("result")
    @Expose
    private int result;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("errors")
    @Expose
    private String errors;
    @SerializedName("data")
    @Expose
    private DataResponse dataResponse;


    public void setResult(int result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }

    public DataResponse getDataResponse() {
        return dataResponse;
    }

    public void setDataResponse(DataResponse dataResponse) {
        this.dataResponse = dataResponse;
    }


    public static final int SUCCESS = 1;
    public static final int VALIDATION_FAILED = 0;

    public int getResult() {
        return result == 1 ? SUCCESS : VALIDATION_FAILED;
    }

    public enum StatusConnection {
        SUCCESS,
        CREATED,
        BAD_REQUEST,
        UNAUTHORIZED,
        FORBIDDEN,
        NOT_FOUND,
        UNSUPPORTED_ACTION,
        CONFLICT,
        VALIDATION_FAILED,
        NO_PRIVILEGE,
        NO_CONNECTION,
        INTERNAL_SERVER_ERROR,
        OTHER;
    }

}
