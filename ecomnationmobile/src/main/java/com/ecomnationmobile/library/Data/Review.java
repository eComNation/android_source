package com.ecomnationmobile.library.Data;

import java.util.List;

/**
 * Created by User on 9/22/2016.
 */
public class Review {
    private List<ReviewData> data;
    private ReviewLink links;

    public List<ReviewData> getData() {
        return data;
    }

    public ReviewLink getLinks() {
        return links;
    }
}
