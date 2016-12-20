package com.ecomnationmobile.library.Control;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecomnationmobile.library.R;


/**
 * Created by Abhi on 29-11-2015.
 */
public class AnimatedEditText extends LinearLayout {

    TextView textView;
    EditText editText;
    View view;
    String editTextHint;
    int GRAY = 0, GREEN = 0;
    Drawable greenLine, grayLine;

    public AnimatedEditText(Context context) {
        this(context, null);
    }

    public AnimatedEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimatedEditText(final Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        GRAY = ContextCompat.getColor(context,R.color.DARK_GRAY);
        GREEN = ContextCompat.getColor(context,R.color.AppGreen);
        greenLine = ContextCompat.getDrawable(context, R.drawable.vertical_divider_green);
        grayLine = ContextCompat.getDrawable(context, R.drawable.vertical_divider);

        setOrientation(VERTICAL);
        setShowDividers(SHOW_DIVIDER_END);

        view = LayoutInflater.from(context).inflate(R.layout.label_text_item, null);
        textView = (TextView)view.findViewById(R.id.txtLabel);
        editText = (EditText) view.findViewById(R.id.txtText);

        addView(view);

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    textView.setVisibility(VISIBLE);
                    editText.setHint("");
                    setColor(GREEN, greenLine);
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(v, 0);
                } else {
                    setColor(GRAY, grayLine);
                    if (editText.getText().toString().equals("")) {
                        textView.setVisibility(INVISIBLE);
                        editText.setHint(editTextHint);
                    }
                }
            }
        });

        this.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    setColor(GRAY, grayLine);
                }
            }
        });
        editText.setBackgroundColor(ContextCompat.getColor(context,R.color.TRANSPARENT));
        editText.setHintTextColor(GRAY);
    }

    private void setColor(int color, Drawable divider) {
        textView.setTextColor(color);
        setDividerDrawable(divider);
    }

    public void setHint(String hint) {
        editTextHint = hint;
        setColor(GRAY, grayLine);
        if (editText.getText().toString().equals("")) {
            textView.setVisibility(INVISIBLE);
            editText.setHint(editTextHint);
        }
    }
}
