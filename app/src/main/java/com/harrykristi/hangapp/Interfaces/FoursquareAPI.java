package com.harrykristi.hangapp.Interfaces;


import com.harrykristi.hangapp.Models.FoursquareResponse;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface FoursquareAPI {
    @GET("/venues/explore")
    public void fetchVenues(@Query("ll") String latLong,
                                @Query("query") String searchTerms,
                                @Query("venuePhotos") boolean getPhotos,
                                @Query("radius") int radius,
                                @Query("section") String section,
                                @Query("limit") int limit,
                                @Query("offset") int offset,
                                @Query("oauth_token") String oAuth,
                                @Query("v") String v,
                                Callback<FoursquareResponse> callback);

    @GET("/venues/{id}")
    public void fetchSpecificVenue(@Path("id") String id,
                                   @Query("venuePhotos") boolean getPhotos,
                                   @Query("oauth_token") String oAuth,
                                   @Query("v") String v,
                                   Callback<FoursquareResponse> callback);
}
