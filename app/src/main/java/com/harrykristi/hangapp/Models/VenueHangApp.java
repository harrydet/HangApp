package com.harrykristi.hangapp.Models;

public class VenueHangApp {

    String venue_id;
    String name;
    String picture_first_comp;
    String picture_second_comp;

    public String getVenueId(){
        return venue_id;
    }

    public String getName(){
        return name;
    }

    public String getPhotoUrl(String resolution){
        return picture_first_comp + resolution + picture_second_comp;
    }
}
