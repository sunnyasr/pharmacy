package com.pharmacy.gts.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.pharmacy.gts.App.APIConfig;
import com.pharmacy.gts.App.MySingleton;
import com.pharmacy.gts.PrefManager.LoginManager;
import com.pharmacy.gts.PrefManager.UserManager;
import com.pharmacy.gts.R;
import com.pharmacy.gts.kprogresshud.KProgressHUD;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etUsername, etPassword;
    private KProgressHUD kProgressHUD;
    private UserManager userManager;
    private LoginManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setProgress();
        loginManager = new LoginManager(this);
        userManager = new UserManager(this);
        etUsername = (EditText) findViewById(R.id.et_usename);
        etPassword = (EditText) findViewById(R.id.et_pass);

        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.tv_reg).setOnClickListener(this);
        findViewById(R.id.tv_lost_password).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_reg) {
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        }
        if (v.getId() == R.id.tv_lost_password) {
            startActivity(new Intent(getApplicationContext(), ForgotPasswordActivity.class));
        }
        if (v.getId() == R.id.btn_login) {
            login();
        }
    }

    private void login() {
        final String username = etUsername.getText().toString().trim();
        final String pass = etPassword.getText().toString().trim();
        if (TextUtils.isEmpty(username))
            TastyToast.makeText(getApplicationContext(), getString(R.string.enter_username), TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(pass))
            TastyToast.makeText(getApplicationContext(), getString(R.string.enter_pass), TastyToast.LENGTH_LONG, TastyToast.INFO);
        else {
            kProgressHUD.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, APIConfig.LOGIN, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
//                    kProgressHUD.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getBoolean("error"))
                            TastyToast.makeText(getApplicationContext(), "Login failed check Phone/Password", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                        else {
                            loginManager.setFirstTimeLaunch(false);
                            userManager.setToken(jsonObject.getString("access_token"));

                            getMe();


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(LoginActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", username);
                    params.put("password", pass);
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(120000,
                    0, 0));
            MySingleton.getmInstanc(getApplicationContext()).addRequestQueue(stringRequest);
        }

    }

    public void setProgress() {
        kProgressHUD = KProgressHUD.create(LoginActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }


    public void getMe() {
//        kProgressHUD.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIConfig.ME, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                kProgressHUD.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    userManager.setUsername(jsonObject.getString("username"));
                    userManager.setFName(jsonObject.getString("fname"));
                    userManager.setLName(jsonObject.getString("lname"));
                    userManager.setPass(jsonObject.getString("tpassword"));
                    userManager.setEmail(jsonObject.getString("email"));
                    userManager.setMobile(jsonObject.getString("mobile"));
                    userManager.setPhoto(jsonObject.getString("photo"));
                    userManager.setCompany(jsonObject.getString("company"));
                    userManager.setOarea(jsonObject.getString("operatingarea"));
                    userManager.setMID(jsonObject.getString("memberid"));

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                kProgressHUD.dismiss();
                if (error instanceof AuthFailureError) {

                }
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
}
