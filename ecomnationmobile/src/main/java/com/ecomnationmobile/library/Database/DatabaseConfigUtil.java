package com.ecomnationmobile.library.Database;

import com.ecomnationmobile.library.Data.Order;
import com.ecomnationmobile.library.Data.Product;
import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Abhi on 28-10-2015.
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {

    public static final Class<?>[] CLASSES = new Class[]{
            Order.class,
            Product.class
    };

    public static void main(String[] args) throws SQLException, IOException {

        // Provide the name of .txt file which you have already created and kept in res/raw directory
        writeConfigFile("ormlite_config.txt",CLASSES);
    }
}
