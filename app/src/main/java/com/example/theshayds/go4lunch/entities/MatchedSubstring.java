package com.example.theshayds.go4lunch.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MatchedSubstring implements Serializable {

    @SerializedName("length")
    private int length;

    @SerializedName("offset")
    private int offset;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
