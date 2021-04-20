package com.farnek.locationpicker.model;

import java.io.Serializable;

public class CommonResponse implements Serializable {
    private int code;
    private GeneralResponse response;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public GeneralResponse getResponse() {
        return response;
    }

    public void setResponse(GeneralResponse response) {
        this.response = response;
    }
}
