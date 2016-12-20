package com.ecomnationmobile.library.Control;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ecomnationmobile.library.R;

/**
 * Created by Abhi on 03-03-2016.
 */
public class CollapsibleText extends FrameLayout {

    TextView textValue, textShowHide;
    View view;
    boolean isShow;
    Context mContext;
    int maxCount;

    public CollapsibleText(Context context) {
        this(context, null);
    }

    public CollapsibleText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollapsibleText(final Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mContext = context;

        view = LayoutInflater.from(context).inflate(R.layout.collapsible_text, null);

        textValue = (TextView) view.findViewById(R.id.txtValue);
        textShowHide = (TextView) view.findViewById(R.id.txtShowHide);

        addView(view);

        textShowHide.setText(mContext.getString(R.string.more));
        isShow = true;

        textShowHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShow) {
                    textShowHide.setText(mContext.getString(R.string.less));
                    textValue.setLines(maxCount);
                    isShow = false;
                } else {
                    textShowHide.setText(mContext.getString(R.string.more));
                    textValue.setLines(5);
                    isShow = true;
                }
            }
        });
    }

    public String getText() {
        return textValue.getText().toString();
    }

    public void setText(String text, int textColorId,int backId) {
        if (text == null || text.equals(""))
            textValue.setText(mContext.getString(R.string.no_description));
        else
            textValue.setText(text.trim());

        textShowHide.setBackgroundResource(backId);
        textShowHide.setTextColor(ContextCompat.getColor(mContext,textColorId));

        textValue.post(new Runnable() {
            public void run() {
                maxCount = textValue.getLineCount();
                if (maxCount > 5) {
                    textValue.setLines(5);
                    textShowHide.setVisibility(VISIBLE);
                } else {
                    textValue.setLines(maxCount);
                    textShowHide.setVisibility(GONE);
                }
            }
        });
    }
}

