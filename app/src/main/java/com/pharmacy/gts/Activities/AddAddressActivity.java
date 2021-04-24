package com.pharmacy.gts.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.pharmacy.gts.App.APIConfig;
import com.pharmacy.gts.App.MySingleton;
import com.pharmacy.gts.Database.Address.AddressDataSource;
import com.pharmacy.gts.Database.Address.AddressDatabase;
import com.pharmacy.gts.Database.Address.AddressItem;
import com.pharmacy.gts.Database.Address.LocalAddressDataSource;
import com.pharmacy.gts.PrefManager.UserManager;
import com.pharmacy.gts.R;
import com.pharmacy.gts.kprogresshud.KProgressHUD;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AddAddressActivity extends AppCompatActivity implements View.OnClickListener {

    private UserManager userManager;
    private KProgressHUD kProgressHUD;
    private CompositeDisposable compositeDisposable;
    private AddressDataSource addressDataSource;
    private EditText etName, etHno, etAddress, etPinCode, etCity, etMobile;
    private String defaultSelect = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Add a New Address");
        setProgress();
        userManager = new UserManager(this);
        successToast(false, null);

        //Local Database
        compositeDisposable = new CompositeDisposable();
        addressDataSource = new LocalAddressDataSource(AddressDatabase.getInstance(this).addressDAO());

        //Add Address
        etName = (EditText) findViewById(R.id.et_name);
        etMobile = (EditText) findViewById(R.id.et_phone);
        etHno = (EditText) findViewById(R.id.et_hno);
        etHno = (EditText) findViewById(R.id.et_hno);
        etAddress = (EditText) findViewById(R.id.et_address);
        etCity = (EditText) findViewById(R.id.et_city);
        etPinCode = (EditText) findViewById(R.id.et_postcode);

        AppCompatCheckBox checkbox = (AppCompatCheckBox) findViewById(R.id.checkbox_default);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // show password
                    defaultSelect = "1";
                } else {
                    // hide password
                    defaultSelect = "0";
                }
            }
        });

        findViewById(R.id.btn_address_submit).setOnClickListener(this);
    }

    public void setProgress() {
        kProgressHUD = KProgressHUD.create(AddAddressActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }

    public void addAddress() {

        String name = etName.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();
        String hno = etHno.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String pincode = etPinCode.getText().toString().trim();

        if (TextUtils.isEmpty(name))
            TastyToast.makeText(getApplicationContext(), getString(R.string.enter_name), TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(mobile))
            TastyToast.makeText(getApplicationContext(), getString(R.string.enter_mobile), TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (mobile.length() < 10)
            TastyToast.makeText(getApplicationContext(), getString(R.string.enter_valid_mobile), TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(hno))
            TastyToast.makeText(getApplicationContext(), getString(R.string.enter_house_no), TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(address))
            TastyToast.makeText(getApplicationContext(), getString(R.string.enter_address), TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(city))
            TastyToast.makeText(getApplicationContext(), getString(R.string.enter_city), TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(pincode))
            TastyToast.makeText(getApplicationContext(), getString(R.string.enter_pincode), TastyToast.LENGTH_LONG, TastyToast.INFO);
        else {
            kProgressHUD.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, APIConfig.ADD_ADDRESS, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    kProgressHUD.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (!jsonObject.getBoolean("error")) {
                            AddressItem addressItem = new AddressItem();
                            addressItem.setAddressid(jsonObject.getString("adddressid"));
                            addressItem.setUid(userManager.getMID());
                            addressItem.setName(name);
                            addressItem.setPhone(mobile);
                            addressItem.setAddress(address);
                            addressItem.setHno(hno);
                            addressItem.setPostcode(pincode);
                            addressItem.setCity(city);
                            addressItem.setDefautselect(Integer.parseInt(defaultSelect));

                            if (defaultSelect.equals("1")) {
                                getDefautAddress();
                            }

                            compositeDisposable.add(addressDataSource.insertOrReplaceAll(addressItem)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() -> {
                                        onBackPressed();
                                        successToast(true, getString(R.string.address_added_success));
                                        finish();
                                    }, throwable -> {
                                        Toast.makeText(AddAddressActivity.this, "[ADD ADDRESS]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }));


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(AddAddressActivity.this, "[ADD ADDRESS]" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + userManager.getToken());
                    return headers;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("memberid", userManager.getMID());
                    params.put("name", name);
                    params.put("phone", mobile);
                    params.put("address", address);
                    params.put("hno", hno);
                    params.put("city", city);
                    params.put("postcode", pincode);
                    params.put("addefault", defaultSelect);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(120000,
                    0, 0));
            MySingleton.getmInstanc(getApplicationContext()).addRequestQueue(stringRequest);
        }
    }

    public void successToast(boolean show, String msg) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_layout,
                findViewById(R.id.custom_toast_container));

        TextView text = layout.findViewById(R.id.tv_toast);
        text.setText(msg);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 150);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        if (show)
            toast.show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_address_submit) {
            addAddress();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    private void getDefautAddress() {
        addressDataSource.getDefaultSelected("1", userManager.getMID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<AddressItem>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(AddressItem addressItem) {
                        updateAddress(addressItem);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e.getMessage().contains("Query returned empty")) {

                        } else {
                            Toast.makeText(AddAddressActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateAddress(AddressItem item) {
        AddressItem addressItem = new AddressItem();
        addressItem.setAddressid(item.getAddressid());
        addressItem.setUid(item.getUid());
        addressItem.setName(item.getName());
        addressItem.setPhone(item.getPhone());
        addressItem.setAddress(item.getAddress());
        addressItem.setHno(item.getHno());
        addressItem.setPostcode(item.getPostcode());
        addressItem.setCity(item.getCity());
        addressItem.setDefautselect(0);
        addressDataSource.updateAddressItems(addressItem)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Integer integer) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(AddAddressActivity.this, "[UPDATE ADDRESS]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
