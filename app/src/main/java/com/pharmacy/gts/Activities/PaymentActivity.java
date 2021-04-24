package com.pharmacy.gts.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.pharmacy.gts.Adapters.OrderAdapter;
import com.pharmacy.gts.App.APIConfig;
import com.pharmacy.gts.App.ComMethod;
import com.pharmacy.gts.App.MySingleton;
import com.pharmacy.gts.Models.OrderModel;
import com.pharmacy.gts.PrefManager.UserManager;
import com.pharmacy.gts.R;
import com.pharmacy.gts.kprogresshud.KProgressHUD;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class PaymentActivity extends AppCompatActivity implements PaymentResultListener, View.OnClickListener {
    private static final String TAG = PaymentActivity.class.getSimpleName();

    private UserManager userManager;
    private ComMethod comMethod;
    private KProgressHUD kProgressHUD;
    private String transid = "";

    private EditText etAmount;
    private TextView tvBalance;

    private ArrayList<OrderModel> orderModelArrayList;
    private OrderAdapter adapter;

    private CardView payLayout;
    private TextView tvNotFound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getString(R.string.pay_online));
        setProgress();
        userManager = new UserManager(this);
        comMethod = new ComMethod(this);

        payLayout = (CardView) findViewById(R.id.pay_layout);
        payLayout.setVisibility(View.GONE);

        tvNotFound = (TextView) findViewById(R.id.tv_not_found);
        etAmount = (EditText) findViewById(R.id.et_amount);
        tvBalance = (TextView) findViewById(R.id.tv_balance);
        tvBalance.setText(getString(R.string.due_balance) + " : " + getString(R.string.currency_symbol) + getIntent().getStringExtra("balance"));
        findViewById(R.id.btn_pay).setOnClickListener(this);

        if (Double.parseDouble(getIntent().getStringExtra("balance")) > 0)
            payLayout.setVisibility(View.VISIBLE);

        //Pay History
        orderModelArrayList = new ArrayList<>();
        adapter = new OrderAdapter(this, orderModelArrayList);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
        recyclerView.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));

        getPayHistory(getIntent().getStringExtra("morderid"));
    }

    public void setProgress() {
        kProgressHUD = KProgressHUD.create(PaymentActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }

    public void startPayment(String transid, int amount) {

        final Activity activity = this;

        Checkout checkout = new Checkout();
        checkout.setImage(R.mipmap.ic_launcher);

        try {
            JSONObject options = new JSONObject();
            options.put("name", userManager.getCompany());
//            options.put("description", "Pharmacy description");
//            //You can omit the image option to fetch the image from dashboard
//            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");

            options.put("description", "Order #" + transid);
            options.put("currency", "INR");
            options.put("amount", amount * 100);

            JSONObject preFill = new JSONObject();
            preFill.put("email", userManager.getEmail());
            preFill.put("contact", userManager.getMobile());

            options.put("prefill", preFill);

            checkout.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        try {
//            Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
            updatePay(razorpayPaymentID);
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentSuccess", e);
        }
    }


    @Override
    public void onPaymentError(int code, String response) {
        try {
            updatePay(response + "test");
//            Toast.makeText(this, "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentError", e);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_pay) {
            String amount = etAmount.getText().toString().trim();
            Double.parseDouble(tvBalance.getText().toString().trim().replace("Due Balance : ₹", ""));
            int balance = (int) Double.parseDouble(tvBalance.getText().toString().trim().replace("Due Balance : ₹", ""));

            if (TextUtils.isEmpty(amount))
                TastyToast.makeText(getApplicationContext(), getString(R.string.enter_amount), TastyToast.LENGTH_LONG, TastyToast.INFO);

            else if (balance < Integer.parseInt(amount))
                TastyToast.makeText(getApplicationContext(), getString(R.string.invalid_amount), TastyToast.LENGTH_LONG, TastyToast.INFO);

            else
                pay(amount);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    public void pay(String amount) {
        kProgressHUD.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIConfig.PAY_NOW, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                kProgressHUD.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (!jsonObject.getBoolean("error")) {
                        transid = jsonObject.getString("transid");
                        startPayment(jsonObject.getString("transid"), Integer.parseInt(amount));
                    } else {
                        comMethod.alertDialog("Payment", jsonObject.getString("msg"), true, null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
                params.put("morderid", getIntent().getStringExtra("morderid"));
                params.put("amount", amount);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(120000,
                0, 0));
        MySingleton.getmInstanc(getApplicationContext()).addRequestQueue(stringRequest);
    }

    public void updatePay(String paytransid) {
        kProgressHUD.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIConfig.PAY_UPDATE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                kProgressHUD.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (!jsonObject.getBoolean("error")) {
                        tvNotFound.setVisibility(View.GONE);
                        JSONObject object = jsonObject.getJSONObject("data");
                        OrderModel model = new OrderModel();
                        model.setTransactionid(object.getString("transactionid"));
                        model.setMorderid(object.getString("morderid"));
                        model.setMemberid(object.getString("memberid"));
                        model.setDueblc(object.getString("dueblc"));
                        model.setAmtreceived(object.getString("amtreceived"));
                        model.setTransid(object.getString("transid"));
                        model.setCreated_date(object.getString("created_date"));
                        model.setActivated(object.getString("activated"));
                        model.setEnabled(object.getString("enabled"));

                        etAmount.setText("");
                        if (object.getDouble("dueblc") > 0) {
                            tvBalance.setText(getString(R.string.due_balance) + " : " + getString(R.string.currency_symbol) + object.getString("dueblc"));
                            payLayout.setVisibility(View.VISIBLE);
                        } else payLayout.setVisibility(View.GONE);

                        orderModelArrayList.add(0, model);
                        adapter.notifyDataSetChanged();
                        comMethod.alertDialog("Payment", jsonObject.getString("msg"), false, null);
                    } else {
                        comMethod.alertDialog("Payment", jsonObject.getString("msg"), true, null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
                params.put("morderid", getIntent().getStringExtra("morderid"));
                params.put("transid", transid);
                params.put("paytransid", paytransid);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(120000,
                0, 0));
        MySingleton.getmInstanc(getApplicationContext()).addRequestQueue(stringRequest);
    }


    public void getPayHistory(String id) {
        kProgressHUD.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, APIConfig.GET_PAY_HISTORY + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                kProgressHUD.dismiss();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    if (jsonArray.length() == 0) {
                        tvNotFound.setVisibility(View.VISIBLE);
                    } else {
                        tvNotFound.setVisibility(View.GONE);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            OrderModel model = new OrderModel();
                            model.setTransactionid(jsonObject.getString("transactionid"));
                            model.setMorderid(jsonObject.getString("morderid"));
                            model.setMemberid(jsonObject.getString("memberid"));
                            model.setDueblc(jsonObject.getString("dueblc"));
                            model.setAmtreceived(jsonObject.getString("amtreceived"));
                            model.setTransid(jsonObject.getString("transid"));
                            model.setCreated_date(jsonObject.getString("created_date"));
                            model.setActivated(jsonObject.getString("activated"));
                            model.setEnabled(jsonObject.getString("enabled"));
                            orderModelArrayList.add(model);

                        }
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PaymentActivity.this, "[PAY HISTORY]" + error.getMessage(), Toast.LENGTH_SHORT).show();
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

}
