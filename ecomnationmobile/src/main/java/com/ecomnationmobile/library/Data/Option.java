package com.ecomnationmobile.library.Data;

import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Abhi on 04-11-2015.
 */

@DatabaseTable
public class Option {
    private String id;
    private String name;
    private String type;
    private String shape;
    private String clarity;
    private String color;
    private double carat;
    private double rate;
    private String size;
    private double price;
    private double per_gm_rate;
    private double weight;
    private boolean is_default;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getShape() {
        return shape;
    }

    public String getClarity() {
        return clarity;
    }

    public String getColor() {
        return color;
    }

    public double getCarat() {
        return carat;
    }

    public double getRate() {
        return rate;
    }

    public boolean is_default() {
        return is_default;
    }

    public String getSize() {
        return size;
    }

    public double getPrice() {
        return price;
    }

    public double getPer_gm_rate() {
        return per_gm_rate;
    }

    public double getWeight() {
        return weight;
    }
}
