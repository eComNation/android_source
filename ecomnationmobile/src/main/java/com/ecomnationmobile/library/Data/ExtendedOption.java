package com.ecomnationmobile.library.Data;

import java.util.List;

/**
 * Created by User on 7/2/2016.
 */
public class ExtendedOption {
    private String id;
    private String name;
    private int nod;
    private int number_of_gems;
    private Option value;
    private List<Option> values;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getNod() {
        return nod;
    }

    public List<Option> getValues() {
        return values;
    }

    public Option getValue() {
        return value;
    }

    public int getNumber_of_gems() {
        return number_of_gems;
    }
}
