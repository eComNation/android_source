package com.eComNation.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eComNation.Common.Utility;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Data.Category;
import com.ecomnationmobile.library.Data.Option;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by User on 6/10/2016.
 */
public class CategoryListAdapter extends ArrayAdapter<Object> {

    Context mContext;
    int layoutResourceId;
    List<Object> data = null;

    public CategoryListAdapter(Context mContext, int layoutResourceId, List<Object> data) {
        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        final View listItem = inflater.inflate(layoutResourceId, parent, false);

        if (data.get(position).getClass().isInstance(new Category())) {
            Category category = (Category) data.get(position);

            ((TextView) listItem.findViewById(R.id.txtCategoryName)).setText(category.getName());
            ImageView image = (ImageView) listItem.findViewById(R.id.imgCategory);

            String url = category.getImage_url();

            if (url != null) {
                Picasso.with(mContext).load(HelperClass.processURL(url)).error(R.drawable.placeholder).into(image, new Callback() {
                    @Override
                    public void onSuccess() {
                        listItem.findViewById(R.id.image_progress).setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        listItem.findViewById(R.id.image_progress).setVisibility(View.GONE);
                    }
                });
            } else {
                image.setImageResource(R.drawable.placeholder);
                listItem.findViewById(R.id.image_progress).setVisibility(View.GONE);
            }
        }
        if (data.get(position).getClass().isInstance(new String())) {
            ((TextView) listItem.findViewById(R.id.txtCategoryName)).setText(data.get(position).toString().toUpperCase());
        }
        if (data.get(position).getClass().isInstance(new Option())) {
            Option test = (Option) data.get(position);
            ImageView image = (ImageView) listItem.findViewById(R.id.imgCategory);
            String name = test.getName();
            String img = name.toLowerCase().replace(" ", "_");
            if(test.getType() != null && test.getType().equalsIgnoreCase(Utility.DIAMOND))
                img = "dia";

            if (img.contains("gold")) {
                img = img.substring(0, img.length() - 4);
                ((TextView) listItem.findViewById(R.id.imageName)).setText(name.substring(name.length() - 3).toUpperCase());
            }
            image.setImageResource(mContext.getResources().getIdentifier(img, "drawable", mContext.getPackageName()));
            ((TextView) listItem.findViewById(R.id.txtCategoryName)).setText(name.toUpperCase());
        }

        return listItem;
    }
}
