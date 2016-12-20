package com.ecomnationmobile.library.Data;

import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Abhi on 04-11-2015.
 */

@DatabaseTable
public class OrderProcessStatus {

    private String name;

    private int id;

    private String status_type;

    public String getType() {
        return status_type;
    }

    public void setType(String type) {
        this.status_type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
