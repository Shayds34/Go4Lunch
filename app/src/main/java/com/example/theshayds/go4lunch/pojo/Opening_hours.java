package com.example.theshayds.go4lunch.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Opening_hours {

     @SerializedName("open_now")
     @Expose
     private boolean open_now;

     @SerializedName("periods")
     @Expose
     private Periods[] periods;

     @SerializedName("weekday_text")
     @Expose
     private String[] weekday_text;

    public boolean getOpen_now ()
    {
        return open_now;
    }

    public void setOpen_now (boolean open_now)
    {
        this.open_now = open_now;
    }

    public Periods[] getPeriods ()
    {
        return periods;
    }

    public void setPeriods (Periods[] periods)
    {
        this.periods = periods;
    }

    public String[] getWeekday_text ()
    {
        return weekday_text;
    }

    public void setWeekday_text (String[] weekday_text)
    {
        this.weekday_text = weekday_text;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [open_now = "+open_now+", periods = "+periods+", weekday_text = "+weekday_text+"]";
    }
}
