package com.pharmacy.gts.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.pharmacy.gts.Adapters.ProductAdapter;
import com.pharmacy.gts.App.APIConfig;
import com.pharmacy.gts.App.ComMethod;
import com.pharmacy.gts.App.MySingleton;
import com.pharmacy.gts.Models.ProductModel;
import com.pharmacy.gts.PrefManager.UserManager;
import com.pharmacy.gts.R;
import com.pharmacy.gts.kprogresshud.KProgressHUD;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductActivity extends AppCompatActivity {

    private ArrayList<ProductModel> productArrayList;
    private ProductAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private ComMethod comMethod;
    private KProgressHUD kProgressHUD;
    private UserManager userManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Products");
        comMethod = new ComMethod(this);
        setProgress();
        userManager =  new UserManager(this);

        productArrayList = new ArrayList<>();
        adapter = new ProductAdapter(this, productArrayList);

        RecyclerView recyclerViewProduct = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerViewProduct.setHasFixedSize(true);
        recyclerViewProduct.setItemViewCacheSize(20);
        recyclerViewProduct.setDrawingCacheEnabled(true);
        recyclerViewProduct.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerViewProduct.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerViewProduct.setLayoutManager(mLayoutManager);
        recyclerViewProduct.setAdapter(adapter);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (comMethod.checkNetworkConnection())
                    getProduct();
                else
                    TastyToast.makeText(getApplicationContext(), "Please check inetenet connection", TastyToast.LENGTH_LONG, TastyToast.ERROR);
            }
        });

        if (comMethod.checkNetworkConnection())
            getProduct();
        else
            TastyToast.makeText(getApplicationContext(), "Please check inetenet connection", TastyToast.LENGTH_LONG, TastyToast.ERROR);
    }

    public void getProduct() {
        kProgressHUD.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, APIConfig.GET_PRODUCT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                refreshLayout.setRefreshing(false);
                productArrayList.clear();
                kProgressHUD.dismiss();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);


                            ProductModel productModel = new ProductModel();
                            productModel.setProductid(jsonObject.getString("productid"));
                            productModel.setPcode(jsonObject.getString("pcode"));
                            productModel.setPname(jsonObject.getString("pname"));
                            productModel.setDescription(jsonObject.getString("description"));
                            productModel.setImage(jsonObject.getString("image"));
                            productModel.setMrp(jsonObject.getInt("mrp"));

                            productArrayList.add(productModel);

//                        Toast.makeText(ProductsActivity.this, ""+jsonObject.getString("pfor"), Toast.LENGTH_SHORT).show();

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
                if (error instanceof AuthFailureError) {
                    comMethod.logout();
                    finish();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
//                headers.put("Content-Type", " application/json");
                headers.put("Authorization", "Bearer " + userManager.getToken());
                return headers;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstanc(getApplicationContext()).addRequestQueue(stringRequest);
    }

    public void setProgress() {
        kProgressHUD = KProgressHUD.create(ProductActivity.this)
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
