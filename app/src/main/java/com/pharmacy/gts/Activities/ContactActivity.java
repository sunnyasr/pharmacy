package com.pharmacy.gts.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.pharmacy.gts.App.APIConfig;
import com.pharmacy.gts.App.ComMethod;
import com.pharmacy.gts.App.MySingleton;
import com.pharmacy.gts.R;
import com.pharmacy.gts.kprogresshud.KProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ContactActivity extends AppCompatActivity {
    private KProgressHUD kProgressHUD;
    private SharedPreferences sharedPreferences;
    private TextView tvMobile, tvEmail, tvAddress;
    private LinearLayout mobileLayout, emailLayout;
    private ComMethod comMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Contact Us");
        comMethod = new ComMethod(this);
        sharedPreferences = getSharedPreferences("mLoginUserPref", Context.MODE_PRIVATE);
        setProgress();
        mobileLayout = (LinearLayout) findViewById(R.id.layout_mobile);
        emailLayout = (LinearLayout) findViewById(R.id.layout_email);
        tvMobile = (TextView) findViewById(R.id.tv_mobile);
        tvEmail = (TextView) findViewById(R.id.tv_email);
        tvAddress = (TextView) findViewById(R.id.tv_address);

//        findViewById(R.id.fab_whatsapp).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                comMethod.openWhatsApp(ContactActivity.this);
//            }
//        });

        getCompany();
    }

    public void getCompany() {
        kProgressHUD.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, APIConfig.GET_CONTACT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                kProgressHUD.dismiss();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("mobile").equals("null"))
                        mobileLayout.setVisibility(View.GONE);
                    tvMobile.setText(jsonObject.getString("mobile"));
                    if (jsonObject.getString("email").equals("null"))
                        emailLayout.setVisibility(View.GONE);
                    tvEmail.setText(jsonObject.getString("email"));
                    tvAddress.setText(jsonObject.getString("address") + " " + jsonObject.getString("city") + " " + jsonObject.getString("statename") + ", Country "
                            + jsonObject.getString("countryname") + ", Pin Code " + jsonObject.getString("pincode"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                kProgressHUD.dismiss();
                if (error instanceof AuthFailureError) {
                    comMethod.logout();
                    finish();
                }

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", " application/json");
                headers.put("Authorization", "Bearer " + sharedPreferences.getString("tokenno", ""));
                return headers;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstanc(getApplicationContext()).addRequestQueue(stringRequest);

    }

    public void setProgress() {
        kProgressHUD = KProgressHUD.create(ContactActivity.this)
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
