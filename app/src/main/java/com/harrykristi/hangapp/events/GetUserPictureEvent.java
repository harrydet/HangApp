package com.harrykristi.hangapp.events;


public class GetUserPictureEvent {
    private String objectId;
    private String id;

    public GetUserPictureEvent(String id){
        this.id = id;
    }

    public String getObjectId(){
        return this.objectId;
    }

    public String getId() {
        return id;
    }


}
