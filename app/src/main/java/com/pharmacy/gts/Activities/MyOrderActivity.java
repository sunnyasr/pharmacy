package com.pharmacy.gts.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import com.pharmacy.gts.Adapters.TabAdapter;
import com.pharmacy.gts.EventBus.PayOnline;
import com.pharmacy.gts.EventBus.ViewOrderDetail;
import com.pharmacy.gts.Fragments.DeliveredFragment;
import com.pharmacy.gts.Fragments.PendingFragment;
import com.pharmacy.gts.R;
import com.google.android.material.tabs.TabLayout;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MyOrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("My Orders");

        //Tablayout
        //Tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager());
        tabAdapter.addFragment(new DeliveredFragment(), "Delivered");
        tabAdapter.addFragment(new PendingFragment(), "Pending");
        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }


    @Subscribe(sticky = false, threadMode = ThreadMode.MAIN)
    public void onPayOnline(PayOnline event) {
        if (event != null) {
            Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
            intent.putExtra("morderid", event.getmOrderModel().getMorderid());
            intent.putExtra("balance", event.getmOrderModel().getDuenett());
            startActivity(intent);
        }
    }

    @Subscribe(sticky = false, threadMode = ThreadMode.MAIN)
    public void onOrderDetail(ViewOrderDetail event) {
        if (event != null) {
            Intent intent = new Intent(getApplicationContext(), OrderDetailActivity.class);
            intent.putExtra("morderid", event.getmOrderModel().getMorderid());
            startActivity(intent);
        }
    }

    @Override
    public void onStart() {
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
}
