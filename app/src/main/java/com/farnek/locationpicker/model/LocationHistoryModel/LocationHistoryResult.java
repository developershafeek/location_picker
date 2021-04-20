package com.farnek.locationpicker.model.LocationHistoryModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LocationHistoryResult {
    @SerializedName("datelist")
    @Expose
    private List<DateModel> datelist = null;

    public List<DateModel> getDatelist() {
        return datelist;
    }

    public void setDatelist(List<DateModel> datelist) {
        this.datelist = datelist;
    }

}
