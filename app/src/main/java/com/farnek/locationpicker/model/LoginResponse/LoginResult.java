package com.farnek.locationpicker.model.LoginResponse;



import java.io.Serializable;

public class LoginResult implements Serializable {
    private UserDetails user_details;

    public UserDetails getUser_details() {
        return user_details;
    }

    public void setUser_details(UserDetails user_details) {
        this.user_details = user_details;
    }
}
