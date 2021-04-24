package com.pharmacy.gts.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import com.pharmacy.gts.App.CircleImageView;
import com.pharmacy.gts.App.ComMethod;
import com.pharmacy.gts.App.MySingleton;
import com.pharmacy.gts.PrefManager.UserManager;
import com.pharmacy.gts.R;
import com.pharmacy.gts.kprogresshud.KProgressHUD;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private UserManager userManager;
    private KProgressHUD kProgressHUD;
    private ComMethod comMethod;
    private TextView tvUserame, tvMemberID;
    private EditText etFname, etLname, etEmail, etMobile, etCompany, etOarea;
    private BottomSheetDialog dialog;

    private final int IMAGE_REQUEST_CODE = 23002, CAMERA = 22002;
    private Bitmap bitmap;
    private CircleImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Profile");
        setProgress();
        comMethod = new ComMethod(this);
        userManager = new UserManager(this);

        profileImage = (CircleImageView) findViewById(R.id.profile_image);
        tvUserame = (TextView) findViewById(R.id.tv_username);
        tvMemberID = (TextView) findViewById(R.id.tv_memberid);

        etFname = (EditText) findViewById(R.id.et_fname);
        etLname = (EditText) findViewById(R.id.et_lname);
        etEmail = (EditText) findViewById(R.id.et_email);
        etMobile = (EditText) findViewById(R.id.et_mobile);
        etCompany = (EditText) findViewById(R.id.et_company);
        etOarea = (EditText) findViewById(R.id.et_oarea);


        //Set Values
        if (comMethod.checkNetworkConnection()) {
            Picasso.get()
                    .load(APIConfig.BASE_IMAGE + "uploads/" + userManager.getPhoto())
                    .placeholder(R.drawable.grey_user)
                    .error(R.drawable.grey_user)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(profileImage);
//            Toast.makeText(this, "Load Image", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, getString(R.string.no_network), Toast.LENGTH_SHORT).show();


        tvUserame.setText("Username\n" + userManager.getUsername());
        tvMemberID.setText("Account ID\n" + userManager.getMID());

        etFname.setText(userManager.getFName());
        etLname.setText(userManager.getLName());
        etEmail.setText(userManager.getEmail());
        etMobile.setText(userManager.getMobile());
        etCompany.setText(userManager.getCompany());
        etOarea.setText(userManager.getOarea());

        findViewById(R.id.btn_update).setOnClickListener(this);
        findViewById(R.id.btn_update_image).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_update) {
            if (comMethod.checkNetworkConnection())
                profileUpdate();
            else
                TastyToast.makeText(getApplicationContext(), getString(R.string.no_network), TastyToast.LENGTH_LONG, TastyToast.ERROR);
        }

        if (v.getId() == R.id.btn_update_image) {
            if (comMethod.checkNetworkConnection())
                showPictureDialog();
            else
                TastyToast.makeText(this, getString(R.string.no_network), TastyToast.LENGTH_LONG, TastyToast.ERROR);

        }
    }

    private void showPictureDialog() {
        View view = getLayoutInflater().inflate(R.layout.bottom_dialog_media_style, null);
        dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        LinearLayout camera_sel = (LinearLayout) view.findViewById(R.id.camera);
        LinearLayout gallery_sel = (LinearLayout) view.findViewById(R.id.gallery);
        camera_sel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhotoFromCamera();
                dialog.dismiss();
            }
        });
        gallery_sel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    private void selectImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, IMAGE_REQUEST_CODE);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(path, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();

                int rotateImage = comMethod.getCameraPhotoOrientation(ProfileActivity.this, path, filePath);
                if (rotateImage == 90) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
                    bitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                }
                profileImage.setImageBitmap(bitmap);
                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == CAMERA && resultCode == RESULT_OK && data != null) {
            bitmap = (Bitmap) data.getExtras().get("data");
            profileImage.setImageBitmap(bitmap);
            uploadImage();
        }
    }

    public void setProgress() {
        kProgressHUD = KProgressHUD.create(ProfileActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }

    private void uploadImage() {
        kProgressHUD.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIConfig.UPLOAD_IMAGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        kProgressHUD.dismiss();
//                        Toast.makeText(ProfileActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (!jsonObject.getBoolean("error")) {
//                                Toast.makeText(ProfileActivity.this, "Image Upload Successfully !!", Toast.LENGTH_SHORT).show();
                                TastyToast.makeText(getApplicationContext(), "Image Upload Successfully !!", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                                userManager.setPhoto(jsonObject.getString("img"));
                            } else
//                                Toast.makeText(ProfileActivity.this, "Image Updating Fail..!", Toast.LENGTH_SHORT).show();
                                TastyToast.makeText(getApplicationContext(), "Image Updating Fail..!", TastyToast.LENGTH_LONG, TastyToast.ERROR);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            kProgressHUD.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ProfileActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                params.put("photo", comMethod.profileToString(bitmap));
                params.put("memberid", userManager.getMID());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(120000,
                0, 0));
        MySingleton.getmInstanc(ProfileActivity.this).addRequestQueue(stringRequest);
    }

    private void profileUpdate() {

        final String fname = etFname.getText().toString().trim();
        final String lname = etLname.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String mobile = etMobile.getText().toString().trim();
        final String company = etCompany.getText().toString().trim();
        final String oarea = etOarea.getText().toString().trim();

        if (TextUtils.isEmpty(fname))
            TastyToast.makeText(getApplicationContext(), getString(R.string.enter_fname), TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(lname))
            TastyToast.makeText(getApplicationContext(), getString(R.string.enter_lname), TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(email))
            TastyToast.makeText(getApplicationContext(), getString(R.string.enter_email), TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (!email.matches(APIConfig.emailPattern))
            TastyToast.makeText(getApplicationContext(), getString(R.string.enter_valid_email), TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(mobile))
            TastyToast.makeText(getApplicationContext(), getString(R.string.enter_mobile), TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (mobile.length() < 10)
            TastyToast.makeText(getApplicationContext(), getString(R.string.enter_valid_mobile), TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(company))
            TastyToast.makeText(getApplicationContext(), getString(R.string.enter_company), TastyToast.LENGTH_LONG, TastyToast.INFO);
        else if (TextUtils.isEmpty(oarea))
            TastyToast.makeText(getApplicationContext(), getString(R.string.enter_area), TastyToast.LENGTH_LONG, TastyToast.INFO);
        else {
            kProgressHUD.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, APIConfig.UPDATE_PROFILE, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    kProgressHUD.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if (!jsonObject.getBoolean("error")) {
                            TastyToast.makeText(getApplicationContext(), getString(R.string.profile_updated), TastyToast.LENGTH_LONG, TastyToast.SUCCESS).show();
                            userManager.setFName(fname);
                            userManager.setLName(lname);
                            userManager.setEmail(email);
                            userManager.setMobile(mobile);
                            userManager.setCompany(company);
                            userManager.setOarea(oarea);

                        } else
                            TastyToast.makeText(getApplicationContext(), getString(R.string.profile_update_failed), TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    kProgressHUD.dismiss();
                    Toast.makeText(ProfileActivity.this, "Error : " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                    params.put("memberid", userManager.getMID());
                    params.put("fname", fname);
                    params.put("lname", lname);
                    params.put("email", email);
                    params.put("mobile", mobile);
                    params.put("company", company);
                    params.put("oarea", oarea);
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(120000,
                    0, 0));
            MySingleton.getmInstanc(getApplicationContext()).addRequestQueue(stringRequest);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
