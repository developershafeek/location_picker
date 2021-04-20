package com.farnek.locationpicker.model.LoginResponse;



import com.farnek.locationpicker.model.UserData;

import java.io.Serializable;

public class LoginResult implements Serializable {
    private UserData user_details;

    public UserData getUser_details() {
        return user_details;
    }

    public void setUser_details(UserData user_details) {
        this.user_details = user_details;
    }
}
