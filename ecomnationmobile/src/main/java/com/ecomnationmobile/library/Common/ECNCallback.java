package com.ecomnationmobile.library.Common;

import com.ecomnationmobile.library.Data.KeyValuePair;

/**
 * Created by Abhi on 16-02-2016.
 */
public interface ECNCallback {

    void onSuccess(String result);

    void onFailure(KeyValuePair error);

}
