package com.harrykristi.hangapp.Models;


public class ResponseUserSearching {
    private boolean error;
    private String message;

    public ResponseUserSearching(boolean error, String message){
        this.error = error;
        this.message = message;
    }

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}
