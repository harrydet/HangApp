package com.harrykristi.hangapp.Models;

import java.util.List;

/**
 * Created by Harry on 2/17/2016.
 */
public class TipsGroups {
    String type;
    String name;
    int count;
    List<TipVenue> items;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public List<TipVenue> getItems() {
        return items;
    }
}
