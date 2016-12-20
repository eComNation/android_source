package com.ecomnationmobile.library.Data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 9/22/2016.
 */
public class ReviewAuthor {

    @SerializedName("first-name")
    private String first_name;

    @SerializedName("last-name")
    private String last_name;

    private String email;

    public String getFirst_name() {
        return first_name;
    }

    public String getEmail() {
        return email;
    }

    public String getLast_name() {
        return last_name;
    }
}
