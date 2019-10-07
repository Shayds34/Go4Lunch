package com.example.theshayds.go4lunch.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class Open {

    @SerializedName("time")
    @Expose
    private String time;

    @SerializedName("day")
    @Expose
    private String day;


    public String getTime ()
    {
        return time;
    }

    public void setTime (String time)
    {
        this.time = time;
    }

    public String getDay ()
    {
        return day;
    }

    public void setDay (String day)
    {
        this.day = day;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [time = "+time+", day = "+day+"]";
    }
}
