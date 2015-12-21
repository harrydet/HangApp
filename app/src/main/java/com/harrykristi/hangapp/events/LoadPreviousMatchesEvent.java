package com.harrykristi.hangapp.events;

public class LoadPreviousMatchesEvent {
    private String userId;

    public String getUserId() {
        return userId;
    }

    public LoadPreviousMatchesEvent(String userId){
        this.userId = userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
