package com.harrykristi.hangapp.events;

public class RegisterUserEvent {
    String object_id;
    String email;
    String first_name;
    String last_name;

    public RegisterUserEvent(String object_id, String email, String first_name, String last_name){
        this.object_id = object_id;
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
    }

    public String getObject_id() {
        return object_id;
    }

    public void setObject_id(String object_id) {
        this.object_id = object_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
}
