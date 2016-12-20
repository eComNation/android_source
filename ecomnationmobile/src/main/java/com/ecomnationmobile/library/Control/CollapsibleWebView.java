package com.ecomnationmobile.library.Control;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ecomnationmobile.library.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created by User on 6/30/2016.
 */
public class CollapsibleWebView extends FrameLayout {

    TextView textShowHide, textExtra;
    LayoutParams layoutParams;
    WebView textValue;
    View view;
    boolean isShow;
    Context mContext;
    int maxCount;

    public CollapsibleWebView(Context context) {
        this(context, null);
    }

    public CollapsibleWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollapsibleWebView(final Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mContext = context;

        view = LayoutInflater.from(context).inflate(R.layout.collapsible_webview, null);

        textValue = (WebView) view.findViewById(R.id.txtValue);
        textShowHide = (TextView) view.findViewById(R.id.txtShowHide);
        textExtra = (TextView) view.findViewById(R.id.txtExtra);

        addView(view);

        textShowHide.setText(mContext.getString(R.string.more));
        textExtra.setVisibility(VISIBLE);
        isShow = true;

        textShowHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShow) {
                    textShowHide.setText(mContext.getString(R.string.less));
                    textExtra.setVisibility(GONE);
                    layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    textValue.setLayoutParams(layoutParams);
                    isShow = false;
                } else {
                    textShowHide.setText(mContext.getString(R.string.more));
                    textExtra.setVisibility(VISIBLE);
                    layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 202);
                    textValue.setLayoutParams(layoutParams);
                    isShow = true;
                }
            }
        });
    }

    public void setText(String text,int colorId) {
        if (text == null || text.equals(""))
            text = mContext.getString(R.string.no_description);

        textShowHide.setTextColor(ContextCompat.getColor(mContext,colorId));

        ViewTreeObserver viewTreeObserver  = textValue.getViewTreeObserver();

        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                int height = textValue.getMeasuredHeight();
                if (height > 210) {
                    layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 202);
                    textShowHide.setVisibility(VISIBLE);
                    textExtra.setVisibility(VISIBLE);
                } else {
                    layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    textShowHide.setVisibility(GONE);
                    textExtra.setVisibility(GONE);
                }
                textValue.setLayoutParams(layoutParams);
                return false;
            }
        });

        StringBuilder sb = new StringBuilder();
        sb.append("<HTML><HEAD></HEAD><body>");
        sb.append(text);
        sb.append("</body></HTML>");

        try {
            Document doc = Jsoup.parse(sb.toString());
            textValue.setWebViewClient(new WebViewClient());
            textValue.getSettings().setJavaScriptEnabled(true);
            textValue.loadDataWithBaseURL("file:///android_asset/", doc.html(), "text/html", "utf-8", null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}