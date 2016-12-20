package com.eComNation.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

import com.eComNation.R;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Data.DropdownClass;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by User on 6/4/2016.
 */
public class MadeToMeasureActivity extends FragmentActivity {

    ViewFlipper mViewFlipper;

    @Override
    public void onCreate(Bundle savedInstantState) {
        super.onCreate(savedInstantState);
        setContentView(R.layout.made_to_measure);

        mViewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackSelected();
            }
        });
    }

    public void onNextSelected() {
        View store_drawer_view;
        LayoutInflater inflator1 = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        int currentLevel = HelperClass.getSharedInt(this, "measure_level");
        ++currentLevel;
        HelperClass.putSharedInt(this, "measure_level", currentLevel);

        if (currentLevel == getAttributeCount())
            store_drawer_view = inflator1.inflate(R.layout.measure_result_fragment, null);
        else
            store_drawer_view = inflator1.inflate(R.layout.measure_steps_fragment, null);

        if (mViewFlipper != null) {
            mViewFlipper.addView(store_drawer_view);
            mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.left_in));
            mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.left_out));
            mViewFlipper.showNext();
        }
    }

    public void onBackSelected() {
        if (mViewFlipper != null) {
            int currentLevel = HelperClass.getSharedInt(this, "measure_level");
            if (currentLevel < 0) {
                super.onBackPressed();
            } else {
                String mURL = HelperClass.getSharedString(this, "variant_hash");
                int index = mURL.lastIndexOf(",");
                if(index >= 0)
                    mURL = mURL.substring(0,index);
                HelperClass.putSharedString(this, "variant_hash", mURL);
                mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.right_in));
                mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.right_out));
                mViewFlipper.showPrevious();
                mViewFlipper.removeViewAt(1 + currentLevel--);
                HelperClass.putSharedInt(this, "measure_level", currentLevel);
            }
        }
    }

    @Override
    public void onBackPressed() {
        onBackSelected();
    }

    public DropdownClass getCurrentAttribute(int pos) {
        String content = HelperClass.getSharedString(this, "measure_variants");
        if (content != null) {
            Type listType = new TypeToken<List<DropdownClass>>() {}.getType();
            List<DropdownClass> variantList = new Gson().fromJson(content, listType);
            if (variantList != null && !variantList.isEmpty())
                return variantList.get(pos);
        }
        return null;
    }

    public int getAttributeCount() {
        String content = HelperClass.getSharedString(this, "measure_variants");
        if (content != null) {
            Type listType = new TypeToken<List<DropdownClass>>() {}.getType();
            List<DropdownClass> variantList = new Gson().fromJson(content, listType);
            if (variantList != null)
                return variantList.size();
        }
        return 0;
    }
}
