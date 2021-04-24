package com.pharmacy.gts.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.pharmacy.gts.R;
import com.pharmacy.gts.kprogresshud.KProgressHUD;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etFname, etLname, etEmail, etMobile, etCompany, etArea, etPass, etUsername;
    private TextView tvUserError, tvEmailError, tvMobileError;
    private ComMethod comMethod;
    private KProgressHUD kProgressHUD;
    private ImageView ivStatus;
    private boolean isStatus = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Register");
        setProgress();
        comMethod = new ComMethod(this);

        etFname = (EditText) findViewById(R.id.et_fname);
        etLname = (EditText) findViewById(R.id.et_lname);
        etEmail = (EditText) findViewById(R.id.et_email);
        etMobile = (EditText) findViewById(R.id.et_mobile);
        etCompany = (EditText) findViewById(R.id.et_company);
        etUsername = (EditText) findViewById(R.id.et_usename);
        etPass = (EditText) findViewById(R.id.et_password);
        etArea = (EditText) findViewById(R.id.et_oarea);
        ivStatus = (ImageView) findViewById(R.id.iv_status);
        tvUserError = (TextView) findViewById(R.id.tv_user_error);
        tvEmailError = (TextView) findViewById(R.id.tv_email_error);
        tvMobileError = (TextView) findViewById(R.id.tv_mobile_error);

        tvUserError.setVisibility(View.GONE);
        tvEmailError.setVisibility(View.GONE);

        etUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    if (comMethod.checkNetworkConnection())
                        checkUsername();
                    else
                        TastyToast.makeText(RegisterActivity.this, "Check your internet connectin!!", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();

                }
            }
        });

        etEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!etEmail.getText().toString().trim().matches(APIConfig.emailPattern)) {
                        tvEmailError.setVisibility(View.VISIBLE);
                        tvEmailError.setText(getString(R.string.enter_valid_email));
                    } else tvEmailError.setVisibility(View.GONE);
                }
            }
        });

        etMobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (etMobile.getText().toString().trim().length() < 10) {
                        tvMobileError.setVisibility(View.VISIBLE);
                        tvMobileError.setText(getString(R.string.enter_valid_mobile));
                    } else tvMobileError.setVisibility(View.GONE);
                }
            }
        });

        findViewById(R.id.btn_register);

        AppCompatCheckBox checkbox = (AppCompatCheckBox) findViewById(R.id.checkbox);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // show password
                    etPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    // hide password
                    etPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        findViewById(R.id.btn_register).setOnClickListener(this);

    }

    private void checkUsername() {

        final String username = etUsername.getText().toString().trim();
        if (TextUtils.isEmpty(username))
            Toast.makeText(this, getString(R.string.enter_username), Toast.LENGTH_SHORT).show();
        else if (!APIConfig.usernamePattern.matcher(username).matches()) {
            isStatus = false;
            ivStatus.setVisibility(View.GONE);
            tvUserError.setVisibility(View.VISIBLE);
            tvUserError.setText(getString(R.string.username_pattern));
//            Toast.makeText(this, "Wrong Pattern", Toast.LENGTH_SHORT).show();
        } else {
            kProgressHUD.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, APIConfig.CHECK_USERNAME, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    kProgressHUD.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (!jsonObject.getBoolean("error")) {
                            ivStatus.setImageResource(R.drawable.check);
                            ivStatus.setVisibility(View.VISIBLE);
                            tvUserError.setVisibility(View.GONE);
                            isStatus = true;
                        } else {
                            ivStatus.setImageResource(R.drawable.failed);
                            ivStatus.setVisibility(View.VISIBLE);
                            isStatus = false;
                            tvUserError.setVisibility(View.VISIBLE);
                            tvUserError.setText(jsonObject.getString("msg"));
                            TastyToast.makeText(RegisterActivity.this, "Try with another username", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    kProgressHUD.dismiss();
                    Toast.makeText(RegisterActivity.this, "Error : " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", username);
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(120000,
                    0, 0));
            MySingleton.getmInstanc(getApplicationContext()).addRequestQueue(stringRequest);


        }
    }

    public void setProgress() {
        kProgressHUD = KProgressHUD.create(RegisterActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            overridePendingTransition(R.anim.slide_to_left, R.anim.slide_from_right);
            finish();
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_register) {
            register();
        }
    }

    private void register() {
        final String fname = etFname.getText().toString().trim();
        final String lname = etLname.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String mobile = etMobile.getText().toString().trim();
        final String username = etUsername.getText().toString().trim();
        final String company = etCompany.getText().toString().trim();
        final String oarea = etArea.getText().toString().trim();
        final String pass = etPass.getText().toString().trim();

        if (TextUtils.isEmpty(fname))
            TastyToast.makeText(this, getString(R.string.enter_fname), TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(lname))
            TastyToast.makeText(getApplicationContext(), getString(R.string.enter_lname), TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(email))
            TastyToast.makeText(getApplicationContext(), getString(R.string.enter_email), TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (!email.matches(APIConfig.emailPattern)) {
            TastyToast.makeText(getApplicationContext(), getString(R.string.enter_valid_email), TastyToast.LENGTH_LONG, TastyToast.INFO);
        } else if (TextUtils.isEmpty(mobile))
            TastyToast.makeText(getApplicationContext(), getString(R.string.enter_mobile), TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (mobile.length() < 10) {
            TastyToast.makeText(getApplicationContext(), getString(R.string.enter_valid_mobile), TastyToast.LENGTH_LONG, TastyToast.INFO);
        } else if (TextUtils.isEmpty(username))
            TastyToast.makeText(getApplicationContext(), getString(R.string.enter_username), TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (!APIConfig.usernamePattern.matcher(username).matches()) {
            isStatus = false;
            tvUserError.setVisibility(View.VISIBLE);
            tvUserError.setText(getString(R.string.username_pattern));
        } else if (TextUtils.isEmpty(company))
            TastyToast.makeText(getApplicationContext(), getString(R.string.enter_company), TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(oarea))
            TastyToast.makeText(getApplicationContext(), getString(R.string.enter_area), TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(pass))
            TastyToast.makeText(getApplicationContext(), getString(R.string.enter_pass), TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (!isStatus)
            TastyToast.makeText(this, getString(R.string.enter_username), TastyToast.LENGTH_LONG, TastyToast.INFO);
        else {
            kProgressHUD.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, APIConfig.REGISTER, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    kProgressHUD.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getBoolean("error")) {
                            comMethod.alertDialog("Error", jsonObject.getString("msg"), true, null);
                        } else {
                            Intent intent = new Intent(getApplicationContext(), RegSuccessActivity.class);
                            intent.putExtra("username", username);
                            intent.putExtra("password", pass);
                            startActivity(intent);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    kProgressHUD.dismiss();
                    Toast.makeText(RegisterActivity.this, "Error : " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", username);
                    params.put("fname", fname);
                    params.put("lname", lname);
                    params.put("email", email);
                    params.put("mobile", mobile);
                    params.put("company", company);
                    params.put("oarea", oarea);
                    params.put("password", pass);
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(120000,
                    0, 0));
            MySingleton.getmInstanc(getApplicationContext()).addRequestQueue(stringRequest);
        }

    }
}
