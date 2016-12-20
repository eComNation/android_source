package com.ecomnationmobile.library.Control;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ecomnationmobile.library.Common.CustomAnimation;
import com.ecomnationmobile.library.Common.CustomLayoutInflater;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Data.FilterValue;
import com.ecomnationmobile.library.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Abhi on 03-02-2016.
 */
public class ExpandableView extends LinearLayout {

    LinearLayout selector, list;
    ImageView image;
    View view;
    String displayText, indexText, labelText;
    List<String> indexList, displayList;
    Context mContext;
    ECNCallback mCallback;
    TextView selectedText;

    public ExpandableView(Context context) {
        this(context, null);
    }

    public ExpandableView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandableView(final Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mContext = context;

        setOrientation(VERTICAL);

        view = LayoutInflater.from(context).inflate(R.layout.expandable_view, null);

        selector = (LinearLayout) view.findViewById(R.id.filterSelector);
        list = (LinearLayout) view.findViewById(R.id.filterList);

        image = (ImageView) view.findViewById(R.id.dropdown);
        selectedText = (TextView) view.findViewById(R.id.selection);

        addView(view);

        selector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.getVisibility() == GONE) {
                    image.setImageResource(R.drawable.uparrow);
                    list.setVisibility(VISIBLE);
                    CustomAnimation.slide_down(mContext, list);
                    selectedText.setText("");
                } else {
                    CustomAnimation.slide_up(mContext, list);
                    list.setVisibility(GONE);
                    image.setImageResource(R.drawable.dropdown_arrow);
                    selectedText.setText(displayText);
                }
            }
        });
    }

    public void init(String labelText, List<FilterValue> collection, String facets, ECNCallback callback) {
        mCallback = callback;
        displayText = indexText = "";
        displayList = new ArrayList<>();
        indexList = new ArrayList<>();
        list.removeAllViews();

        this.labelText = labelText;

        ((TextView) selector.findViewById(R.id.title)).setText(HelperClass.capitalizeFirst(labelText));

        final List<FilterValue> new_collection = sort(collection);

        View view;
        for (int i = 0; i < new_collection.size(); i++) {
            if (labelText.contains("color"))
                view = CustomLayoutInflater.getColorFilterView(mContext, new_collection.get(i).getValue());
            else
                view = CustomLayoutInflater.getFilterView(mContext, new_collection.get(i).getValue());
            view.setTag(i);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    ToggleButton isSelected = (ToggleButton) v.findViewById(R.id.pickButton);
                    isSelected.toggle();
                    setSelection(isSelected.isChecked(), new_collection.get(position));
                }
            });
            String string = HelperClass.getSharedString(mContext, mContext.getString(R.string.selected_filters));
            if (string != null) {
                List<String> selectedList = new ArrayList<>(Arrays.asList(string.split(",")));
                for (String s : selectedList) {
                    if (s.equals(new_collection.get(i).getAttribute_index())) {
                        ((ToggleButton) view.findViewById(R.id.pickButton)).setChecked(true);
                        setSelection(true, new_collection.get(i));
                    }
                }
                selectedText.setText(displayText);
            }
            if (facets != null) {
                if (facets.contains("\""+new_collection.get(i).getAttribute_index()+"\""))
                    list.addView(view);
            } else
                list.addView(view);
        }
    }

    private List<FilterValue> sort(List<FilterValue> list) {
        Collections.sort(list, new Comparator<FilterValue>() {
            public int compare(FilterValue order1,FilterValue order2) {
                return String.valueOf(order1.getValue()).compareTo(order2.getValue());
            }
        });
        return list;
    }

    private void setSelection(boolean flag, FilterValue fValue) {
        if (flag) {
            displayList.add(HelperClass.capitalizeFirst(fValue.getValue()));
            indexList.add(fValue.getAttribute_index());
        } else {
            displayList.remove(HelperClass.capitalizeFirst(fValue.getValue()));
            indexList.remove(fValue.getAttribute_index());
        }
        displayText = TextUtils.join(",", displayList);
        indexText = TextUtils.join(",", indexList);

        mCallback.onSuccess("");
        mCallback.onFailure(null);
    }

    public String getSelection() {
        return indexText;
    }

    public String getDisplaySelection() {
        return displayList.isEmpty() ? "" : labelText.toUpperCase();
    }

    public void reset() {
        indexList = new ArrayList<>();
        displayList = new ArrayList<>();
        indexText = displayText = "";
        selectedText.setText(displayText);
        int count = list.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = list.getChildAt(i);
            ((ToggleButton) v.findViewById(R.id.pickButton)).setChecked(false);
        }
        mCallback.onFailure(null);
    }

    public int getCount() {
        if(list != null)
            return list.getChildCount();
        return 0;
    }
}