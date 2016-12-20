package com.eComNation.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.eComNation.R;
import com.ecomnationmobile.library.Common.ECNCallback;
import com.ecomnationmobile.library.Common.EComNationActivity;
import com.ecomnationmobile.library.Common.HelperClass;
import com.ecomnationmobile.library.Control.CustomEditText;
import com.ecomnationmobile.library.Control.CustomSpinner;
import com.ecomnationmobile.library.Control.ProgressDialogView;
import com.ecomnationmobile.library.Data.Address;
import com.ecomnationmobile.library.Data.Country;
import com.ecomnationmobile.library.Data.KeyValuePair;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abhi on 27-10-2015.
 */
public class UpdateAddressActivity extends EComNationActivity {

    public Address ADDRESS;
    long countryIndex;
    int statesIndex;
    long id;
    List<KeyValuePair> States;
    List<Country> Countries;
    ProgressDialog dialog;
    View progressBar, emptyView;
    CustomEditText firstName, lastName, address1, address2, city, mobile, pinCode;
    CustomEditText stateText;
    CustomSpinner stateSpin, country;

    @Override
    public void onCreate(Bundle savedInstantState) {
        super.onCreate(savedInstantState);
        setContentView(R.layout.shipping_address);

        dialog = new ProgressDialogView(this, "Updating...", R.drawable.progress);

        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.empty_View);
        emptyView.findViewById(R.id.btnRetry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAddress();
            }
        });

        Countries = new ArrayList<>();

        States = new ArrayList<>();

        id = getIntent().getLongExtra("id", 0);

        if (id == 0) {
            ADDRESS = new Address();
            ((TextView) findViewById(R.id.txtAddressHeader)).setText(getString(R.string.add_address));
            progressBar.setVisibility(View.GONE);
        } else {
            getAddress();
            ((TextView) findViewById(R.id.txtAddressHeader)).setText("Update Address");
        }

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        firstName = (CustomEditText) findViewById(R.id.shippingFirstName);
        firstName.init(getString(R.string.first_name));
        firstName.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

        lastName = (CustomEditText) findViewById(R.id.shippingLastName);
        lastName.init(getString(R.string.last_name));
        lastName.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

        mobile = (CustomEditText) findViewById(R.id.shippingMobile);
        mobile.init(getString(R.string.mobile));
        mobile.setInputType(InputType.TYPE_CLASS_PHONE);

        address1 = (CustomEditText) findViewById(R.id.shippingAddress1);
        address1.init(getString(R.string.address1));

        address2 = (CustomEditText) findViewById(R.id.shippingAddress2);
        address2.init(getString(R.string.address2));

        city = (CustomEditText) findViewById(R.id.shippingCity);
        city.init(getString(R.string.city));

        pinCode = (CustomEditText) findViewById(R.id.shippingPinCode);
        pinCode.init(getString(R.string.pin_code));

        stateSpin = (CustomSpinner) findViewById(R.id.stateSpinner);
        stateSpin.init(getString(R.string.state));

        stateText = (CustomEditText) findViewById(R.id.stateText);
        stateText.init(getString(R.string.state));

        stateSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                statesIndex = States.get(position).getValue();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Type listType = new TypeToken<List<Country>>() {
        }.getType();
        Countries = (new Gson()).fromJson(HelperClass.getSharedString(getApplicationContext(), "countries"), listType);
        List<String> countries = new ArrayList<>();
        for (Country c : Countries) {
            countries.add(c.getName());
        }
        country = (CustomSpinner) findViewById(R.id.country);
        country.init(getString(R.string.country));
        country.setSpinner(countries);
        country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                countryIndex = Countries.get(position).getId();
                getStates(countryIndex);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        (findViewById(R.id.txtSaveAddress)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String params = "";
                String value = firstName.getText().trim();
                if (value.equals("")) {
                    Toast.makeText(UpdateAddressActivity.this, "First name cannot be blank", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    params += "first_name=" + encode(value);
                }

                value = lastName.getText().trim();
                if (value.equals("")) {
                    Toast.makeText(UpdateAddressActivity.this, "Last name cannot be blank", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    params += "&last_name=" + encode(value);
                }

                value = mobile.getText().trim();
                if (value.equals("")) {
                    Toast.makeText(UpdateAddressActivity.this, "Mobile number cannot be blank", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    params += "&phone=" + encode(value);
                }

                value = address1.getText().trim();
                if (value.equals("")) {
                    Toast.makeText(UpdateAddressActivity.this, "Address line 1 cannot be blank", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    params += "&address1=" + encode(value);
                }

                params += "&address2=" + encode(address2.getText().trim());

                params += "&country_id=" + countryIndex;

                if (statesIndex != 0)
                    params += "&state_id=" + statesIndex;
                else {
                    value = stateText.getText().trim();
                    if (value.equals("")) {
                        Toast.makeText(UpdateAddressActivity.this, "State cannot be blank", Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        params += "&state_name="+encode(value);
                    }
                }

                value = city.getText().trim();
                if (value.equals("")) {
                    Toast.makeText(UpdateAddressActivity.this, "City cannot be blank", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    params += "&city=" + encode(value);
                }

                value = pinCode.getText().trim();
                if (value.equals("")) {
                    Toast.makeText(UpdateAddressActivity.this, "Pin code cannot be blank", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    params += "&zipcode=" + encode(value);
                }

                if (id == 0)
                    postData(params);
                else
                    putData(params);
            }
        });
    }

    private void getAddress() {
        progressBar.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        String url = getString(R.string.production_base_url) + getString(R.string.api) + "store/customer/addresses/"+id+"?access_token=" + HelperClass.getSharedString(this, getString(R.string.access_token));
        HelperClass.getData(this, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    JSONObject addressObject = (JSONObject) obj.get("address");
                    ADDRESS = (new Gson()).fromJson(addressObject.toString(), Address.class);

                    setUpAddress();
                } catch (Exception e) {
                    Toast.makeText(UpdateAddressActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(KeyValuePair error) {
                displayError(error.getKey());
            }
        });
    }

    private void displayError(String error) {
        emptyView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        ((TextView)emptyView.findViewById(R.id.txtMessage)).setText(error);
    }

    private void setUpAddress() {
        if(ADDRESS != null) {
            emptyView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);

            if (ADDRESS.getFirst_name() != null)
                firstName.setText(ADDRESS.getFirst_name().trim());

            if (ADDRESS.getLast_name() != null)
                lastName.setText(ADDRESS.getLast_name().trim());

            if (ADDRESS.getPhone() != null)
                mobile.setText(ADDRESS.getPhone().trim());

            if (ADDRESS.getAddress1() != null)
                address1.setText(ADDRESS.getAddress1().trim());

            if (ADDRESS.getAddress2() != null)
                address2.setText(ADDRESS.getAddress2().trim());
            else
                address2.setText("");

            if (ADDRESS.getCity() != null)
                city.setText(ADDRESS.getCity().trim());

            if (ADDRESS.getZipcode() != null)
                pinCode.setText(ADDRESS.getZipcode().trim());

            country.setSelection(getCountry(ADDRESS.getCountry_id()));
        }
    }

    private String encode(String value) {
        String encodedValue = "";
        try {
            encodedValue = URLEncoder.encode(value, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodedValue;
    }

    private int getCountry(long id) {
        for (int i = 0; i < Countries.size(); i++) {
            if (Countries.get(i).getId() == id) {
                countryIndex = id;
                getStates(id);
                return i;
            }
        }
        return -1;
    }

    private int getState(int id) {
        if (States != null) {
            for (int i = 0; i < States.size(); i++) {
                if (States.get(i).getValue() == id) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void postData(String params) {
        HelperClass.hideKeyboard(this);
        dialog.show();
        String url = getString(R.string.production_base_url) + getString(R.string.api) + "store/customer/addresses?" + params + "&access_token=" + HelperClass.getSharedString(this, getString(R.string.access_token));

        HelperClass.postData(this, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    JSONObject addressObject = (JSONObject) obj.get("address");
                    HelperClass.putSharedString(UpdateAddressActivity.this, "address", addressObject.toString());
                    Toast.makeText(UpdateAddressActivity.this, "Address updated", Toast.LENGTH_SHORT).show();
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(KeyValuePair error) {
                dialog.dismiss();
                Toast.makeText(UpdateAddressActivity.this, error.getKey(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void putData(String params) {
        HelperClass.hideKeyboard(this);
        dialog.show();
        String url = getString(R.string.production_base_url) + getString(R.string.api) + "store/customer/addresses/" + ADDRESS.getId() + "?" + params + "&access_token=" + HelperClass.getSharedString(this, getString(R.string.access_token));

        HelperClass.putData(this, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    JSONObject addressObject = (JSONObject) obj.get("address");
                    HelperClass.putSharedString(UpdateAddressActivity.this, "address", addressObject.toString());
                    Toast.makeText(UpdateAddressActivity.this, "Address updated", Toast.LENGTH_SHORT).show();
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(KeyValuePair error) {
                dialog.dismiss();
                Toast.makeText(UpdateAddressActivity.this, error.getKey(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setStatesSpinner() {
        List<String> list = new ArrayList<>();
        for (KeyValuePair k : States) {
            list.add(k.getKey());
        }
        stateSpin.setSpinner(list);
    }

    private void getStates(long id) {
        dialog.setMessage("Please wait...");
        dialog.show();
        final Gson gson = new Gson();

        String url = getString(R.string.production_base_url) + getString(R.string.api) + "countries/" + id + "/states";

        HelperClass.getData(this, url, new ECNCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String countryString = obj.get("states").toString();

                    Type listType = new TypeToken<List<KeyValuePair>>() {
                    }.getType();
                    States = gson.fromJson(countryString, listType);

                    if (States != null && !States.isEmpty()) {
                        setStatesSpinner();
                        statesIndex = ADDRESS.getState_id();
                        int str = getState(statesIndex);
                        if (str == -1) {
                            stateSpin.setSelection(0);
                        } else {
                            stateSpin.setSelection(str);
                        }
                        stateSpin.setVisibility(View.VISIBLE);
                        stateText.setVisibility(View.GONE);

                    } else {
                        stateText.setText(ADDRESS.getState_name());
                        stateSpin.setVisibility(View.GONE);
                        stateText.setVisibility(View.VISIBLE);
                        statesIndex = 0;
                    }
                    dialog.dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(KeyValuePair error) {
                dialog.dismiss();
            }
        });
    }
}