package com.ecomnationmobile.library.Common;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Abhi on 26/7/15.
 */
public class JSON {

    public static String Read(Context context, String fileName) {
        String json;
        try {
            InputStream is = context.getAssets().open(fileName);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            return null;
        }
        return json;
    }
}
