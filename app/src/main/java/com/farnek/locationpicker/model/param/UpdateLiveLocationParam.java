package com.farnek.locationpicker.model.param;

public class UpdateLiveLocationParam {
    private String device_id;
    private String latitude;
    private String longitude;


    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
