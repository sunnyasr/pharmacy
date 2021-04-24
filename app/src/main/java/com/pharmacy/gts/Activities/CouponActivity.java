package com.pharmacy.gts.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.pharmacy.gts.Adapters.CouponAdapter;
import com.pharmacy.gts.App.APIConfig;
import com.pharmacy.gts.App.ComMethod;
import com.pharmacy.gts.App.MySingleton;
import com.pharmacy.gts.EventBus.Coupon;
import com.pharmacy.gts.Models.CouponModel;
import com.pharmacy.gts.PrefManager.UserManager;
import com.pharmacy.gts.R;
import com.pharmacy.gts.kprogresshud.KProgressHUD;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CouponActivity extends AppCompatActivity {

    private CouponAdapter adapter;
    private ArrayList<CouponModel> arrayList;
    private UserManager userManager;
    private ComMethod comMethod;
    private KProgressHUD kProgressHUD;

    private ClipboardManager myClipboard;
    private ClipData myClip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);

        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getString(R.string.coupons));
        setProggress();
        userManager = new UserManager(this);
        comMethod = new ComMethod(this);
        arrayList = new ArrayList<>();
        adapter = new CouponAdapter(this, arrayList);
        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        getCoupons();
    }

    private void setProggress() {

        kProgressHUD = new KProgressHUD(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }

    public void getCoupons() {
        kProgressHUD.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, APIConfig.GET_COUPON + userManager.getMID(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                arrayList.clear();
                kProgressHUD.dismiss();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    if (jsonArray.length() == 0) {
                        Toast.makeText(CouponActivity.this, "No Couponsl", Toast.LENGTH_SHORT).show();
                    } else {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            CouponModel model = new CouponModel();
                            JSONObject object = jsonArray.getJSONObject(i);

                            model.setCouponid(object.getString("couponid"));
                            model.setCoupon(object.getString("coupon"));
                            model.setImagecoupon(object.getString("imagecoupon"));
                            model.setDescription(object.getString("description"));
                            model.setStartdate(object.getString("startdate"));
                            model.setEnddate(object.getString("enddate"));
                            model.setMemberid(object.getString("memberid"));
                            model.setMinpurchase(object.getString("minpurchase"));
                            model.setPercent(object.getString("percent"));
                            arrayList.add(model);
                        }

                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CouponActivity.this, "[COUPON]" + error.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Subscribe(sticky = false, threadMode = ThreadMode.MAIN)
    public void onCouponEvent(Coupon coupon) {
        if (coupon != null) {
            myClip = ClipData.newPlainText("text", coupon.getCouponModel().getCoupon());
            myClipboard.setPrimaryClip(myClip);
            Toast.makeText(this, "Copied : " + coupon.getCouponModel().getCoupon(), Toast.LENGTH_SHORT).
                    show();
        }
    }
}