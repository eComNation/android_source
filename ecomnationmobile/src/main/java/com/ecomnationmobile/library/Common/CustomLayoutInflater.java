package com.ecomnationmobile.library.Common;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecomnationmobile.library.Control.CollapsibleText;
import com.ecomnationmobile.library.Data.Product;
import com.ecomnationmobile.library.Data.ReviewData;
import com.ecomnationmobile.library.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by Abhi on 31-01-2016.
 */
public class CustomLayoutInflater {

    public static View getFilterView(Context mContext, String title) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View listItem = inflater.inflate(R.layout.filter_item, null);

        ((TextView) listItem.findViewById(R.id.title)).setText(HelperClass.capitalizeFirst(title));

        return listItem;
    }

    public static View getColorFilterView(Context mContext, String title) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View listItem = inflater.inflate(R.layout.color_filter_item, null);

        int id = HelperClass.getColorId(mContext, title.toUpperCase());
        if (id != 0)
            listItem.findViewById(R.id.color).setBackgroundResource(id);
        ((TextView) listItem.findViewById(R.id.title)).setText(title);

        return listItem;
    }

    public static View getColorView(Context mContext, String url) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        final View listItem = inflater.inflate(R.layout.color_item, null);

        ImageView image = (ImageView) listItem.findViewById(R.id.img);

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
            image.setImageResource(R.color.LIGHT_BACK);
            listItem.findViewById(R.id.image_progress).setVisibility(View.GONE);
        }

        return listItem;
    }

    public static View getProductView(Context mContext, Product product, int width, int currency, int small_item_height, int sale_label) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        final View listItem = inflater.inflate(R.layout.small_preview, null);
        listItem.findViewById(R.id.imgSale).setBackgroundResource(sale_label);

        TextView oldPrice = (TextView) listItem.findViewById(R.id.txtOldPrice);
        Double diff = product.getPrevious_price() - product.getPrice();
        if (diff > 0) {
            oldPrice.setVisibility(View.VISIBLE);
            oldPrice.setText(HelperClass.formatCurrency(mContext.getString(currency), product.getPrevious_price()));
            oldPrice.setPaintFlags(oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            listItem.findViewById(R.id.imgSale).setVisibility(View.VISIBLE);
            if (HelperClass.getSharedBoolean(mContext, "show_percent_off")) {
                String percent = HelperClass.getPercentageString(diff, product.getPrevious_price());
                ((TextView) listItem.findViewById(R.id.imgSale)).setText(percent + " OFF");
            }
        }
        ((TextView) listItem.findViewById(R.id.txtPrice)).setText(HelperClass.formatCurrency(mContext.getString(currency), product.getPrice()));

        ((TextView) listItem.findViewById(R.id.txtProductName)).setText(product.getName());
        ImageView image = (ImageView) listItem.findViewById(R.id.imgProduct);

        String url = product.getProduct_image_url();
        if (url == null || url.equals(""))
            url = product.getImage_url();

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
        float height = mContext.getResources().getDimension(small_item_height);
        listItem.setLayoutParams(new LinearLayout.LayoutParams(width, (int) height));
        return listItem;
    }

    public static View getReview(Context mContext, ReviewData review, int colorId, int backId) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        final View listItem = inflater.inflate(R.layout.review_list_item, null);

        ((TextView) listItem.findViewById(R.id.rating)).setText(String.format(mContext.getString(R.string.review_star), review.getAttributes().getStar()));
        ((TextView) listItem.findViewById(R.id.title)).setText(review.getAttributes().getTitle());
        ((CollapsibleText) listItem.findViewById(R.id.content)).setText(review.getAttributes().getContent(), colorId, backId);

        String name = review.getAttributes().getAuthor().getFirst_name();
        name += " " + review.getAttributes().getAuthor().getLast_name();
        name += ", " + review.getAttributes().getAuthor().getEmail();
        ((TextView) listItem.findViewById(R.id.name)).setText(name);

        return listItem;
    }
}
