package com.farnek.locationpicker.model;

import java.io.Serializable;

public class UserData implements Serializable {
    private String device_id;
    private String name;

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
