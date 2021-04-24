package com.pharmacy.gts.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.pharmacy.gts.Adapters.OrderProductAdapter;
import com.pharmacy.gts.App.APIConfig;
import com.pharmacy.gts.App.ComMethod;
import com.pharmacy.gts.App.MySingleton;
import com.pharmacy.gts.Models.OrderProductModel;
import com.pharmacy.gts.PrefManager.UserManager;
import com.pharmacy.gts.R;
import com.pharmacy.gts.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class OrderDetailActivity extends AppCompatActivity {

    private UserManager userManager;
    private KProgressHUD kProgressHUD;
    private ComMethod comMethod;

    private TextView tvOrderID, tvTotal, tvDiscount, tvGST,tvNett, tvQTY, tvDueNett, tvPaidStatus, tvOrderDate, tvDelivery;
    private TextView tvName, tvAddress, tvCity, tvPincode, tvMobile;

    private CardView orderLayout, addressLayout;

    //Product List
    private ArrayList<OrderProductModel> orderProductModelArrayList;
    private RecyclerView recyclerView;
    private OrderProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Order Details");
        setProgress();
        userManager = new UserManager(this);
        comMethod = new ComMethod(this);
        init();

        orderProductModelArrayList = new ArrayList<>();
        adapter = new OrderProductAdapter(this, orderProductModelArrayList);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
        recyclerView.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));


        if (comMethod.checkNetworkConnection()) {
            getDetails(getIntent().getStringExtra("morderid"));
        } else comMethod.alertDialog("Network", getString(R.string.no_network), true, null);
    }

    public void init() {

        orderLayout = (CardView) findViewById(R.id.order_layout);
        addressLayout = (CardView) findViewById(R.id.address_layout);

        orderLayout.setVisibility(View.GONE);
        addressLayout.setVisibility(View.GONE);

        tvOrderID = (TextView) findViewById(R.id.tv_order);
        tvTotal = (TextView) findViewById(R.id.tv_total_amt);
        tvDiscount = (TextView) findViewById(R.id.tv_discount);
        tvGST = (TextView) findViewById(R.id.tv_gst);
        tvNett = (TextView) findViewById(R.id.tv_nett);
        tvQTY = (TextView) findViewById(R.id.tv_qty);
        tvDueNett = (TextView) findViewById(R.id.tv_due_amt);
        tvPaidStatus = (TextView) findViewById(R.id.tv_paid_status);
        tvOrderDate = (TextView) findViewById(R.id.tv_order_date);
        tvDelivery = (TextView) findViewById(R.id.tv_delivery);

        tvName = (TextView) findViewById(R.id.tv_name);
        tvAddress = (TextView) findViewById(R.id.tv_address);
        tvCity = (TextView) findViewById(R.id.tv_city);
        tvPincode = (TextView) findViewById(R.id.tv_pincode);
        tvMobile = (TextView) findViewById(R.id.tv_mobile);
    }

    public void getDetails(String id) {
        kProgressHUD.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, APIConfig.GET_ORDER_DETAIL + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                kProgressHUD.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONObject master = jsonObject.getJSONObject("masterorder");
                    JSONObject address = jsonObject.getJSONObject("address");

                    //Master Details

                    tvOrderID.setText("Order No. #" + master.getString("morderid"));
                    tvTotal.setText(getString(R.string.currency_symbol) + master.getString("totalamount"));
                    tvDiscount.setText(getString(R.string.currency_symbol) +master.getString("discount"));
                    tvGST.setText(getString(R.string.currency_symbol) +master.getString("gstamt") );
                    tvNett.setText(getString(R.string.currency_symbol) + master.getString("nett"));
                    tvQTY.setText(master.getString("totalqty"));
                    tvDueNett.setText(getString(R.string.currency_symbol) + master.getString("duenett"));

                    if (master.getString("paystatus").equals("1"))
                        tvPaidStatus.setText("Paid");
                    else {
                        tvPaidStatus.setTextColor(getResources().getColor(R.color.red));
                        tvPaidStatus.setText("Unpaid");
                    }
                    tvOrderDate.setText(comMethod.dateConvert(master.getString("orderdate")));

                    if (master.getString("status").equals("1"))
                        tvDelivery.setText("Delivered");
                    else {
                        tvDelivery.setTextColor(getResources().getColor(R.color.red));
                        tvDelivery.setText("Pending");
                    }
                    orderLayout.setVisibility(View.VISIBLE);

                    //Address
                    tvName.setText(address.getString("name"));
                    tvCity.setText(address.getString("city"));
                    tvPincode.setText("Pin Code " + address.getString("postcode"));
                    tvMobile.setText("M : " + address.getString("phone"));
                    tvAddress.setText( address.getString("hno") + " " + address.getString("address"));
                    addressLayout.setVisibility(View.VISIBLE);

                    //Products
                    JSONArray array = jsonObject.getJSONArray("products");

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        OrderProductModel model = new OrderProductModel();

                        model.setOrderid(object.getString("orderid"));
                        model.setMorderid(object.getString("morderid"));
                        model.setProductid(object.getString("productid"));
                        model.setAddressid(object.getString("addressid"));
                        model.setPrice(object.getString("price"));
                        model.setGst(object.getInt("gst"));
                        model.setQuantity(object.getInt("quantity"));
                        model.setAmount(object.getInt("amount"));
                        model.setDiscount(object.getString("discount"));
                        model.setNett(object.getString("nett"));
                        model.setStatus(object.getString("status"));
                        model.setOrderdate(object.getString("orderdate"));
                        model.setDelivereddate(object.getString("delivereddate"));
                        model.setActivated(object.getString("activated"));
                        model.setEnabled(object.getString("enabled"));
                        model.setPname(object.getString("pname"));
                        model.setMrp(object.getInt("mrp"));
                        model.setMfr(object.getString("mfr"));
                        model.setSalts(object.getString("salts"));
                        model.setImage(object.getString("image"));
                        model.setPcode(object.getString("pcode"));
                        model.setBrand(object.getString("brand"));

                        orderProductModelArrayList.add(model);

                    }

                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                kProgressHUD.dismiss();
                Toast.makeText(OrderDetailActivity.this, "[GET DETAIL ORDER]" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + userManager.getToken());
                return headers;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstanc(getApplicationContext()).addRequestQueue(stringRequest);
    }

    public void setProgress() {
        kProgressHUD = KProgressHUD.create(OrderDetailActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
