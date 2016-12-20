package com.ecomnationmobile.library.Data;

import android.content.Context;

import com.ecomnationmobile.library.Database.DatabaseManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

/**
 * Created by Abhi on 24-02-2016.
 */

@DatabaseTable
public class Category {

    @DatabaseField(id = true)
    private long id;

    @DatabaseField
    private String name;

    @DatabaseField
    private long parent_id;

    @DatabaseField
    private int position;

    @DatabaseField
    private String image_url;

    private boolean is_parent = true;

    public boolean is_parent() {
        return is_parent;
    }

    public void setIs_parent(boolean is_parent) {
        this.is_parent = is_parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public long getParent_id() {
        return parent_id;
    }

    public void setParent_id(long parent_id) {
        this.parent_id = parent_id;
    }

    public long getId() {
        return id;
    }

    public boolean is_Parent(Context context) {
        DatabaseManager.init(context);
        List<Category> subCategories = DatabaseManager.getInstance().getSubCategories(this.getId());
        return subCategories != null && !subCategories.isEmpty() && is_parent;
    }

    public String getImage_url() {
        return image_url;
    }
}
