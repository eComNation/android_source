package com.eComNation.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.eComNation.R;
import com.ecomnationmobile.library.Data.Category;

import java.util.List;

/**
 * Created by Abhi on 26-08-2015.
 */
public class DrawerListAdapter extends ArrayAdapter<Object> {

    Context mContext;
    int layoutResourceId;
    List<Object> data = null;

    public DrawerListAdapter(Context mContext, int layoutResourceId, List<Object> data) {
        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItem;

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        listItem = inflater.inflate(layoutResourceId, parent, false);

        if(data.get(position).getClass().isInstance(new Category())) {
            Category item = (Category)data.get(position);
            ((TextView) listItem.findViewById(R.id.drawer_title)).setText(item.getName());
            if (!item.is_Parent(mContext))
                listItem.findViewById(R.id.rightArrow).setVisibility(View.GONE);
        }
        else
        if(data.get(position).getClass().isInstance(new String())) {
            String item = (String)data.get(position);
            ((TextView) listItem.findViewById(R.id.drawer_title)).setText(item);
            listItem.findViewById(R.id.rightArrow).setVisibility(View.GONE);
        }

        return listItem;
    }
}
