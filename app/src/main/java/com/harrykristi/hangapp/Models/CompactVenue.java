package com.harrykristi.hangapp.Models;


public class CompactVenue {
    private static final long serialVersionUID = -7714811839778109046L;
    private String id;
    private String name;
    private Boolean verified;
    private Location location;
    private Category[] categories;
    private Stats stats;
    private String url;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Boolean getVerified() {
        return verified;
    }

    public Location getLocation() {
        return location;
    }

    public Category[] getCategories() {
        return categories;
    }

    public Stats getStats() {
        return stats;
    }

    public String getUrl() {
        return url;
    }
}
