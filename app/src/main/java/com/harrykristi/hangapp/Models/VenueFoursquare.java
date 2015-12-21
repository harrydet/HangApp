package com.harrykristi.hangapp.Models;

public class VenueFoursquare {
    String id;

    public String getName() {
        return name;
    }
    public String getPhoto(String size){
        return photos.getPhotoPrefix() + size + photos.getPhotoSuffix();
    }

    String name;
    Stats stats;
    Photos photos;
    float rating;

    public int getCurrentCheckins(){
        return stats.getCurrentCheckins();
    }

    public float getRating() {
        return rating/2;
    }
}
