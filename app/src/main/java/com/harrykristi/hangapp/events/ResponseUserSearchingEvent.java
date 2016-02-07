package com.harrykristi.hangapp.events;


public class ResponseUserSearchingEvent {
    private boolean error;
    private String message;

    public ResponseUserSearchingEvent(boolean error, String message){
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
