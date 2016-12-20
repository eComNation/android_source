package com.ecomnationmobile.library.Data;

/**
 * Created by User on 8/24/2016.
 */
public class ExtendedVariant {
    private String id;
    private double price;
    private double previous_price;
    private String option1;
    private String option2;
    private String option3;
    private int quantity;

    public String getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }

    public double getPrevious_price() {
        return previous_price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getOption1() {
        return option1;
    }

    public String getOption2() {
        return option2;
    }

    public String getOption3() {
        return option3;
    }
}
