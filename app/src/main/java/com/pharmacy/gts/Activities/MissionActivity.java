package com.pharmacy.gts.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MissionActivity extends AppCompatActivity {

    private KProgressHUD kProgressHUD;
    private TextView tvTitle, tvDesc;
    private ComMethod comMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);
        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getIntent().getStringExtra("title"));
        setProgress();
        comMethod = new ComMethod(this);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvDesc = (TextView) findViewById(R.id.tv_desc);
        getAbout();
    }

    public void setProgress() {
        kProgressHUD = KProgressHUD.create(MissionActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }


    public void getAbout() {
        kProgressHUD.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, APIConfig.WEBPAGE +
                getIntent().getStringExtra("pageid"), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                kProgressHUD.dismiss();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    tvTitle.setText(jsonObject.getString("page"));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        tvDesc.setText(Html.fromHtml(jsonObject.getString("description"), Html.FROM_HTML_MODE_LEGACY));
                    } else
                        tvDesc.setText(Html.fromHtml(jsonObject.getString("description")));
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
        });
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
