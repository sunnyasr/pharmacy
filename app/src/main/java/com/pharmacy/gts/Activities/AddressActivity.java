package com.pharmacy.gts.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.pharmacy.gts.Adapters.AddressAdapter;
import com.pharmacy.gts.App.APIConfig;
import com.pharmacy.gts.App.ComMethod;
import com.pharmacy.gts.App.MySingleton;
import com.pharmacy.gts.Database.Address.AddressDataSource;
import com.pharmacy.gts.Database.Address.AddressDatabase;
import com.pharmacy.gts.Database.Address.AddressItem;
import com.pharmacy.gts.Database.Address.LocalAddressDataSource;
import com.pharmacy.gts.EventBus.AddressDelete;
import com.pharmacy.gts.EventBus.AddressSelect;
import com.pharmacy.gts.EventBus.AddressUpdate;
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

public class AddressActivity extends AppCompatActivity implements View.OnClickListener {

    private UserManager userManager;
    private KProgressHUD kProgressHUD;
    private ComMethod comMethod;
    private AddressDataSource addressDataSource;
    private CompositeDisposable compositeDisposable;
    private AddressAdapter addressAdapter;
    private RecyclerView recyclerView;
    private MutableLiveData<List<AddressItem>> mutableLiveDataAddressItem;
    private Parcelable recyclerViewState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Address");
        setProgress();
        userManager = new UserManager(this);
        comMethod = new ComMethod(this);
        compositeDisposable = new CompositeDisposable();
        addressDataSource = new LocalAddressDataSource(AddressDatabase.getInstance(this).addressDAO());
        recyclerView = (RecyclerView) findViewById(R.id.recycler_address);
        findViewById(R.id.btn_add_new_address).setOnClickListener(this);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, linearLayoutManager.getOrientation()));
        getMutableLiveDataCartItem().observe(this, new Observer<List<AddressItem>>() {
            @Override
            public void onChanged(List<AddressItem> addressItems) {
                if (addressItems == null || addressItems.isEmpty()) {

                    recyclerView.setVisibility(View.GONE);

                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    addressAdapter = new AddressAdapter(AddressActivity.this, addressItems);
                    recyclerView.setAdapter(addressAdapter);

                }
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

    public MutableLiveData<List<AddressItem>> getMutableLiveDataCartItem() {
        if (mutableLiveDataAddressItem == null)
            mutableLiveDataAddressItem = new MutableLiveData();
        getAllAddressItem();

        return mutableLiveDataAddressItem;
    }

    private void getAllAddressItem() {
        compositeDisposable.add(addressDataSource.getALLAddress(userManager.getMID()).
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cartItems -> {
//                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                    mutableLiveDataAddressItem.setValue(cartItems);
                }, throwable -> {
                    mutableLiveDataAddressItem.setValue(null);
                }));
    }

    @Subscribe(sticky = false, threadMode = ThreadMode.MAIN)
    public void onAddressSelectItem(AddressSelect event) {
        if (event.getAddressItem() != null) {
            onBackPressed();
            finish();
//            Toast.makeText(this, ""+event.getAddressItem().getAddressid(), Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe(sticky = false, threadMode = ThreadMode.MAIN)
    public void onAddressDeleteItem(AddressDelete event) {
        if (event.getAddressItem() != null) {
            recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
            removeAddress(event.getAddressItem().getAddressid(), event);
        }
    }

    @Subscribe(sticky = false, threadMode = ThreadMode.MAIN)
    public void onAddressUpdateItem(AddressUpdate event) {
        if (event.getAddressID() != null) {
            Intent intent = new Intent(getApplicationContext(),UpdateAddressActivity.class);
            intent.putExtra("addressid",event.getAddressID());
            startActivity(intent);
        }
    }

    private void removeAddress(String addressid, AddressDelete event) {

        kProgressHUD.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, APIConfig.DELETE_ADDRESS + addressid, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean("error")) {
                        addressDataSource.deleteAddressItem(event.getAddressItem())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new SingleObserver<Integer>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onSuccess(Integer integer) {
                                        kProgressHUD.dismiss();
                                        addressAdapter.notifyDataSetChanged();
                                        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Toast.makeText(AddressActivity.this, "[DELETE ADDRESS]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    } else {
                        kProgressHUD.dismiss();
                        TastyToast.makeText(getApplicationContext(), jsonObject.getString("msg"), TastyToast.LENGTH_LONG, TastyToast.ERROR);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddressActivity.this, "[REMOVE ADDRESS]" + error.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void setProgress() {
        kProgressHUD = KProgressHUD.create(AddressActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
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

    @Override
    public void onClick(View v) {
        startActivity(new Intent(getApplicationContext(), AddAddressActivity.class));
    }

}
