package com.ecomnationmobile.library.Control;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecomnationmobile.library.R;

/**
 * Created by Chetan on 01/07/2016.
 */
public class AlertDialogView{

    View view;
    Context mContext;
    TextView txtYes, txtNo, txtHeader, txtBody;
    LinearLayout headerLayout;
    AlertDialog alertDialog;
    ImageView storeImage;

    final public static int CONFIRM = 1;
    final public static int ERROR = 2;
    final public static int UPDATE = 3;

    public AlertDialogView(final Context context) {
        mContext = context;

        view = LayoutInflater.from(context).inflate(R.layout.alert_dialog_view, null);
        txtYes = (TextView) view.findViewById(R.id.txtYes);
        txtNo = (TextView) view.findViewById(R.id.txtNo);
        txtHeader = (TextView) view.findViewById(R.id.txtHeader);
        txtBody = (TextView) view.findViewById(R.id.txtBody);
        headerLayout = (LinearLayout) view.findViewById(R.id.headerLayout);
        storeImage = (ImageView) view.findViewById(R.id.storeImage);
    }

    public void initColor(int headerBackRoundColor, int buttonTextColor, int storeImageId){
        headerLayout.setBackgroundColor(ContextCompat.getColor(mContext,headerBackRoundColor));
        txtYes.setTextColor(ContextCompat.getColor(mContext,buttonTextColor));
        txtNo.setTextColor(ContextCompat.getColor(mContext,buttonTextColor));
        storeImage.setImageResource(storeImageId);
    }

    public void initText(String headerText, String bodyText,int type){
        setHeaderText(headerText);
        setBodyText(bodyText);
        switch (type)
        {
            case CONFIRM:
                txtYes.setText("YES");
                txtNo.setText("NO");
                break;
            case ERROR:
                txtYes.setText("RETRY");
                txtNo.setText("CLOSE");
                break;
            case UPDATE:
                txtYes.setText("UPDATE");
                txtNo.setText("CLOSE");
                break;
        }
    }

    public TextView getYesButton(){
        return txtYes;
    }

    public TextView getNoButton(){
        return txtNo;
    }

    public void setHeaderText(String headerText){
        txtHeader.setText(headerText);
    }

    public void setBodyText(String bodyText){
        txtBody.setText(bodyText);
    }

    public void showAlertDialog() {
        alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setView(view);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public void dismissAlertDialog(){
        if(alertDialog != null)
            alertDialog.dismiss();
    }
}
