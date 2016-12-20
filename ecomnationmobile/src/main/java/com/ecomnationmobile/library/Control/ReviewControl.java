package com.ecomnationmobile.library.Control;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ecomnationmobile.library.Common.CustomLayoutInflater;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Data.KeyValuePair;
import com.ecomnationmobile.library.Data.Review;
import com.ecomnationmobile.library.Data.ReviewData;
import com.ecomnationmobile.library.Data.ReviewLink;
import com.ecomnationmobile.library.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 9/24/2016.
 */
public class ReviewControl extends FrameLayout {

    Context mContext;
    View view;
    String storeId, urlValue;
    long productId;
    Review review;
    LinearLayout reviewList;
    ECNCallback mCallback;
    ProgressDialog progressDialog;

    public ReviewControl(Context context) {
        this(context, null);
    }

    public ReviewControl(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReviewControl(final Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mContext = context;

        view = LayoutInflater.from(context).inflate(R.layout.review_layout, null);
        reviewList = (LinearLayout) view.findViewById(R.id.reviewList);

        view.findViewById(R.id.addReview).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onSuccess("");
            }
        });

        addView(view);
    }

    public void init(String storeId, long productId, int colorId, int progressId, ECNCallback callback) {
        this.storeId = storeId;
        this.productId = productId;
        ((TextView) view.findViewById(R.id.addReview)).setTextColor(ContextCompat.getColor(mContext, colorId));
        mCallback = callback;
        urlValue = mContext.getString(R.string.review_url) + mContext.getString(R.string.api) + "store/" + storeId + "/product/" + productId + "/reviews?page[number]=" + 1 + "&page[size]=3";

        progressDialog = new ProgressDialogView(mContext, "Please wait...", progressId);
        getReviews(urlValue);
    }

    public void getReviews(String url) {
        HelperClass.getData(mContext, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                review = new Gson().fromJson(result, Review.class);
                setUpReviews(review.getData());
                if(progressDialog.isShowing())
                    progressDialog.dismiss();
            }

            @Override
            public void onFailure(KeyValuePair error) {
                setUpReviews(new ArrayList<ReviewData>());
                if(progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        });
    }

    public void setUpReviews(final List<ReviewData> dataList) {
        if (dataList != null && !dataList.isEmpty()) {
            reviewList.setVisibility(VISIBLE);
            view.findViewById(R.id.noReviewText).setVisibility(GONE);
            reviewList.removeAllViews();
            for (int i = 0; i < dataList.size(); i++) {
                final View review = CustomLayoutInflater.getReview(mContext, dataList.get(i), R.color.WHITE, R.color.DARK_ORANGE);
                review.setTag(dataList.get(i).getId());
                review.findViewById(R.id.options).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String id = (String) review.getTag();
                        PopupMenu popup = new PopupMenu(mContext, v);

                        /** Adding menu items to the popumenu */
                        popup.getMenuInflater().inflate(R.menu.flag, popup.getMenu());

                        /** Defining menu item click listener for the popup menu */
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                markAsFlagged(id);
                                return true;
                            }

                        });

                        /** Showing the popup menu */
                        popup.show();
                    }
                });
                reviewList.addView(review);
            }

            view.findViewById(R.id.reviewLinks).setVisibility(GONE);
            ReviewLink link = review.getLinks();
            if (link != null) {
                if (link.getSelf() != null && !link.getSelf().isEmpty())
                    urlValue = link.getSelf();
                if(dataList.size() == 1) {
                    if (link.getPrev() != null && !link.getPrev().isEmpty())
                        urlValue = link.getPrev();
                }

                setLink(R.id.first, link.getFirst());
                setLink(R.id.prev, link.getPrev());
                setLink(R.id.next, link.getNext());
                setLink(R.id.last, link.getLast());
            }
        } else {
            reviewList.setVisibility(GONE);
            view.findViewById(R.id.noReviewText).setVisibility(VISIBLE);
        }
    }

    private void setLink(int id, final String link) {
        view.findViewById(id).setVisibility(INVISIBLE);
        if (link != null && !link.isEmpty()) {
            view.findViewById(id).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.show();
                    getReviews(link);
                }
            });
            view.findViewById(id).setVisibility(VISIBLE);
            view.findViewById(R.id.reviewLinks).setVisibility(VISIBLE);
        }
    }

    private void markAsFlagged(String reviewId) {
        String url = mContext.getString(R.string.review_url) + mContext.getString(R.string.api) + "store/" + storeId + "/product/" + productId + "/reviews/" + reviewId + "/flag";

        progressDialog.show();
        HelperClass.putData(mContext, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                getReviews(urlValue);
                Toast.makeText(mContext, "Review has been marked as flagged.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(KeyValuePair error) {

            }
        });
    }
}
