package com.pharmacy.gts.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
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
import com.pharmacy.gts.Adapters.MainSliderAdapter;
import com.pharmacy.gts.App.APIConfig;
import com.pharmacy.gts.App.CircleImageView;
import com.pharmacy.gts.App.ComMethod;
import com.pharmacy.gts.App.MySingleton;
import com.pharmacy.gts.Database.Cart.CartDataSource;
import com.pharmacy.gts.Database.Cart.CartDatabase;
import com.pharmacy.gts.Database.Cart.CartItem;
import com.pharmacy.gts.Database.Cart.LocalCartDataSource;
import com.pharmacy.gts.Permissions.RunTimePermissions;
import com.pharmacy.gts.PrefManager.LoginManager;
import com.pharmacy.gts.PrefManager.UserManager;
import com.pharmacy.gts.R;
import com.pharmacy.gts.Services.PicassoImageLoadingService;
import com.pharmacy.gts.kprogresshud.KProgressHUD;
import com.google.android.material.navigation.NavigationView;
import com.razorpay.Checkout;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ss.com.bannerslider.Slider;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private ArrayList<String> imageArray;
    private MainSliderAdapter sliderAdapter;
    private Slider slider;
    private KProgressHUD kProgressHUD;
    private ComMethod comMethod;
    private NavigationView navigationView;
    private DrawerLayout drawer;

    private UserManager userManager;
    private TextView headerName, headerCompany, headerOarea;
    private CircleImageView navProfile;

    //Cart Count
    private TextView textCartItemCount;
    private int mCartItemCount = 0;
    //Database Local Cart
    private CompositeDisposable compositeDisposable;
    private CartDataSource cartDataSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new RunTimePermissions(this).requestStoragePermission();
        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Pharmacy");
        Checkout.preload(getApplicationContext());
        comMethod = new ComMethod(this);
        setProgress();
        userManager = new UserManager(this);
        compositeDisposable = new CompositeDisposable();
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDAO());

        drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.drawer_opne, R.string.drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        headerName = (TextView) header.findViewById(R.id.tv_name);
        headerCompany = (TextView) header.findViewById(R.id.tv_company);
        headerOarea = (TextView) header.findViewById(R.id.tv_oarea);
        navProfile = (CircleImageView) header.findViewById(R.id.avatar);


        headerName.setText(userManager.getFName() + " " + userManager.getLName());
        headerCompany.setText(userManager.getCompany());
        headerOarea.setText(userManager.getOarea());

        Picasso.get()
                .load(APIConfig.BASE_IMAGE + "uploads/" + userManager.getPhoto())
                .placeholder(R.drawable.grey_user)
                .error(R.drawable.grey_user)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(navProfile);

        //END HEADER

        imageArray = new ArrayList<>();
        sliderAdapter = new MainSliderAdapter(this, imageArray);
        Slider.init(new PicassoImageLoadingService(this));
        slider = findViewById(R.id.banner_slider1);
        slider.setAdapter(sliderAdapter);

        //Menus
        findViewById(R.id.layout_product).setOnClickListener(this);
        findViewById(R.id.layout_my_account).setOnClickListener(this);
        findViewById(R.id.my_cart).setOnClickListener(this);
        findViewById(R.id.my_order).setOnClickListener(this);
        findViewById(R.id.our_mission).setOnClickListener(this);
        findViewById(R.id.our_vision).setOnClickListener(this);

        getSlider();
        getCart();


    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.nav_dashboard) {
            drawer.closeDrawer(GravityCompat.START);

        }
        if (item.getItemId() == R.id.nav_profile) {
            drawer.closeDrawer(GravityCompat.START);
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }

        if (item.getItemId() == R.id.nav_orders) {
            drawer.closeDrawer(GravityCompat.START);
            startActivity(new Intent(getApplicationContext(), MyOrderActivity.class));
        }

        if (item.getItemId() == R.id.nav_trans_history) {
            drawer.closeDrawer(GravityCompat.START);
            startActivity(new Intent(getApplicationContext(), TransactionActivity.class));
        }
        if (item.getItemId() == R.id.nav_coupon) {
            drawer.closeDrawer(GravityCompat.START);
            startActivity(new Intent(getApplicationContext(), CouponActivity.class));
        }

        if (item.getItemId() == R.id.nav_mission) {
            drawer.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(getApplicationContext(), MissionActivity.class);
            intent.putExtra("pageid", "2");
            intent.putExtra("title", "Mission");
            startActivity(intent);
        }
        if (item.getItemId() == R.id.nav_vision) {
            drawer.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(getApplicationContext(), MissionActivity.class);
            intent.putExtra("pageid", "3");
            intent.putExtra("title", "Vision");
            startActivity(intent);
        }
        if (item.getItemId() == R.id.nav_about) {
            drawer.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(getApplicationContext(), MissionActivity.class);
            intent.putExtra("pageid", "1");
            intent.putExtra("title", "About Us");
            startActivity(intent);
        }
        if (item.getItemId() == R.id.nav_contact) {
            drawer.closeDrawer(GravityCompat.START);
            startActivity(new Intent(getApplicationContext(), ContactActivity.class));
        }
        if (item.getItemId() == R.id.nav_share) {
            drawer.closeDrawer(GravityCompat.START);
            comMethod.share(MainActivity.this);
        }
        if (item.getItemId() == R.id.nav_star) {
            drawer.closeDrawer(GravityCompat.START);
            comMethod.rate(MainActivity.this);
        }
        if (item.getItemId() == R.id.nav_change_pass) {
            drawer.closeDrawer(GravityCompat.START);
            startActivity(new Intent(getApplicationContext(), ChangePasswordActivity.class));
        }
        if (item.getItemId() == R.id.nav_logout) {
            drawer.closeDrawer(GravityCompat.START);
            logout();
        }

        return true;
    }

    public void logout() {
        LoginManager loginManager = new LoginManager(this);
        loginManager.setFirstTimeLaunch(true);
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        clearCart();
        finish();
    }

    private void getSlider() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, APIConfig.SLIDER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                imageArray.clear();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        imageArray.add(jsonObject.getString("image"));
                    }
                    slider.setAdapter(sliderAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstanc(getApplicationContext()).addRequestQueue(stringRequest);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.layout_product)
            startActivity(new Intent(getApplicationContext(), ProductActivity.class));
        if (v.getId() == R.id.layout_my_account)
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        if (v.getId() == R.id.my_cart)
            startActivity(new Intent(getApplicationContext(), CartActivity.class));
        if (v.getId() == R.id.my_order)
            startActivity(new Intent(getApplicationContext(), MyOrderActivity.class));
        if (v.getId() == R.id.our_mission) {
            Intent intent = new Intent(getApplicationContext(), MissionActivity.class);
            intent.putExtra("pageid", "2");
            intent.putExtra("title", "Mission");
            startActivity(intent);
        }
        if (v.getId() == R.id.our_vision) {
            Intent intent = new Intent(getApplicationContext(), MissionActivity.class);
            intent.putExtra("pageid", "3");
            intent.putExtra("title", "Vision");
            startActivity(intent);
        }

    }

    public void setProgress() {
        kProgressHUD = KProgressHUD.create(MainActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }

    @Override
    protected void onResume() {
        super.onResume();
        headerName.setText(userManager.getFName() + " " + userManager.getLName());
        headerCompany.setText(userManager.getCompany());
        headerOarea.setText(userManager.getOarea());
        Picasso.get()
                .load(APIConfig.BASE_IMAGE + "uploads/" + userManager.getPhoto())
                .placeholder(R.drawable.grey_user)
                .error(R.drawable.grey_user)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(navProfile);

        countCartItem();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        final MenuItem menuItem = menu.findItem(R.id.action_cart);

        View actionView = menuItem.getActionView();
        textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);

        setupBadge();

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_cart) {
            startActivity(new Intent(getApplicationContext(), CartActivity.class));
        }
        return true;
    }

    private void setupBadge() {

        if (textCartItemCount != null) {
            if (mCartItemCount == 0) {
                if (textCartItemCount.getVisibility() != View.GONE) {
                    textCartItemCount.setVisibility(View.GONE);
                }
            } else {
                textCartItemCount.setText(String.valueOf(Math.min(mCartItemCount, 99)));
                if (textCartItemCount.getVisibility() != View.VISIBLE) {
                    textCartItemCount.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void countCartItem() {
        cartDataSource.countItemInCart(userManager.getMID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Integer integer) {
//                        Toast.makeText(MainActivity.this, "Count : " + integer, Toast.LENGTH_SHORT).show();
                        mCartItemCount = integer;
                        setupBadge();

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivity.this, "[COUNT CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        kProgressHUD.dismiss();
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
                        Toast.makeText(MainActivity.this, "[CART CLEAN]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getCart() {
        kProgressHUD.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, APIConfig.GET_CART + userManager.getMID(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonArray = new JSONArray(response);

                    if (jsonArray.length() == 0) {
                        clearCart();
                    }

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        CartItem item = new CartItem();
                        item.setProductid(object.getString("productid"));
                        item.setCartid(object.getString("cartid"));
                        item.setPname(object.getString("pname"));
                        item.setMrp(object.getDouble("mrp"));
                        item.setUid(userManager.getMID());
                        item.setImage(object.getString("image"));
                        item.setGst(object.getInt("gst"));
                        item.setQuantity(object.getInt("qty"));
                        compositeDisposable.add(cartDataSource.insertOrReplaceAll(item)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
//                                    Toast.makeText(MainActivity.this, "Added to cart", Toast.LENGTH_SHORT).show();
                                }, throwable -> {
                                    Toast.makeText(MainActivity.this, "[LOCAL CART ERROR]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                }));

                    }

                    countCartItem();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "[SERVER Cart Error]" + error.getMessage(), Toast.LENGTH_SHORT).show();

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
}
