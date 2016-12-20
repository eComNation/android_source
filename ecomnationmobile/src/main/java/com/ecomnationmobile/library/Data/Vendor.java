package com.ecomnationmobile.library.Data;

import java.util.List;

/**
 * Created by User on 8/24/2016.
 */
public class Vendor {
    private String id;
    private String name;
    private double price;
    private double shipping_charge;
    private List<ExtendedVariant> variants;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public double getShipping_charge() {
        return shipping_charge;
    }

    public List<ExtendedVariant> getVariants() {
        return variants;
    }
}
