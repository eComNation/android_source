package com.ecomnationmobile.library.Data;

/**
 * Created by Abhi on 26/7/15.
 */
public class KeyValuePair {


    public KeyValuePair(){}

    public KeyValuePair(int value,String key)
    {
        this.id = value; this.name = key;
    }

    public int getValue() {
        return id;
    }

    public void setValue(int value) {
        this.id = value;
    }

    private int id;

    public void setKey(String key) {
        this.name = key;
    }

    public String getKey() {
        return name;
    }

    private String name;
}
