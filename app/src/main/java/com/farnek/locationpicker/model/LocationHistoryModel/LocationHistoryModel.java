package com.farnek.locationpicker.model.LocationHistoryModel;

import com.farnek.locationpicker.model.GeneralResponse;

import java.io.Serializable;

    public class LocationHistoryModel implements Serializable {
        private int code;
        private GeneralResponse response;
        private LocationHistoryResult result;

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

        public LocationHistoryResult getResult() {
            return result;
        }

        public void setResult(LocationHistoryResult result) {
            this.result = result;
        }
    }


