package com.harrykristi.hangapp.Models;

import fi.foyt.foursquare.api.entities.HereNow;

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
    Location location;

    HereNow hereNow;

    public Stats getStats(){return stats;}

    public long getCurrentCheckins(){
        return hereNow.getCount();
    }

    public float getRating() {
        return rating/2;
    }

    public String getId() {
        return id;
    }

    public int getTotalPhotos(){
        return photos.getTotalPhotos();
    }

    public String getPhotoPrefix(int index){
        return photos.getPhotoPrefix(index);
    }

    public String getPhotoSuffix(int index){
        return photos.getPhotoSuffix(index);
    }

    public Location getLocation(){
        return location;
    }
}
