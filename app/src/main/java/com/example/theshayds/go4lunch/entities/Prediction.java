package com.example.theshayds.go4lunch.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Prediction implements Serializable {

    @SerializedName("description")
    private String description;

    @SerializedName("id")
    private String id;

    @SerializedName("matched_substring")
    private String matchedSubstring;

    @SerializedName("place_id")
    private String placeId;

    @SerializedName("reference")
    private String reference;

    @SerializedName("terms")
    private List<Term> terms;

    @SerializedName("types")
    private List<String> types;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMatchedSubstring() {
        return matchedSubstring;
    }

    public void setMatchedSubstring(String matchedSubstring) {
        this.matchedSubstring = matchedSubstring;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public List<Term> getTerms() {
        return terms;
    }

    public void setTerms(List<Term> terms) {
        this.terms = terms;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }
}
