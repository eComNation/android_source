package com.ecomnationmobile.library.Data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 9/22/2016.
 */
public class ReviewAttribute {
    @SerializedName("store-id")
    private long store_id;

    private String title;
    private String content;
    private double star;
    private int status;
    private ReviewAuthor author;
    private ReviewProduct product;

    public long getStore_id() {
        return store_id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public double getStar() {
        return star;
    }

    public int getStatus() {
        return status;
    }

    public ReviewAuthor getAuthor() {
        return author;
    }

    public ReviewProduct getProduct() {
        return product;
    }
}
