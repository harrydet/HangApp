package com.harrykristi.hangapp.events;


public class GetUserPictureEvent {
    private String objectId;

    public GetUserPictureEvent(String objectId){
        this.objectId = objectId;
    }

    public String getObjectId(){
        return this.objectId;
    }
}
