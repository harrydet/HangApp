package com.harrykristi.hangapp.Interfaces;

import com.harrykristi.hangapp.model.ResponseUserSearching;
import com.harrykristi.hangapp.model.UserProfileResponse;
import com.harrykristi.hangapp.model.HangAppResponse;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface HangAppAPI {
    @GET("/users/match_history/{id}")
    public void fetchPreviousMatches(@Path("id") String id,
                                     @Query("venueId") String venueId,
                                     Callback<UserProfileResponse> callback);

    @FormUrlEncoded
    @POST("/registerUser")
    public void registerUser(@Field("object_id") String object_id,
                                    @Field("email") String email,
                                    @Field("first_name") String first_name,
                                    @Field("last_name") String last_name,
                                    Callback<HangAppResponse> callback);
    @GET("/users/{object_id}")
    public void fetchUserWithParams(@Path("object_id") String object_id,
                                    @Query("profilePictureUrl") boolean profilePicture,
                                    Callback<UserProfileResponse> callback);

    @FormUrlEncoded
    @POST("/startSearching")
    public void startSearching(@Field("object_id") String object_id,
                               @Field("venue_id") String venue_id,
                               Callback<ResponseUserSearching> callback);

}
