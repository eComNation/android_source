package com.ecomnationmobile.library.Control;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.R;

import org.json.JSONObject;

/**
 * Created by User on 7/20/2016.
 */
public class PinCodeChecker extends LinearLayout {

    EditText pinCodeText;
    Button btnCheck;
    ImageButton btnClose;
    String message, pinCodeString;
    View view, pinCodeMessage;
    Context mContext;

    public PinCodeChecker(Context context) {
        this(context, null);
    }

    public PinCodeChecker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PinCodeChecker(final Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mContext = context;

        setOrientation(VERTICAL);
        setDividerDrawable(ContextCompat.getDrawable(context, R.drawable.vertical_divider));
        setShowDividers(SHOW_DIVIDER_BEGINNING | SHOW_DIVIDER_END);

        view = LayoutInflater.from(context).inflate(R.layout.pincode_checker, null);

        pinCodeText = (EditText) view.findViewById(R.id.pinCodeText);
        btnCheck = (Button) view.findViewById(R.id.btnCheck);
        btnClose = (ImageButton) view.findViewById(R.id.btnClose);
        pinCodeMessage = view.findViewById(R.id.pinCodeMessage);

        addView(view);
    }

    public void init(int id, final int true_id, final int false_id, final int null_id) {
        final ProgressDialog dialog = new ProgressDialog(mContext, android.R.style.Theme_Holo_Light_Panel);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);

        pinCodeString = HelperClass.getSharedString(mContext, "pincodes");
        btnCheck.setTextColor(ContextCompat.getColor(mContext,id));
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pinCodeText.getText().length() == 6) {
                    HelperClass.hideKeyboard(mContext);
                    dialog.show();
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pinCodeMessage.setVisibility(View.VISIBLE);
                            try {
                                final JSONObject jObject = new JSONObject(pinCodeString);
                                boolean b = jObject.getBoolean(pinCodeText.getText().toString());
                                if (b)
                                    message = String.format(mContext.getString(true_id), pinCodeText.getText().toString());
                                else
                                    message = String.format(mContext.getString(false_id), pinCodeText.getText().toString());
                            } catch (Exception e) {
                                message = String.format(mContext.getString(null_id), pinCodeText.getText().toString());
                                e.printStackTrace();
                            } finally {
                                ((TextView) findViewById(R.id.txtMessage)).setText(message);
                                dialog.dismiss();
                            }
                        }
                    }, 1000);
                } else {
                    Toast.makeText(mContext, "Please enter a valid pin code.", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinCodeText.setText("");
                pinCodeMessage.setVisibility(View.GONE);
            }
        });
    }
}
