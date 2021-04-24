package com.pharmacy.gts.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.pharmacy.gts.Adapters.CartAdapter;
import com.pharmacy.gts.App.APIConfig;
import com.pharmacy.gts.App.MySingleton;
import com.pharmacy.gts.Database.Cart.CartDataSource;
import com.pharmacy.gts.Database.Cart.LocalCartDataSource;
import com.pharmacy.gts.Database.Cart.CartDatabase;
import com.pharmacy.gts.Database.Cart.CartItem;

import com.pharmacy.gts.EventBus.DeleteItemInCart;
import com.pharmacy.gts.EventBus.UpdateItemInCart;
import com.pharmacy.gts.PrefManager.UserManager;
import com.pharmacy.gts.R;
import com.pharmacy.gts.kprogresshud.KProgressHUD;
import com.sdsmdg.tastytoast.TastyToast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CartActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private Parcelable recyclerViewState;
    private CartDataSource cartDataSource;
    private UserManager userManager;
    private TextView tvTotalPrice, tvEmpty;
    private CompositeDisposable compositeDisposable;
    private MutableLiveData<List<CartItem>> mutableLiveDataCartItem;
    private CardView groupPlaceHolder;
    private KProgressHUD kProgressHUD;
    private CartAdapter cartAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("My Cart");

        setProgress();
        userManager = new UserManager(this);
        compositeDisposable = new CompositeDisposable();
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDAO());

        recyclerView = (RecyclerView) findViewById(R.id.recycler_cart);
        tvTotalPrice = (TextView) findViewById(R.id.txt_total_price);
        tvEmpty = (TextView) findViewById(R.id.txt_empty_cart);
        groupPlaceHolder = (CardView) findViewById(R.id.group_place_holder);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, linearLayoutManager.getOrientation()));

        getMutableLiveDataCartItem().observe(this, new Observer<List<CartItem>>() {
            @Override
            public void onChanged(List<CartItem> cartItems) {
                if (cartItems == null || cartItems.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    groupPlaceHolder.setVisibility(View.GONE);
                    cartAdapter = new CartAdapter(CartActivity.this, cartItems);
                    recyclerView.setAdapter(cartAdapter);
                } else {
                    calculateTotalPrice();
                    tvEmpty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    groupPlaceHolder.setVisibility(View.VISIBLE);
                    cartAdapter = new CartAdapter(CartActivity.this, cartItems);
                    recyclerView.setAdapter(cartAdapter);
//                    Toast.makeText(CartActivity.this, "Success", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.btn_place_order).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(sticky = false, threadMode = ThreadMode.MAIN)
    public void onUpdateItemInCartEvent(UpdateItemInCart event) {
        if (event.getCartItem() != null) {
//            Toast.makeText(this, "Countedff", Toast.LENGTH_SHORT).show();
            recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
            updateCart(event.getCartItem().getCartid(), String.valueOf(event.getCartItem().getQuantity()), event);
        }

    }

    @Subscribe(sticky = false, threadMode = ThreadMode.MAIN)
    public void onDeleteItemInCartEvent(DeleteItemInCart event) {
        if (event.getCartItem() != null) {
            recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
//            Toast.makeText(this, "Remove Called", Toast.LENGTH_SHORT).show();
            removeCart(event.getCartItem().getCartid(), event);
        }

    }

    public void removeCart(String id, DeleteItemInCart event) {
        kProgressHUD.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, APIConfig.REMOVE_CART + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean("error")) {
                        cartDataSource.deleteCartItem(event.getCartItem())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new SingleObserver<Integer>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onSuccess(Integer integer) {

                                        cartAdapter.notifyDataSetChanged();
                                        calculateTotalPrice();
                                        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                                        kProgressHUD.dismiss();
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Toast.makeText(CartActivity.this, "[UPDATE CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                Toast.makeText(DetailProductActivity.this, ""+response, Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CartActivity.this, "[REMOVE CART]" + error.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void calculateTotalPrice() {

        cartDataSource.sumMRP(userManager.getMID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Double>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Double aDouble) {
                        tvTotalPrice.setText(new StringBuilder("Total\n₹").append(String.format("%.2f", aDouble)));
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!e.getMessage().contains("Query returned empty")) {
                            Toast.makeText(CartActivity.this, "[SUM CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public MutableLiveData<List<CartItem>> getMutableLiveDataCartItem() {
        if (mutableLiveDataCartItem == null)
            mutableLiveDataCartItem = new MutableLiveData();
        getAllCartItem();

        return mutableLiveDataCartItem;
    }

    private void getAllCartItem() {
        compositeDisposable.add(cartDataSource.getALLCart(userManager.getMID()).
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cartItems -> {
                    mutableLiveDataCartItem.setValue(cartItems);


                }, throwable -> {
                    mutableLiveDataCartItem.setValue(null);
                    Toast.makeText(this, "Null", Toast.LENGTH_SHORT).show();
                }));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    private void updateCart(String cartid, String qty, UpdateItemInCart event) {
        kProgressHUD.show();

        MySingleton.getmInstanc(getApplicationContext()).cancelPendingRequests(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIConfig.UPDATE_CART, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (!jsonObject.getBoolean("error")) {
                        cartDataSource.updateCartItems(event.getCartItem())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new SingleObserver<Integer>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onSuccess(Integer integer) {

                                        calculateTotalPrice();
                                        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                                        kProgressHUD.dismiss();
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Toast.makeText(CartActivity.this, "[UPDATE CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    } else
                        TastyToast.makeText(getApplicationContext(), getString(R.string.error_update_cart), TastyToast.LENGTH_LONG, TastyToast.ERROR);
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
                params.put("cartid", cartid);
                params.put("qty", qty);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(120000,
                0, 0));
        MySingleton.getmInstanc(getApplicationContext()).addRequestQueue(stringRequest);

    }

    public void setProgress() {
        kProgressHUD = KProgressHUD.create(CartActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_place_order) {
            Intent intent
                    = new Intent(getApplicationContext(), PlaceOrderActivity.class);
            intent.putExtra("amount", Double.parseDouble(tvTotalPrice.getText().toString().replace("Total\n₹", "")));
            startActivity(intent);
        }
    }
}


