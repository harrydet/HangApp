package com.harrykristi.hangapp.Models;

/**
 * Created by Harry on 2/17/2016.
 */
public class TipVenue {
    String id;
    double createdAt;
    String text;

    UserTipFoursquare user;

    public String getId() {
        return id;
    }

    public double getCreatedAt() {
        return createdAt;
    }

    public String getText() {
        return text;
    }

    public UserTipFoursquare getUser() {
        return user;
    }
}
