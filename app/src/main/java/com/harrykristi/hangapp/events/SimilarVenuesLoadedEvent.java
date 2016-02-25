package com.harrykristi.hangapp.events;


import com.harrykristi.hangapp.Models.SimilarVenuesResponse;

public class SimilarVenuesLoadedEvent {
    SimilarVenuesResponse response;

    public SimilarVenuesLoadedEvent(SimilarVenuesResponse response){
        this.response = response;
    }

    public SimilarVenuesResponse getResponse() {
        return response;
    }
}
