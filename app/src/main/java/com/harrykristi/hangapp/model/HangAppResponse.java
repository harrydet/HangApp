package com.harrykristi.hangapp.model;


import java.util.List;

public class HangAppResponse {
    String error;
    String message;
    List<User> matches;

    public List<User> getMatches(){
        return matches;
    }
}
