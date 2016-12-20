package com.ecomnationmobile.library.Data;

/**
 * Created by Abhi on 02-03-2016.
 */
public class ProductImage {
    private long id;
    private int priority;
    private String url;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
