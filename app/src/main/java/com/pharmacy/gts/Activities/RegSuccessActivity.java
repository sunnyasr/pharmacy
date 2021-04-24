package com.pharmacy.gts.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.pharmacy.gts.R;

public class RegSuccessActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvUsername, tvPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_success);

        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Welcome");

        tvUsername = (TextView) findViewById(R.id.tv_username);
        tvPassword = (TextView) findViewById(R.id.tv_password);


        tvUsername.setText(getIntent().getStringExtra("username"));
        tvPassword.setText(getIntent().getStringExtra("password"));

        findViewById(R.id.btn_yes).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_yes) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            finish();
        }
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
}
