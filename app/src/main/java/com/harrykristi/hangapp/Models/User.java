package com.harrykristi.hangapp.Models;

import java.util.List;


public class User {
    String object_id;
    private String user_name;
    private String first_name;
    private String last_name;
    private int total_check_ins;
    private int total_matches;
    private String profile_picture_url;
    private List<User> user_matches;

    public User RandomBuilder(){
        User stub = new User();
        stub.setUser_name("testusername");
        stub.setFirst_name("testfirstname");
        stub.setLast_name("testlastname");
        stub.setTotal_check_ins(12);
        stub.setTotal_matches(34);
        for(int i = 0; i< 10; i++){
            stub.getUser_matches().add(stub);
        }

        return stub;

    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public int getTotal_check_ins() {
        return total_check_ins;
    }

    public void setTotal_check_ins(int total_check_ins) {
        this.total_check_ins = total_check_ins;
    }

    public int getTotal_matches() {
        return total_matches;
    }

    public void setTotal_matches(int total_matches) {
        this.total_matches = total_matches;
    }

    public List<User> getUser_matches() {
        return user_matches;
    }

    public void setUser_matches(List<User> user_matches) {
        this.user_matches = user_matches;
    }

    public String getObject_id() {
        return object_id;
    }

    public void setObject_id(String object_id) {
        this.object_id = object_id;
    }
}
