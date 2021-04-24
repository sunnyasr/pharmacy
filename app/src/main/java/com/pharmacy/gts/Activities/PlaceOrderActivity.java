package com.pharmacy.gts.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
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
import com.pharmacy.gts.Database.Cart.CartDataSource;
import com.pharmacy.gts.Database.Cart.CartDatabase;
import com.pharmacy.gts.Database.Cart.LocalCartDataSource;
import com.pharmacy.gts.EventBus.AddressSelect;
import com.pharmacy.gts.PrefManager.UserManager;
import com.pharmacy.gts.R;
import com.pharmacy.gts.kprogresshud.KProgressHUD;
import com.sdsmdg.tastytoast.TastyToast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PlaceOrderActivity extends AppCompatActivity implements View.OnClickListener {

    private UserManager userManager;
    private CompositeDisposable compositeDisposable;
    private AddressDataSource addressDataSource;
    private ScrollView addressLayout;
    private KProgressHUD kProgressHUD;

    private EditText etName, etHno, etAddress, etPinCode, etCity, etMobile, etCoupon;

    //Selected Address
    private TextView tvSName, tvSAddress, tvSMobile, tvSScity, tvSPinCode, tvDefault, tvCouponStatus;
    private CardView selectedLayout, couponLayout;
    private String addressID = null, defaultSelect = "0";
    //Place Layout
    private CardView placeLayout;
    private TextView tvTotlaMRP, tvDiscountMRP, tvTotalAmt, tvGST;
    private double mTotalMRP = 0.00, mDiscountMRP = 0.00, mTotalAMT = 0.00, mGST;
    //Clear Cart
    private CartDataSource cartDataSource;
    private int couponid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);
        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Place order");
        setProgress();
        userManager = new UserManager(this);
        successToast(false, null);

        //Local Database
        compositeDisposable = new CompositeDisposable();
        addressDataSource = new LocalAddressDataSource(AddressDatabase.getInstance(this).addressDAO());
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDAO());
        mTotalMRP = getIntent().getDoubleExtra("amount", 0.0);
        initDefault();
        getAddress();
    }

    public void initDefault() {
        placeLayout = (CardView) findViewById(R.id.place_layout);
        selectedLayout = (CardView) findViewById(R.id.select_layout);
        couponLayout = (CardView) findViewById(R.id.layout_coupon);
        addressLayout = (ScrollView) findViewById(R.id.layout_address_add);
        addressLayout.setVisibility(View.GONE);

        //Selected Address
        tvSName = (TextView) findViewById(R.id.tv_name_default);
        tvSAddress = (TextView) findViewById(R.id.tv_address_default);
        tvSScity = (TextView) findViewById(R.id.tv_city_default);
        tvSPinCode = (TextView) findViewById(R.id.tv_pin_default);
        tvSMobile = (TextView) findViewById(R.id.tv_mobile_default);
        tvDefault = (TextView) findViewById(R.id.tv_default);
        tvDefault.setVisibility(View.GONE);

        //Coupon
        etCoupon = (EditText) findViewById(R.id.et_coupon);
        tvCouponStatus = (TextView) findViewById(R.id.tv_coupon_status);
        findViewById(R.id.btn_coupon).setOnClickListener(this);

        etCoupon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                applyCoupon();
            }
        });

        //Add Address
        etName = (EditText) findViewById(R.id.et_name);
        etMobile = (EditText) findViewById(R.id.et_phone);
        etHno = (EditText) findViewById(R.id.et_hno);
        etHno = (EditText) findViewById(R.id.et_hno);
        etAddress = (EditText) findViewById(R.id.et_address);
        etCity = (EditText) findViewById(R.id.et_city);
        etPinCode = (EditText) findViewById(R.id.et_postcode);

        //Place Order
        tvTotlaMRP = (TextView) findViewById(R.id.tv_total_mrp);
        tvDiscountMRP = (TextView) findViewById(R.id.tv_discount_mrp);
        tvTotalAmt = (TextView) findViewById(R.id.tv_total_amt);
        tvGST = (TextView) findViewById(R.id.tv_gst_amt);

        tvTotlaMRP.setText(getString(R.string.currency_symbol) + mTotalMRP);
        tvDiscountMRP.setText(getString(R.string.currency_symbol) + mDiscountMRP);

        mTotalAMT = mTotalMRP - mDiscountMRP;
        tvTotalAmt.setText(getString(R.string.currency_symbol) + mTotalAMT);

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
        findViewById(R.id.new_address).setOnClickListener(this);
        findViewById(R.id.btn_continue).setOnClickListener(this);

        calculateGST();
    }

    public void setProgress() {
        kProgressHUD = KProgressHUD.create(PlaceOrderActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }

    public void getAddress() {
        kProgressHUD.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, APIConfig.GET_ADDRESS + userManager.getMID(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                kProgressHUD.dismiss();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    if (jsonArray.length() == 0) {
                        addressLayout.setVisibility(View.VISIBLE);
                        placeLayout.setVisibility(View.GONE);
//                        clearAddress();
                    }

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        AddressItem addressItem = new AddressItem();
                        addressItem.setAddressid(jsonObject.getString("addressid"));
                        addressItem.setUid(jsonObject.getString("memberid"));
                        addressItem.setName(jsonObject.getString("name"));
                        addressItem.setPhone(jsonObject.getString("phone"));
                        addressItem.setAddress(jsonObject.getString("address"));
                        addressItem.setHno(jsonObject.getString("hno"));
                        addressItem.setPostcode(jsonObject.getString("postcode"));
                        addressItem.setCity(jsonObject.getString("city"));
                        addressItem.setDefautselect(jsonObject.getInt("addefault"));

                        compositeDisposable.add(addressDataSource.insertOrReplaceAll(addressItem)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
//                                    Toast.makeText(PlaceOrderActivity.this, "Address Added successfully", Toast.LENGTH_SHORT).show();
                                }, throwable -> {
                                    Toast.makeText(PlaceOrderActivity.this, "[ADD ADDRESS]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                }));


                    }

                    getDefautAddress();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PlaceOrderActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + userManager.getToken());
                return headers;
            }
        };

        MySingleton.getmInstanc(getApplicationContext()).addRequestQueue(stringRequest);
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
                        addressLayout.setVisibility(View.GONE);
                        selectedLayout.setVisibility(View.VISIBLE);
                        couponLayout.setVisibility(View.VISIBLE);
                        placeLayout.setVisibility(View.VISIBLE);
                        tvSName.setText(addressItem.getName());
                        tvSAddress.setText(addressItem.getAddress());
                        tvSScity.setText(addressItem.getCity());
                        tvSPinCode.setText(addressItem.getPostcode());
                        tvSMobile.setText(addressItem.getPhone());
                        addressID = addressItem.getAddressid();
                        tvDefault.setVisibility(View.VISIBLE);
//                        Toast.makeText(PlaceOrderActivity.this, "" + addressItem.getDefautselect(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e.getMessage().contains("Query returned empty")) {
                            addressLayout.setVisibility(View.VISIBLE);
                            selectedLayout.setVisibility(View.GONE);
                            couponLayout.setVisibility(View.GONE);
                            placeLayout.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(PlaceOrderActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.new_address) {
            startActivity(new Intent(getApplicationContext(), AddressActivity.class));
        }
        if (v.getId() == R.id.btn_address_submit) {
            addAddress();
        }
        if (v.getId() == R.id.btn_continue) {
            placeOrder();
        }

        if (v.getId() == R.id.btn_coupon) {
            applyCoupon();
        }
    }

    private void applyCoupon() {
        String coupon = etCoupon.getText().toString().trim().toUpperCase();
        mTotalMRP = 0.00;
        mDiscountMRP = 0.00;
        mTotalAMT = 0.00;

        mTotalMRP = getIntent().getDoubleExtra("amount", 0.0);
        tvTotlaMRP.setText(getString(R.string.currency_symbol) + mTotalMRP);
        tvDiscountMRP.setText(getString(R.string.currency_symbol) + mDiscountMRP);


        mTotalAMT = mTotalMRP - mDiscountMRP;
        tvTotalAmt.setText(getString(R.string.currency_symbol) + mTotalAMT);

        if (TextUtils.isEmpty(coupon)) {
            TastyToast.makeText(getApplicationContext(), getString(R.string.enter_coupon), TastyToast.LENGTH_LONG, TastyToast.INFO);
        } else {
            kProgressHUD.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, APIConfig.CHECK_COUPON, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    kProgressHUD.dismiss();

                    try {
                        JSONObject object = new JSONObject(response);

                        if (object.getBoolean("error")) {
                            couponid = 0;
                            tvCouponStatus.setVisibility(View.VISIBLE);
                            tvCouponStatus.setText(object.getString("msg"));
                            tvCouponStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                            int percent = 0;

                            mDiscountMRP = mTotalMRP * percent / 100;

                            tvDiscountMRP.setText(getString(R.string.currency_symbol) + mDiscountMRP);


                            mTotalAMT = (mTotalMRP - mDiscountMRP) + mGST;
                            tvTotalAmt.setText(getString(R.string.currency_symbol) + mTotalAMT);

//                            Toast.makeText(PlaceOrderActivity.this, "Error " + mTotalAMT, Toast.LENGTH_SHORT).show();
                        } else {
                            tvCouponStatus.setVisibility(View.VISIBLE);
                            tvCouponStatus.setText(coupon + " Applied successfully");
                            tvCouponStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                            int percent = object.getInt("percent");

                            mDiscountMRP = mTotalMRP * percent / 100;

                            tvDiscountMRP.setText(getString(R.string.currency_symbol) + mDiscountMRP);

                            mTotalAMT = (mTotalMRP - mDiscountMRP) + mGST;
                            tvTotalAmt.setText(getString(R.string.currency_symbol) + mTotalAMT);

                            couponid = object.getInt("couponid");


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    kProgressHUD.dismiss();
                    Toast.makeText(PlaceOrderActivity.this, "[APPLY COUPON]" + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                    params.put("coupon", coupon);
                    params.put("memberid", userManager.getMID());
                    params.put("total", String.valueOf(mTotalAMT));
                    return params;
                }
            };

            MySingleton.getmInstanc(getApplicationContext()).addRequestQueue(stringRequest);
        }


    }

    private void placeOrder() {

        if (TextUtils.isEmpty(addressID))
            TastyToast.makeText(getApplicationContext(), getString(R.string.select_address), TastyToast.LENGTH_LONG, TastyToast.INFO);
        else {
            kProgressHUD.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, APIConfig.PLACE_ORDER, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
//                    Toast.makeText(PlaceOrderActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                    kProgressHUD.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if (!jsonObject.getBoolean("error")) {
                            clearCart();
                            TastyToast.makeText(getApplicationContext(), jsonObject.getString("msg"), TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else
                            TastyToast.makeText(getApplicationContext(), jsonObject.getString("msg"), TastyToast.LENGTH_LONG, TastyToast.ERROR);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(PlaceOrderActivity.this, "[PLACE ORDER]" + error.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authentication", "Bearer " + userManager.getToken());
                    return headers;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("memberid", userManager.getMID());
                    params.put("addressid", addressID);
                    params.put("amount", String.valueOf(mTotalMRP));
                    params.put("totalamt", String.valueOf(mTotalAMT));
                    params.put("couponid", String.valueOf(couponid));
                    params.put("discount", String.valueOf(mDiscountMRP));
                    params.put("gstamt", String.valueOf(mGST));
                    Log.d("PLACE-GST", String.valueOf(params));
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(120000,
                    0, 0));
            MySingleton.getmInstanc(getApplicationContext()).addRequestQueue(stringRequest);
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onAddressSelectItem(AddressSelect event) {
        if (event.getAddressItem() != null) {
            tvSName.setText(event.getAddressItem().getName());
            tvSAddress.setText(event.getAddressItem().getAddress());
            tvSScity.setText(event.getAddressItem().getCity());
            tvSPinCode.setText(event.getAddressItem().getPostcode());
            tvSMobile.setText(event.getAddressItem().getPhone());
            addressID = event.getAddressItem().getAddressid();
            if (event.getAddressItem().getDefautselect() == 1)
                tvDefault.setVisibility(View.VISIBLE);
            else tvDefault.setVisibility(View.GONE);
//            Toast.makeText(this, "Event : "+event.getAddressItem().getAddressid(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
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
                            addressID = jsonObject.getString("adddressid");
                            AddressItem addressItem = new AddressItem();
                            addressItem.setAddressid(jsonObject.getString("adddressid"));
                            addressItem.setUid(userManager.getMID());
                            addressItem.setName(name);
                            addressItem.setPhone(mobile);
                            addressItem.setAddress(address);
                            addressItem.setHno(hno);
                            addressItem.setPostcode(pincode);
                            addressItem.setCity(city);
                            addressItem.setDefautselect(jsonObject.getInt("default"));

                            compositeDisposable.add(addressDataSource.insertOrReplaceAll(addressItem)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() -> {
                                        addressLayout.setVisibility(View.GONE);
                                        selectedLayout.setVisibility(View.VISIBLE);
                                        couponLayout.setVisibility(View.VISIBLE);
                                        placeLayout.setVisibility(View.VISIBLE);
                                        tvSName.setText(addressItem.getName());
                                        tvSAddress.setText(addressItem.getAddress());
                                        tvSScity.setText(addressItem.getCity());
                                        tvSPinCode.setText(addressItem.getPostcode());
                                        tvSMobile.setText(addressItem.getPhone());
                                        addressID = addressItem.getAddressid();
                                        tvDefault.setVisibility(View.VISIBLE);
                                        successToast(true, getString(R.string.address_added_success));
                                    }, throwable -> {
                                        Toast.makeText(PlaceOrderActivity.this, "[ADD ADDRESS]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }));


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(PlaceOrderActivity.this, "[ADD ADDRESS]" + error.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void clearCart() {
        cartDataSource.cleanCart(userManager.getMID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Integer integer) {
//                        Toast.makeText(MainActivity.this, "Clear cart successful!!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(PlaceOrderActivity.this, "[CART CLEAN]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearAddress() {
        addressDataSource.cleanAddress(userManager.getMID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Integer integer) {
//                        Toast.makeText(MainActivity.this, "Clear cart successful!!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(PlaceOrderActivity.this, "[ADDRESS CLEAN]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAddress();
    }

    public void checkAddress() {
        addressDataSource.getItemInAddress(addressID, userManager.getMID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<AddressItem>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onSuccess(AddressItem addressItem) {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e.getMessage().contains("Query returned empty")) {
                            getDefautAddress();
                        }
                    }
                });
    }

    private void calculateGST() {

        cartDataSource.sumGST(userManager.getMID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Double>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Double aDouble) {
                        mGST = aDouble;
                        mTotalAMT = aDouble + mTotalAMT;
                        tvGST.setText(new StringBuilder("₹").append(String.format("%.2f", aDouble)));
                        tvTotalAmt.setText(new StringBuilder("₹").append(String.format("%.2f", mTotalAMT)));
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!e.getMessage().contains("Query returned empty")) {
                            Toast.makeText(PlaceOrderActivity.this, "[SUM CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
