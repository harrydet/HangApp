package com.harrykristi.hangapp.Models;


public class Category {
    private static final long serialVersionUID = -4573082152802069375L;
    private String id;
    private String name;
    private String pluralName;
    private Icon icon;
    private String[] parents;
    private Boolean primary;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPluralName() {
        return pluralName;
    }

    public Icon getIcon() {
        return icon;
    }

    public String[] getParents() {
        return parents;
    }

    public Boolean getPrimary() {
        return primary;
    }
}
