package com.harrykristi.hangapp.Interfaces;

public class EndPoints {

    // Server url
    public static final String BASE_URL = "http://178.62.117.251/rest_endpoints/v1";
    public static final String USER = BASE_URL + "/users/_ID_";
    public static final String USER_GCM = BASE_URL + "/users/registration_id/_ID_";
    public static final String USER_LOGIN = BASE_URL + "/users/login";
    public static final String CHAT_ROOMS = BASE_URL + "/chat_rooms";
    public static final String CHAT_THREAD = BASE_URL + "/chat_rooms/_ID_";
    public static final String CHAT_ROOM_MESSAGE = BASE_URL + "/chat_rooms/_ID_/message";
}
