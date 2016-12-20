package com.ecomnationmobile.library.Data;

/**
 * Created by Abhi on 29-03-2016.
 */
public class BannerImage {
    private long id;
    private int position;
    private String url;
    private long category_id;
    private boolean is_list;
    private String dimensions;

    public boolean isList() {
        return is_list;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public long getCategory_id() {
        return category_id;
    }

    public String getDimensions() { return dimensions; }
}
