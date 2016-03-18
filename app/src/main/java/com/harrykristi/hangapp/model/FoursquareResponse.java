package com.harrykristi.hangapp.model;

import java.util.List;

public class FoursquareResponse {
    Meta meta;
    List<Notification> notifications;

    public Response getResponse() {
        return response;
    }

    Response response;
}

class Meta {
    int code;
    String requestedId;
}

class Notification {
    String type;
}


