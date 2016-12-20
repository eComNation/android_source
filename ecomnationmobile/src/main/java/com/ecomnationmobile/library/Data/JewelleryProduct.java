package com.ecomnationmobile.library.Data;

import java.util.List;

/**
 * Created by Abhi on 30-10-2015.
 */
public class JewelleryProduct {
    private String id;
    private String making_type;
    private double making_charge;
    private double engrave_charge;
    private double discount;
    private List<Option> metal_options;
    private List<Option> metal_sizes;
    private List<ExtendedOption> gem_options;

    public String getId() {
        return id;
    }

    public String getMaking_type() {
        return making_type;
    }

    public double getMaking_charge() {
        return making_charge;
    }

    public double getDiscount() {
        return discount;
    }

    public double getEngrave_charge() {
        return engrave_charge;
    }

    public List<Option> getMetal_options() {
        return metal_options;
    }

    public List<Option> getMetal_sizes() {
        return metal_sizes;
    }

    public List<ExtendedOption> getGem_options() {
        return gem_options;
    }
}
