package com.example.theshayds.go4lunch.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("utc_offset")
    @Expose
    private String utc_offset;
    @SerializedName("formatted_address")
    @Expose
    private String formatted_address;
    @SerializedName("types")
    @Expose
    private String[] types;
    @SerializedName("website")
    @Expose
    private String website;
    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("rating")
    @Expose
    private String rating;
    @SerializedName("address_components")
    @Expose
    private AddressComponents[] address_components;
    @SerializedName("photos")
    @Expose
    private Photo[] photos;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("reference")
    @Expose
    private String reference;
    @SerializedName("user_ratings_total")
    @Expose
    private String user_ratings_total;
    @SerializedName("reviews")
    @Expose
    private Reviews[] reviews;
    @SerializedName("prices_level")
    @Expose
    private String price_level;
    @SerializedName("scope")
    @Expose
    private String scope;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("opening_hours")
    @Expose
    private Opening_hours opening_hours;
    @SerializedName("geometry")
    @Expose
    private Geometry geometry;
    @SerializedName("vicinity")
    @Expose
    private String vicinity;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("adr_address")
    @Expose
    private String adr_address;
    @SerializedName("PlusCode")
    @Expose
    private PlusCode plusCode;
    @SerializedName("formatted_phone_number")
    @Expose
    private String formatted_phone_number;
    @SerializedName("international_phone_number")
    @Expose
    private String international_phone_number;
    @SerializedName("place_id")
    @Expose
    private String place_id;

    public String getUtc_offset ()
    {
        return utc_offset;
    }

    public void setUtc_offset (String utc_offset)
    {
        this.utc_offset = utc_offset;
    }

    public String getFormatted_address ()
    {
        return formatted_address;
    }

    public void setFormatted_address (String formatted_address)
    {
        this.formatted_address = formatted_address;
    }

    public String[] getTypes ()
    {
        return types;
    }

    public void setTypes (String[] types)
    {
        this.types = types;
    }

    public String getWebsite ()
    {
        return website;
    }

    public void setWebsite (String website)
    {
        this.website = website;
    }

    public String getIcon ()
    {
        return icon;
    }

    public void setIcon (String icon)
    {
        this.icon = icon;
    }

    public String getRating ()
    {
        return rating;
    }

    public void setRating (String rating)
    {
        this.rating = rating;
    }

    public AddressComponents[] getAddress_components ()
    {
        return address_components;
    }

    public void setAddress_components (AddressComponents[] address_components)
    {
        this.address_components = address_components;
    }

    public Photo[] getPhotos ()
    {
        return photos;
    }

    public void setPhotos (Photo[] photos)
    {
        this.photos = photos;
    }

    public String getUrl ()
    {
        return url;
    }

    public void setUrl (String url)
    {
        this.url = url;
    }

    public String getReference ()
    {
        return reference;
    }

    public void setReference (String reference)
    {
        this.reference = reference;
    }

    public String getUser_ratings_total ()
    {
        return user_ratings_total;
    }

    public void setUser_ratings_total (String user_ratings_total)
    {
        this.user_ratings_total = user_ratings_total;
    }

    public Reviews[] getReviews ()
    {
        return reviews;
    }

    public void setReviews (Reviews[] reviews)
    {
        this.reviews = reviews;
    }

    public String getPrice_level ()
    {
        return price_level;
    }

    public void setPrice_level (String price_level)
    {
        this.price_level = price_level;
    }

    public String getScope ()
    {
        return scope;
    }

    public void setScope (String scope)
    {
        this.scope = scope;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public Opening_hours getOpening_hours ()
    {
        return opening_hours;
    }

    public void setOpening_hours (Opening_hours opening_hours)
    {
        this.opening_hours = opening_hours;
    }

    public Geometry getGeometry ()
    {
        return geometry;
    }

    public void setGeometry (Geometry geometry)
    {
        this.geometry = geometry;
    }

    public String getVicinity ()
    {
        return vicinity;
    }

    public void setVicinity (String vicinity)
    {
        this.vicinity = vicinity;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getAdr_address ()
    {
        return adr_address;
    }

    public void setAdr_address (String adr_address)
    {
        this.adr_address = adr_address;
    }

    public PlusCode getPlusCode()
    {
        return plusCode;
    }

    public void setPlusCode(PlusCode plusCode)
    {
        this.plusCode = plusCode;
    }

    public String getFormatted_phone_number ()
    {
        return formatted_phone_number;
    }

    public void setFormatted_phone_number (String formatted_phone_number)
    {
        this.formatted_phone_number = formatted_phone_number;
    }

    public String getInternational_phone_number ()
    {
        return international_phone_number;
    }

    public void setInternational_phone_number (String international_phone_number)
    {
        this.international_phone_number = international_phone_number;
    }

    public String getPlace_id ()
    {
        return place_id;
    }

    public void setPlace_id (String place_id)
    {
        this.place_id = place_id;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [utc_offset = "+utc_offset+", formatted_address = "+formatted_address+", types = "+types+", website = "+website+", icon = "+icon+", rating = "+rating+", address_components = "+address_components+", photos = "+photos+", url = "+url+", reference = "+reference+", user_ratings_total = "+user_ratings_total+", reviews = "+reviews+", price_level = "+price_level+", scope = "+scope+", name = "+name+", opening_hours = "+opening_hours+", geometry = "+geometry+", vicinity = "+vicinity+", id = "+id+", adr_address = "+adr_address+", PlusCode = "+ plusCode +", formatted_phone_number = "+formatted_phone_number+", international_phone_number = "+international_phone_number+", place_id = "+place_id+"]";
    }
}
