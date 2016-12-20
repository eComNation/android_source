package com.eComNation.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eComNation.Adapter.CategoryListAdapter;
import com.eComNation.Common.CustomLayoutInflater;
import com.eComNation.Common.Utility;
import com.eComNation.R;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Control.GridViewWithHeaderAndFooter;
import com.ecomnationmobile.library.Control.ProgressDialogView;
import com.ecomnationmobile.library.Data.ExtendedOption;
import com.ecomnationmobile.library.Data.JewelleryProduct;
import com.ecomnationmobile.library.Data.KeyValuePair;
import com.ecomnationmobile.library.Data.Option;
import com.ecomnationmobile.library.Data.Price;
import com.ecomnationmobile.library.Data.Product;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by User on 6/22/2016.
 */
public class ViraniCustomizationActivity extends FragmentActivity {

    LinearLayout layout;
    float dens;
    JewelleryProduct jewelleryProduct;
    Product product;
    Price price;
    List<ExtendedOption> diamondOptions;
    List<Option> primaryStoneList, secondaryStoneList, primaryQualityList, secondaryQualityList, metalList;
    View engraveView, metalBlock, diamondBlock, gemStoneBlock;
    GridViewWithHeaderAndFooter diaPrimaryGrid, diaSecondaryGrid, gemPrimaryGrid, gemSecondaryGrid, metalGrid;
    TextView primaryText, secondaryText, engraveDemoText, remainingText, detailText;
    CheckBox chkBox;
    CategoryListAdapter primaryAdapter, secondaryAdapter;
    EditText engraveText;
    int primaryQuality, secondaryQuality, metalIndex, primaryStone, secondaryStone;
    String parameters, engrave_text;
    ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.virani_customization);

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        dialog = new ProgressDialogView(this, "Updating...", R.drawable.progress);

        dens = HelperClass.getDisplayMetrics(this).density;

        jewelleryProduct = (new Gson()).fromJson(HelperClass.getSharedString(this, "new_product"), JewelleryProduct.class);
        product = (new Gson()).fromJson(HelperClass.getSharedString(this, "product"), Product.class);
        price = (new Gson()).fromJson(HelperClass.getSharedString(this, "price"), Price.class);

        if (jewelleryProduct != null) {

            diamondOptions = jewelleryProduct.getGem_options();
            primaryQualityList = getList(0, Utility.DIAMOND);
            secondaryQualityList = getList(1, Utility.DIAMOND);
            primaryStoneList = getList(0, "");
            secondaryStoneList = getList(1, "");

            metalList = jewelleryProduct.getMetal_options();

            primaryQuality = HelperClass.getSharedInt(this, "primary_quality");
            secondaryQuality = HelperClass.getSharedInt(this, "secondary_quality");
            metalIndex = HelperClass.getSharedInt(this, "metal_index");
            primaryStone = HelperClass.getSharedInt(this, "primary_stone");
            secondaryStone = HelperClass.getSharedInt(this, "secondary_stone");
            engrave_text = HelperClass.getSharedString(this, "engrave_text");

            metalGrid = (GridViewWithHeaderAndFooter) findViewById(R.id.metalGrid);
            chkBox = (CheckBox) findViewById(R.id.chkEngrave);

            chkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (chkBox.isChecked())
                        findViewById(R.id.engraveLayout).setVisibility(View.VISIBLE);
                    else {
                        findViewById(R.id.engraveLayout).setVisibility(View.GONE);
                        engrave_text = "";
                    }
                    getPrice();
                }
            });

            primaryText = (TextView) findViewById(R.id.diaSecondaryHeader);
            secondaryText = (TextView) findViewById(R.id.gemSecondaryHeader);

            engraveText = (EditText) findViewById(R.id.engraveText);
            engraveDemoText = (TextView) findViewById(R.id.demoText);
            remainingText = (TextView) findViewById(R.id.remainingText);
            ImageView img = (ImageView) findViewById(R.id.image);

            String url = getString(R.string.misc_url) + "misc/engrave.png";
            HelperClass.setPicassoBitMap(url, img, new ECNCallback() {
                @Override
                public void onSuccess(String result) {
                }

                @Override
                public void onFailure(KeyValuePair error) {
                }
            });

            engraveText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    engraveDemoText.setText(s);
                    engrave_text = s.toString();
                    remainingText.setText(String.format(getString(R.string.engrave_remaining), 25 - engrave_text.length()));
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            findViewById(R.id.btnFinish).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setParameters();
                    if (parameters.equals("")) {
                        setResult(0);
                    } else {
                        HelperClass.putSharedString(ViraniCustomizationActivity.this,"price",new Gson().toJson(price));
                        HelperClass.putSharedString(ViraniCustomizationActivity.this, "price_parameters", parameters);
                        setResult(100);
                    }
                    HelperClass.hideKeyboard(ViraniCustomizationActivity.this);
                    finish();
                }
            });

            List<String> customization_options = Arrays.asList(getResources().getStringArray(R.array.customization_options));

            engraveView = findViewById(R.id.enGraveBlock);
            diamondBlock = findViewById(R.id.diamondBlock);
            gemStoneBlock = findViewById(R.id.gemBlock);
            metalBlock = findViewById(R.id.metalBlock);

            gemPrimaryGrid = (GridViewWithHeaderAndFooter) findViewById(R.id.gemPrimaryGrid);
            gemSecondaryGrid = (GridViewWithHeaderAndFooter) findViewById(R.id.gemSecondaryGrid);
            diaPrimaryGrid = (GridViewWithHeaderAndFooter) findViewById(R.id.diaPrimaryGrid);
            diaSecondaryGrid = (GridViewWithHeaderAndFooter) findViewById(R.id.diaSecondaryGrid);

            layout = (LinearLayout) findViewById(R.id.options);
            layout.removeAllViews();

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.weight = 1;

            for (int i = 0; i < customization_options.size(); i++) {
                View text = CustomLayoutInflater.getCustomItem(this, customization_options.get(i), 0);
                text.setLayoutParams(params);
                text.setClickable(true);
                text.setTag(i);

                layout.addView(text);
                text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setSelection((int) v.getTag());
                    }
                });
            }

            if (primaryStoneList.isEmpty() && secondaryStoneList.isEmpty())
                layout.getChildAt(2).setVisibility(View.GONE);

            setPriceData();

            setSelection(0);
        }
    }

    private void setSelection(int index) {
        int count = layout.getChildCount();
        for (int i = 0; i < count; i++) {
            layout.getChildAt(i).setBackgroundResource(R.color.LIGHT_GREY);
            ((TextView) layout.getChildAt(i).findViewById(R.id.name)).setTextColor(getResources().getColor(R.color.SecondaryColorText));
        }
        layout.getChildAt(index).setBackgroundResource(R.color.WHITE);
        ((TextView) layout.getChildAt(index).findViewById(R.id.name)).setTextColor(getResources().getColor(R.color.PrimaryColorText));

        getFragment(index);
    }

    private void getFragment(int index) {
        switch (index) {
            case 0:
                showGrid(0);
                break;

            case 1:
                showGrid(1);
                break;

            case 2:
                showGrid(2);
                break;

            case 3:
                if (engrave_text != null && !engrave_text.equals("")) {
                    engraveText.setText(engrave_text);
                    ((CheckBox) findViewById(R.id.chkEngrave)).setChecked(true);
                    findViewById(R.id.engraveLayout).setVisibility(View.VISIBLE);
                } else {
                    engraveText.setText("");
                    ((CheckBox) findViewById(R.id.chkEngrave)).setChecked(false);
                    findViewById(R.id.engraveLayout).setVisibility(View.GONE);
                }

                diamondBlock.setVisibility(View.GONE);
                gemStoneBlock.setVisibility(View.GONE);
                metalBlock.setVisibility(View.GONE);
                engraveView.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void showGrid(int id) {
        engraveView.setVisibility(View.GONE);
        diamondBlock.setVisibility(View.GONE);
        gemStoneBlock.setVisibility(View.GONE);
        metalBlock.setVisibility(View.GONE);

        switch (id) {
            case 0:
                metalBlock.setVisibility(View.VISIBLE);

                ((TextView) findViewById(R.id.metalHeader)).setText(String.format(getString(R.string.metal_text), product.getName()));
                primaryText.setVisibility(View.GONE);
                primaryAdapter = new CategoryListAdapter(ViraniCustomizationActivity.this, R.layout.customization_item, new ArrayList<Object>(metalList));
                metalGrid.setAdapter(primaryAdapter);

                metalGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        metalIndex = position;
                        setMetalDetail();
                        getPrice();
                    }
                });

                metalGrid.getViewTreeObserver().addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener() {

                            @Override
                            public void onGlobalLayout() {
                                if (metalIndex >= 0) {
                                    setMetalDetail();
                                }
                            }
                        });

                break;

            case 1:
                diamondBlock.setVisibility(View.VISIBLE);

                ((TextView) findViewById(R.id.diaHeader)).setText(String.format(getString(R.string.customization_text), product.getName()));
                primaryText.setVisibility(View.VISIBLE);
                primaryAdapter = new CategoryListAdapter(ViraniCustomizationActivity.this, R.layout.customization_item, new ArrayList<Object>(primaryQualityList));
                diaPrimaryGrid.setAdapter(primaryAdapter);

                diaPrimaryGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        primaryQuality = position;
                        setDiamondDetail();
                        getPrice();
                    }
                });

                diaPrimaryGrid.getViewTreeObserver().addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener() {

                            @Override
                            public void onGlobalLayout() {
                                setDiamondDetail();
                            }
                        });


                if (secondaryQualityList.isEmpty()) {
                    diaSecondaryGrid.setVisibility(View.GONE);
                    primaryText.setVisibility(View.GONE);
                } else {
                    secondaryAdapter = new CategoryListAdapter(ViraniCustomizationActivity.this, R.layout.customization_item, new ArrayList<Object>(secondaryQualityList));
                    diaSecondaryGrid.setAdapter(secondaryAdapter);

                    diaSecondaryGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                            secondaryQuality = position;
                            getPrice();
                        }
                    });

                    if (secondaryQuality >= 0) {
                        diaSecondaryGrid.requestFocusFromTouch();
                        diaSecondaryGrid.setSelection(secondaryQuality);
                    }

                    diaSecondaryGrid.setVisibility(View.VISIBLE);
                    primaryText.setVisibility(View.VISIBLE);
                }
                break;
            case 2:
                gemStoneBlock.setVisibility(View.VISIBLE);

                ((TextView) findViewById(R.id.gemHeader)).setText(String.format(getString(R.string.customization_text), product.getName()));
                primaryAdapter = new CategoryListAdapter(ViraniCustomizationActivity.this, R.layout.customization_item, new ArrayList<Object>(primaryStoneList));
                gemPrimaryGrid.setAdapter(primaryAdapter);

                gemPrimaryGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        primaryStone = position;
                        setGemStoneDetail();
                        getPrice();
                    }
                });

                gemPrimaryGrid.getViewTreeObserver().addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener() {

                            @Override
                            public void onGlobalLayout() {
                                setGemStoneDetail();
                            }
                        });


                if (secondaryStoneList.isEmpty()) {
                    gemSecondaryGrid.setVisibility(View.GONE);
                    secondaryText.setVisibility(View.GONE);
                } else {
                    secondaryAdapter = new CategoryListAdapter(ViraniCustomizationActivity.this, R.layout.customization_item, new ArrayList<Object>(secondaryStoneList));
                    gemSecondaryGrid.setAdapter(secondaryAdapter);

                    gemSecondaryGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                            secondaryStone = position;
                            getPrice();
                        }
                    });

                    if (secondaryStone >= 0) {
                        gemSecondaryGrid.requestFocusFromTouch();
                        gemSecondaryGrid.setSelection(secondaryStone);
                    }

                    gemSecondaryGrid.setVisibility(View.VISIBLE);
                    secondaryText.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private List<Option> getList(int index, String bool) {
        List<Option> newList = new ArrayList<>();
        if (diamondOptions != null && diamondOptions.size() > index) {
            ExtendedOption diop = diamondOptions.get(index);
            for (Option t : diop.getValues()) {
                if (t.getType().equalsIgnoreCase(bool))
                    newList.add(t);
            }
        }
        return newList;
    }

    private void setParameters() {
        parameters = "";
        if (metalIndex >= 0) {
            parameters += "&metal_option_id=" + jewelleryProduct.getMetal_options().get(metalIndex).getId();
        }
        if (primaryQuality >= 0) {
            parameters += "&gem_options[" + diamondOptions.get(0).getId() + "]=" + primaryQualityList.get(primaryQuality).getId();
        }
        if (secondaryQuality >= 0) {
            parameters += "&gem_options[" + diamondOptions.get(1).getId() + "]=" + secondaryQualityList.get(secondaryQuality).getId();
        }
        if (primaryStone >= 0) {
            parameters += "&gem_options[" + diamondOptions.get(0).getId() + "]=" + primaryStoneList.get(primaryStone).getId();
        }
        if (secondaryStone >= 0) {
            parameters += "&gem_options[" + diamondOptions.get(1).getId() + "]=" + secondaryStoneList.get(secondaryStone).getId();
        }

        boolean isChecked = chkBox.isChecked();
        if (isChecked && !engraveText.getText().equals("")) {
            engrave_text = engraveText.getText().toString();
            parameters += "&engrave_text=" + engrave_text;
            HelperClass.putSharedString(ViraniCustomizationActivity.this, "engrave_text", engrave_text);
        }
    }

    private void setMetalDetail() {
        for (int i = 0; i < metalList.size(); i++) {
            ImageView image = (ImageView) metalGrid.getChildAt(i).findViewById(R.id.imgCategory);
            String img = metalList.get(i).getName().toLowerCase().replace(" ", "_");
            img = img.substring(0, img.length() - 4);
            if (i == metalIndex)
                img += "_full";
            image.setImageResource(getResources().getIdentifier(img, "drawable", getPackageName()));
        }
        Option op = metalList.get(metalIndex);
        String goldK = op.getName().substring(op.getName().length() - 3);
        ((TextView) findViewById(R.id.metalDetails)).setText(String.format(getString(R.string.metal_alloy), op.getName(), goldK));
    }

    private void setDiamondDetail() {
        for (int i = 0; i < primaryQualityList.size(); i++) {
            if(i == primaryQuality) {
                primaryStone = -1;
                diaPrimaryGrid.getChildAt(i).setBackgroundResource(R.drawable.border_grey);
            }
            else
                diaPrimaryGrid.getChildAt(i).setBackgroundResource(R.drawable.border_transparent);
        }
        if(primaryQuality >= 0) {
            Option op = primaryQualityList.get(primaryQuality);
            ((TextView) findViewById(R.id.diaDetails)).setText(String.format(getString(R.string.diamond_details), op.getColor().toUpperCase(), op.getClarity().toUpperCase()));
        }
        else {
            ((TextView) findViewById(R.id.diaDetails)).setText("");
        }
    }

    private void setGemStoneDetail() {
        for (int i = 0; i < primaryStoneList.size(); i++) {
            if (i == primaryStone) {
                primaryQuality = -1;
                gemPrimaryGrid.getChildAt(i).setBackgroundResource(R.drawable.border_grey);
            } else
                gemPrimaryGrid.getChildAt(i).setBackgroundResource(R.drawable.border_transparent);
        }
        ((TextView) findViewById(R.id.gemDetails)).setText("");
    }

    private void getPrice() {
        dialog.show();
        setParameters();
        String url = getString(R.string.jewel_commerce) + getString(R.string.api) + "store/products/" + product.getId() + "/price?store_id=" + getString(R.string.store_id);
        url += parameters;

        HelperClass.getData(this, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    JSONObject productObject = (JSONObject) obj.get("price");
                    String productString = productObject.toString();

                    price = (new Gson()).fromJson(productString, Price.class);
                    setPriceData();

                } catch (Exception e) {
                    Toast.makeText(ViraniCustomizationActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(KeyValuePair error) {
                dialog.dismiss();
            }
        });
    }

    private void setPriceData() {
        double diamondAmount = 0, gemAmount = 0;
        List<ExtendedOption> diaOptions = price.getGem_options();
        if (diaOptions != null && !diaOptions.isEmpty()) {
            for (int i = 0; i < diaOptions.size(); i++) {
                Option diamondOption = getDiamondOption(i, diaOptions.get(i).getValue().getId());
                if (diamondOption != null) {
                    if (diamondOption.getType().equalsIgnoreCase(Utility.DIAMOND)) {
                        diamondAmount += diaOptions.get(i).getValue().getPrice();
                    } else {
                        gemAmount += diaOptions.get(i).getValue().getPrice();
                    }
                }
            }
        }
        double total = price.getMetal().getPrice() + diamondAmount + gemAmount + price.getEngrave_charge() + price.getMaking_charge();
        ((TextView) layout.getChildAt(0).findViewById(R.id.price)).setText(HelperClass.formatCurrency(getString(R.string.currency), price.getMetal().getPrice()));
        ((TextView) layout.getChildAt(1).findViewById(R.id.price)).setText(HelperClass.formatCurrency(getString(R.string.currency), diamondAmount));
        ((TextView) layout.getChildAt(3).findViewById(R.id.price)).setText(HelperClass.formatCurrency(getString(R.string.currency), price.getEngrave_charge()));
        ((TextView) layout.getChildAt(2).findViewById(R.id.price)).setText(HelperClass.formatCurrency(getString(R.string.currency), gemAmount));

        ((TextView) findViewById(R.id.totalPrice)).setText(HelperClass.formatCurrency(getString(R.string.currency), total));
    }

    private Option getDiamondOption(int index, String id) {
        List<Option> list = jewelleryProduct.getGem_options().get(index).getValues();
        if (list != null && !list.isEmpty()) {
            for (Option dop : list) {
                if (dop.getId().equals(id))
                    return dop;
            }
        }
        return null;
    }

    private int convertPixels(float pix) {
        return (int) (pix * dens);
    }
}
