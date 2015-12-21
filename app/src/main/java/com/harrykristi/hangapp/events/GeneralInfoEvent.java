package com.harrykristi.hangapp.events;

public class GeneralInfoEvent {
    boolean error;
    String message;

    public GeneralInfoEvent(boolean error, String message){
        this.error = error;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
