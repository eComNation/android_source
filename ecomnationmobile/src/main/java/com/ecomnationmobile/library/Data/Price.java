package com.ecomnationmobile.library.Data;

import java.util.List;

/**
 * Created by User on 7/2/2016.
 */
public class Price {
    private double total;
    private double engrave_charge;
    private double making_charge;
    private Option metal;
    private List<ExtendedOption> gem_options;

    public double getTotal() {
        return total;
    }

    public double getEngrave_charge() {
        return engrave_charge;
    }

    public double getMaking_charge() {
        return making_charge;
    }

    public Option getMetal() {
        return metal;
    }

    public List<ExtendedOption> getGem_options() {
        return gem_options;
    }
}
