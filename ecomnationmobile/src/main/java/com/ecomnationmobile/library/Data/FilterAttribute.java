package com.ecomnationmobile.library.Data;

import java.util.List;

/**
 * Created by Abhi on 26-02-2016.
 */
public class FilterAttribute {

    private String name;
    private List<FilterValue> values;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FilterValue> getValues() {
        return values;
    }

    public void setValues(List<FilterValue> values) {
        this.values = values;
    }
}
