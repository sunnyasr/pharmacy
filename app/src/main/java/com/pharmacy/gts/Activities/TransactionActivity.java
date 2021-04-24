package com.pharmacy.gts.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.pharmacy.gts.Adapters.OrderAdapter;
import com.pharmacy.gts.App.APIConfig;
import com.pharmacy.gts.App.MySingleton;
import com.pharmacy.gts.Models.OrderModel;
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

public class TransactionActivity extends AppCompatActivity {

    private ArrayList<OrderModel> orderModelArrayList;
    private OrderAdapter adapter;
    private KProgressHUD kProgressHUD;
    private TextView tvNotFound;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getString(R.string.transactions));
        setProgress();
        userManager = new UserManager(this);
        tvNotFound = (TextView) findViewById(R.id.tv_not_found);
        orderModelArrayList = new ArrayList<>();
        adapter = new OrderAdapter(this, orderModelArrayList);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
        recyclerView.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));

        getPayHistory(userManager.getMID());
    }

    public void setProgress() {
        kProgressHUD = KProgressHUD.create(TransactionActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }

    public void getPayHistory(String id) {
        kProgressHUD.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, APIConfig.GET_TRANSATIONS + id, new Response.Listener<String>() {
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
                Toast.makeText(TransactionActivity.this, "[PAY HISTORY]" + error.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
