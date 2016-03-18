package com.harrykristi.hangapp.model;

import java.util.List;

import fi.foyt.foursquare.api.entities.HereNow;

public class Response {
    public List<Group> getGroups(){
        return groups;
    }

    public String getVenueNameAt(int position){
        return groups.get(0).getVenueAt(position).getName();
    }

    public long totalCheckinsAt(int position){
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

    public VenueFoursquare getVenue(){
        return venue;
    }

    List<Group> groups;
    VenueFoursquare venue;
    VenueGroup similarVenues;

    public VenueGroup getSimilarVenues() {
        return similarVenues;
    }

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

    public String getType() {
        return type;
    }
}

class Item {
    public VenueFoursquare getVenueFoursquare(){
        return venue;
    }

    Reason reasons;
    VenueFoursquare venue;
    List<TipVenue> tips;
    String referralId;

    public List<TipVenue> getTips(){
        return tips;
    }
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

    HereNow hereNow;

    public int getTotalTips() {
        return totalTips;
    }

    public int getTotalCheckins() {
        return totalCheckins;
    }

    public HereNow getHereNow(){return hereNow;}

    int currentCheckins;
    int totalTips;
}

class Photos {
    int count;
    List<PhotoGroup> groups;

    public String getPhotoPrefix(int index){
        return groups.get(0).getPhotoPrefix(index);
    }

    public String getPhotoSuffix(int index){
        return groups.get(0).getPhotoSuffix(index);
    }

    public String getPhotoPrefix(){
        return groups.get(0).getPhotoPrefix(0);
    }

    public String getPhotoSuffix(){
        return groups.get(0).getPhotoSuffix(0);
    }

    public int getTotalPhotos(){
        if(groups != null)
            return groups.get(0).getTotalPhotos();
        return 0;
    }

}

class PhotoGroup{
    List<PhotoItem> items;
    public String getPhotoPrefix(int index){
        return items.get(index).getPrefix();
    }

    public String getPhotoSuffix(int index){
        return items.get(index).getSuffix();
    }

    public int getTotalPhotos(){
        return items.size();
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

class TipGroup{
    List<Group> groups;

    public Group getGroup(){
        for (Group group :
                groups) {
            if (group.getType() == "others") return group;
        }

        return null;
    }
}