package com.farnek.locationpicker.model.LoginResponse;



import com.farnek.locationpicker.model.GeneralResponse;

import java.io.Serializable;

public class LoginResponse implements Serializable {
    private int code;
    private GeneralResponse response;
    private LoginResult result;

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

    public LoginResult getResult() {
        return result;
    }

    public void setResult(LoginResult result) {
        this.result = result;
    }
}
