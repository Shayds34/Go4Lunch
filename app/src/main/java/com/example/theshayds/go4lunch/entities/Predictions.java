package com.example.theshayds.go4lunch.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Predictions implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("predictions")
    private List<Prediction> predictions;

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Prediction> getPredictions() {
        return this.predictions;
    }

    public void setPredictions(List<Prediction> predictions) {
        this.predictions = predictions;
    }
}
