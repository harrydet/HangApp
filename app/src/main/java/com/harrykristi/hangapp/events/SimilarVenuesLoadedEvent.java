package com.harrykristi.hangapp.events;


import com.harrykristi.hangapp.model.SimilarVenuesResponse;

public class SimilarVenuesLoadedEvent {
    SimilarVenuesResponse response;

    public SimilarVenuesLoadedEvent(SimilarVenuesResponse response){
        this.response = response;
    }

    public SimilarVenuesResponse getResponse() {
        return response;
    }
}
