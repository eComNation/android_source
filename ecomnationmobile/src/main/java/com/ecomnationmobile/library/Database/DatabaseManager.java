package com.ecomnationmobile.library.Database;

/**
 * Created by Abhi on 05-11-2015.
 */

import android.content.Context;

import com.ecomnationmobile.library.Data.Category;

import java.sql.SQLException;
import java.util.List;

public class DatabaseManager {

    static private DatabaseManager instance;

    static public void init(Context ctx) {
        if (null == instance) {
            instance = new DatabaseManager(ctx);
        }
    }

    static public DatabaseManager getInstance() {
        return instance;
    }

    private DatabaseHelper helper;

    private DatabaseManager(Context ctx) {
        helper = new DatabaseHelper(ctx);
    }

    private DatabaseHelper getHelper() {
        return helper;
    }

    public void deleteCategories()
    {
        try {
            getHelper().getCategoryDao().deleteBuilder().delete();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void addCategories(List<Category> categories) {
        try {
            getHelper().getCategoryDao().create(categories);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Category> getSubCategories(long parent_id) {
        List<Category> categories = null;
        try {
            categories = getHelper().getCategoryDao().queryForEq("parent_id", parent_id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public List<Category> getAllCategories() {
        List<Category> categories = null;
        try {
            categories = getHelper().getCategoryDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public Category getCategory(long id) {
        Category category = null;
        try {
            category = getHelper().getCategoryDao().queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return category;
    }
}