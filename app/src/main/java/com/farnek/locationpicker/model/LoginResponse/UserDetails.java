
package com.farnek.locationpicker.model.LoginResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class UserDetails {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("device_id")
    @Expose
    private String deviceId;
    @SerializedName("frequency")
    @Expose
    private String frequency;
    @SerializedName("lastupdate")
    @Expose
    private String lastupdate;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getLastupdate() {
        return lastupdate;
    }

    public void setLastupdate(String lastupdate) {
        this.lastupdate = lastupdate;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
