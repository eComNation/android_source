package com.ecomnationmobile.library.Control;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;

/**
 * Created by User on 8/24/2016.
 */
public class ProgressDialogView extends ProgressDialog {
    public ProgressDialogView(Context context, String message, int drawableId){
        super(context, android.R.style.Theme_Holo_Light_Panel);
        super.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        super.setMessage(message);
        super.setIndeterminateDrawable(ContextCompat.getDrawable(context,drawableId));
        super.setCancelable(false);
    }
}
