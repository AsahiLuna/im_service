package com.asahiluna.imdemo.entity;

public class Ok {
    String message = "ok";

    public static Ok newOk() {
        return new Ok();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
