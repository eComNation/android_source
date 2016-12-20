package com.ecomnationmobile.library.Control;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecomnationmobile.library.R;

/**
 * Created by Abhi on 14-01-2016.
 */
public class CustomEditText extends LinearLayout {

    TextView textView;
    EditText editText;
    View view;
    int secondary, primary;
    Context mContext;

    public CustomEditText(Context context) {
        this(context, null);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomEditText(final Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mContext = context;

        setOrientation(VERTICAL);

        secondary = ContextCompat.getColor(mContext,R.color.SecondaryColorText);
        primary = ContextCompat.getColor(mContext,R.color.PrimaryColorText);

        view = LayoutInflater.from(context).inflate(R.layout.custom_edittext, null);

        textView = (TextView) view.findViewById(R.id.txtLabel);
        editText = (EditText) view.findViewById(R.id.txtText);

        addView(view);

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setColor(primary);
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(v, 0);
                } else {
                    setColor(secondary);
                }
            }
        });
    }

    public void init(String labelText) {
        textView.setText(labelText);
    }

    public String getText() {
        return editText.getText().toString();
    }

    public void setText(String text) {
        editText.setText(text);
    }

    public void setInputType(int type) {
        editText.setInputType(type);
    }

    private void setColor(int color) {
        textView.setTextColor(color);
    }

    public void setEnabled(boolean bool) {
        editText.setEnabled(bool);
        if(bool)
            editText.setTextColor(primary);
        else
            editText.setTextColor(secondary);
    }

    public void setLines(int lines) {
        editText.setLines(lines);
    }

    public void setMinLines(int lines) {
        editText.setMinLines(lines);
    }

    public void setMaxLines(int lines) {
        editText.setMaxLines(lines);
    }

    public void setError(String text) {
        if (text == null)
            editText.setError(null);
        else {
            editText.setError(text);
            editText.requestFocus();
            setTextChangeListener(text);
        }
    }

    public void setTextChangeListener(final String errMsg) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!editText.getText().toString().trim().equalsIgnoreCase("")) {
                    editText.setError(null);
                }
            }
        });
    }
}
