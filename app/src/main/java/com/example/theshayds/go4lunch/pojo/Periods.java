package com.example.theshayds.go4lunch.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Periods {

    @SerializedName("close")
    @Expose
    private Close close;

    @SerializedName("open")
    @Expose
    private Open open;

    public Close getClose ()
    {
        return close;
    }

    public void setClose (Close close)
    {
        this.close = close;
    }

    public Open getOpen ()
    {
        return open;
    }

    public void setOpen (Open open)
    {
        this.open = open;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [close = "+close+", open = "+open+"]";
    }
}
