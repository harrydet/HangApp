package com.harrykristi.hangapp.Models;

import java.util.List;

public class Response {
    public List<Group> getGroups(){
        return groups;
    }

    public String getVenueNameAt(int position){
        return groups.get(0).getVenueAt(position).getName();
    }

    public int totalCheckinsAt(int position){
        return groups.get(0).getVenueAt(position).getCurrentCheckins();
    }

    public String getPhotoAtVenue(int position, String size){
        return groups.get(0).getVenueAt(position).getPhoto(size);
    }

    public int getTotalVenues(){
        return groups.get(0).getTotalItems();
    }

    public float getVenueRatingAt(int position){
        return groups.get(0).getVenueAt(position).getRating();
    }

    public VenueFoursquare getVenueAt(int position){
        return groups.get(0).getVenueAt(position);
    }

    List<Group> groups;

    public boolean extend(Response responseExtension) {
        groups.get(0).extendItems(responseExtension.getGroups().get(0).getItems());
        return responseExtension.getTotalVenues() == 10;
    }
}

class ResponseItems {
    List<Group> groups;
}

class Group {
    public VenueFoursquare getVenueAt(int position){
        return items.get(position).getVenueFoursquare();
    }

    public int getTotalItems(){
        return items.size();
    }

    public void extendItems(List<Item> itemExtension){
        items.addAll(itemExtension);
    }

    public List<Item> getItems(){
        return items;
    }

    String type;
    String name;
    List<Item> items;
}

class Item {
    public VenueFoursquare getVenueFoursquare(){
        return venue;
    }

    Reason reasons;
    VenueFoursquare venue;
    List<Tip> tips;
    String referralId;
}

class Reason {
    int count;
}

class Tip {
    String id;
    double createdAt;
    String text;
}

class Stats{
    int totalCheckins;

    public int getCurrentCheckins() {
        return currentCheckins;
    }

    public int getTotalTips() {
        return totalTips;
    }

    public int getTotalCheckins() {
        return totalCheckins;
    }

    int currentCheckins;
    int totalTips;
}

class Photos {
    int count;
    List<PhotoGroup> groups;

    public String getPhotoPrefix(){
        return groups.get(0).getPhotoPrefix();
    }

    public String getPhotoSuffix(){
        return groups.get(0).getPhotoSuffix();
    }

}

class PhotoGroup{
    List<PhotoItem> items;
    public String getPhotoPrefix(){
        return items.get(0).getPrefix();
    }

    public String getPhotoSuffix(){
        return items.get(0).getSuffix();
    }
}

class PhotoItem{
    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    String prefix;
    String suffix;
}