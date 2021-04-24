package com.pharmacy.gts.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.pharmacy.gts.App.APIConfig;
import com.pharmacy.gts.App.ComMethod;
import com.pharmacy.gts.App.MySingleton;
import com.pharmacy.gts.Database.Cart.CartDataSource;
import com.pharmacy.gts.Database.Cart.CartDatabase;
import com.pharmacy.gts.Database.Cart.CartItem;
import com.pharmacy.gts.Database.Cart.LocalCartDataSource;
import com.pharmacy.gts.Models.ProductsImages;
import com.pharmacy.gts.PrefManager.UserManager;
import com.pharmacy.gts.R;
import com.pharmacy.gts.kprogresshud.KProgressHUD;
import com.sdsmdg.tastytoast.TastyToast;
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

public class DetailProductActivity extends AppCompatActivity implements View.OnClickListener {

    private UserManager userManager;
    private KProgressHUD kProgressHUD;
    private ComMethod comMethod;
    private TextView tvName, tvSalts, tvMRP, tvDescription, tvBrand;
    private ViewPager viewPager;
    private ArrayList<ProductsImages> images;
    private MyViewPopupAdapter myViewPagerAdapter;

    private LinearLayout sliderDotspanel;
    private ImageView[] dots;

    private Dialog myDialog;

    private Button btnAddCart, btnRemoveCart;

    //Database Local Cart
    private CompositeDisposable compositeDisposable;
    private CartDataSource cartDataSource;

    private TextView textCartItemCount;
    private int mCartItemCount = 0;
    private int mGST = 0;

    private LinearLayout bottomCartLayout;
    private TextView tvBottomCart;
    private Button btnBottomCart;

    //Price
    private double mrp = 0.0;
    private String productImage = "";
    private CartItem mCartItem;


    @Override
    protected void onResume() {
        super.onResume();
//        Toast.makeText(this, "Product ID :" + getIntent().getStringExtra("productid"), Toast.LENGTH_SHORT).show();
        getCartIdExist();
        countCartItem();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);
        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Product Description");
        userManager = new UserManager(this);
        setProgress();
        successToast(false, null);
        myDialog = new Dialog(this);
        comMethod = new ComMethod(this);
        if (comMethod.checkNetworkConnection())
            getProdduct(getIntent().getStringExtra("productid"));
        else Toast.makeText(this, getString(R.string.no_network), Toast.LENGTH_SHORT).show();

        //Prodcut Details
        tvName = (TextView) findViewById(R.id.tv_pname);
        tvSalts = (TextView) findViewById(R.id.tv_salts);
        tvMRP = (TextView) findViewById(R.id.tv_mrp);
        tvDescription = (TextView) findViewById(R.id.product_desc);
        tvBrand = (TextView) findViewById(R.id.tv_brand);
        tvName.setText(getIntent().getStringExtra("pname"));


        //Viewpager Product Images
        images = new ArrayList<>();
        viewPager = (ViewPager) findViewById(R.id.iv_product);
        sliderDotspanel = (LinearLayout) findViewById(R.id.SliderDots);
        myViewPagerAdapter = new MyViewPopupAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        //Database Local Cart

        compositeDisposable = new CompositeDisposable();
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDAO());

        //Add To Cart
        btnAddCart = (Button) findViewById(R.id.btn_add_cart);
        btnRemoveCart = (Button) findViewById(R.id.btn_remove_cart);
        btnAddCart.setOnClickListener(this);
        btnRemoveCart.setOnClickListener(this);

        //Bottom cart layout
        bottomCartLayout = (LinearLayout) findViewById(R.id.bottom_cart_layout);

        tvBottomCart = (TextView) findViewById(R.id.tv_bottom_cart);
        btnBottomCart = (Button) findViewById(R.id.btn_bottom_cart_view);
        btnBottomCart.setOnClickListener(this);
        getCartIdExist();
        countCartItem();


    }


    public void getCartIdExist() {
        cartDataSource.getItemInCart(getIntent().getStringExtra("productid"), userManager.getMID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<CartItem>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(CartItem cartItem) {
                        mCartItem = cartItem;
                        if (cartItem != null) {
                            btnAddCart.setVisibility(View.GONE);
                            btnRemoveCart.setVisibility(View.VISIBLE);
                            bottomCartLayout.setVisibility(View.VISIBLE);
//                            Toast.makeText(DetailProductActivity.this, "Not Null", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!e.getMessage().contains("Query returned empty")) {
                            Toast.makeText(DetailProductActivity.this, "[GET CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            btnAddCart.setVisibility(View.VISIBLE);
                            btnRemoveCart.setVisibility(View.GONE);
                        }
                    }
                });

    }

    public void successToast(boolean show, String msg) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_layout,
                findViewById(R.id.custom_toast_container));

        TextView text = layout.findViewById(R.id.tv_toast);
        text.setText(msg);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 150);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        if (show)
            toast.show();
    }

    public void setProgress() {
        kProgressHUD = KProgressHUD.create(DetailProductActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }

    public void getProdduct(String pid) {
        kProgressHUD.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, APIConfig.GET_SINSGLE_PRODUCT + pid, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                kProgressHUD.dismiss();

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    mrp = jsonObject.getDouble("mrp");
                    productImage = jsonObject.getString("image");
                    tvSalts.setText(jsonObject.getString("salts"));
                    tvMRP.setText("₹ " + jsonObject.getString("mrp"));
                    tvDescription.setText(jsonObject.getString("description"));
                    tvBrand.setText("by " + jsonObject.getString("brand"));
                    mGST = jsonObject.getInt("gst");
                    ProductsImages images1 = new ProductsImages();
                    images1.setProductimg(jsonObject.getString("image"));
                    images.add(images1);

                    JSONArray jsonArray = jsonObject.getJSONArray("images");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        ProductsImages productsImages = new ProductsImages();
                        productsImages.setProductimg(object.getString("image"));
                        images.add(productsImages);
                    }


                    myViewPagerAdapter.notifyDataSetChanged();

                    dots = new ImageView[myViewPagerAdapter.getCount()];
                    for (int i = 0; i < myViewPagerAdapter.getCount(); i++) {

                        dots[i] = new ImageView(DetailProductActivity.this);
                        dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                        params.setMargins(8, 0, 8, 0);

                        sliderDotspanel.addView(dots[i], params);

                    }
                    dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                kProgressHUD.dismiss();
                Toast.makeText(DetailProductActivity.this, "Error : " + error.getMessage(), Toast.LENGTH_SHORT).show();

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
        if (item.getItemId() == R.id.action_cart) {
            startActivity(new Intent(getApplicationContext(), CartActivity.class));
        }
        return true;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_add_cart) {
            addToCart();


        }
        if (v.getId() == R.id.btn_remove_cart) {
            removeCart(mCartItem.getCartid());
        }
        if (v.getId() == R.id.btn_bottom_cart_view) {
            startActivity(new Intent(getApplicationContext(), CartActivity.class));
        }

    }

    public void removeCart(String id) {
        kProgressHUD.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, APIConfig.REMOVE_CART + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean("error")) {
                        cartDataSource.deleteCartItem(mCartItem)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new SingleObserver<Integer>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onSuccess(Integer integer) {
                                        btnAddCart.setVisibility(View.VISIBLE);
                                        btnRemoveCart.setVisibility(View.GONE);
                                        kProgressHUD.dismiss();
                                        countCartItem();
                                        successToast(true, getString(R.string.remove_cart_success));
                                    }

                                    @Override
                                    public void onError(Throwable e) {

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
                Toast.makeText(DetailProductActivity.this, "[REMOVE CART]" + error.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void addToCart() {
        kProgressHUD.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIConfig.ADD_TO_CART, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("error"))
                        TastyToast.makeText(getApplicationContext(), jsonObject.getString("msg"), TastyToast.LENGTH_LONG, TastyToast.ERROR);
                    else {
                        btnAddCart.setVisibility(View.GONE);
                        btnRemoveCart.setVisibility(View.VISIBLE);
                        mCartItem = new CartItem();
                        mCartItem.setProductid(getIntent().getStringExtra("productid"));
                        mCartItem.setPname(getIntent().getStringExtra("pname"));
                        mCartItem.setMrp(mrp);
                        mCartItem.setGst(mGST);
                        mCartItem.setImage(productImage);
                        mCartItem.setQuantity(1);
                        mCartItem.setCartid(jsonObject.getString("cartid"));
                        mCartItem.setUid(userManager.getMID());

                        compositeDisposable.add(cartDataSource.insertOrReplaceAll(mCartItem)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                    successToast(true, getString(R.string.custom_toast_message));
                                    kProgressHUD.dismiss();
                                    countCartItem();
                                }, throwable -> {
                                    Toast.makeText(DetailProductActivity.this, "[CART ERROR]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                }));


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                kProgressHUD.dismiss();
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
                params.put("pid", getIntent().getStringExtra("productid"));
                params.put("qty", "1");
                params.put("mid", userManager.getMID());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(120000,
                0, 0));
        MySingleton.getmInstanc(getApplicationContext()).addRequestQueue(stringRequest);

    }

    public class MyViewPopupAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        public MyViewPopupAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView imgDisplay;
            layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.image_product_preview, container, false);

            imgDisplay = (ImageView) view.findViewById(R.id.iv_product);
            final ProductsImages image = images.get(position);


            Picasso.get()
                    .load(APIConfig.BASE_IMAGE + "products/" + image.getProductimg())
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(imgDisplay);
            container.addView(view);

//            imgDisplay.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("images", images);
//                    bundle.putInt("position", position);
//
//                    Intent intent = new Intent(BuyActivity.this, PopUpActivity.class);
//                    intent.putExtras(bundle);
//                    startActivity(intent);
//                }
//            });

            return view;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == ((View) obj);
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            for (int i = 0; i < myViewPagerAdapter.getCount(); i++) {
                dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));
            }

            dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void popup() {
        ImageButton txtclose;
        ImageView imageView;
        myDialog.setContentView(R.layout.custom_popup_cart);

        txtclose = (ImageButton) myDialog.findViewById(R.id.txtclose);


        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();

            }
        });
//        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.setCancelable(false);
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.show();
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
                        mCartItemCount = integer;
                        setupBadge();
                        if (mCartItemCount == 0)
                            bottomCartLayout.setVisibility(View.GONE);
                        else {
                            bottomCartLayout.setVisibility(View.VISIBLE);
                            tvBottomCart.setText(mCartItemCount + " Items in cart");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(DetailProductActivity.this, "[COUNT CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

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


}
