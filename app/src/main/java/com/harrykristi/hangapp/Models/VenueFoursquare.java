package com.harrykristi.hangapp.Models;

import java.util.Objects;

import fi.foyt.foursquare.api.entities.CompleteTip;
import fi.foyt.foursquare.api.entities.HereNow;
import fi.foyt.foursquare.api.entities.TipGroup;
import fi.foyt.foursquare.api.entities.Tips;

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

    Tips tips;

    public Stats getStats(){return stats;}

    public long getCurrentCheckins(){
        return hereNow.getCount();
    }

    public CompleteTip[] getTips(){
        TipGroup[] groups = tips.getGroups();
        for (TipGroup group : groups) {
            if (Objects.equals(group.getType(), "others")) {
                return group.getItems();
            }
        }
        return null;
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
