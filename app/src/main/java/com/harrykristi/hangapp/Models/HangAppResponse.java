package com.harrykristi.hangapp.Models;


import com.harrykristi.hangapp.Models.User;

import java.util.List;

public class HangAppResponse {
    String error;
    String message;
    List<User> matches;

    public List<User> getMatches(){
        return matches;
    }
}
