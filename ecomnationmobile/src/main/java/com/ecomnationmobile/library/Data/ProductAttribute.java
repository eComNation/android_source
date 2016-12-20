package com.ecomnationmobile.library.Data;

import java.util.List;

/**
 * Created by User on 6/9/2016.
 */
public class ProductAttribute {
    private String name;
    private List<String> values;

    public void setName(String name) {
        this.name = name;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public List<String> getValues() {
        return values;
    }

}
