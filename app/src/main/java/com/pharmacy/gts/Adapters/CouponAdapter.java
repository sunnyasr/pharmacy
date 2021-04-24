package com.pharmacy.gts.Adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pharmacy.gts.App.APIConfig;
import com.pharmacy.gts.EventBus.Coupon;
import com.pharmacy.gts.Models.CouponModel;
import com.pharmacy.gts.R;

import org.greenrobot.eventbus.EventBus;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.MyViewHolder> {

    private ArrayList<CouponModel> couponModelArrayList;
    private Context mContext;
    private long time;


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView iv_coupon;
        TextView tvCoupon, tvDesc, tvPercent, tvEndDate;


        ArrayList<CouponModel> downlineArrayList = new ArrayList<CouponModel>();
        Context ctx;
        private FrameLayout container;
        RelativeLayout cardView;
        ImageButton btnCopy;

        MyViewHolder(View view, Context ctx, ArrayList<CouponModel> downlineArrayList) {
            super(view);
            this.downlineArrayList = downlineArrayList;
            this.ctx = ctx;

            iv_coupon = (ImageView) view.findViewById(R.id.iv_coupon);

            tvCoupon = (TextView) view.findViewById(R.id.tv_coupon);
            tvDesc = (TextView) view.findViewById(R.id.tv_desc);
            tvPercent = (TextView) view.findViewById(R.id.tv_percent);
            tvEndDate = (TextView) view.findViewById(R.id.tv_end_date);
            cardView = (RelativeLayout) view.findViewById(R.id.layout_coupon);
            btnCopy = (ImageButton) view.findViewById(R.id.btn_copy);
            container = (FrameLayout) itemView.findViewById(R.id.single_coupon_layout);
            container.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();


        }
    }

    public CouponAdapter(Context context, ArrayList<CouponModel> empViewArrayList) {
        mContext = context;
        this.couponModelArrayList = empViewArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_coupon_layout, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView, mContext, couponModelArrayList);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        setAnimation(holder.container, position);

        if (position % 2 == 0) {
            holder.cardView.setBackgroundResource(R.drawable.menu_yellow_gradient_style);
        } else {
            holder.cardView.setBackgroundResource(R.drawable.menu_red_gradient_style);
        }

        final CouponModel model = couponModelArrayList.get(position);
        Glide.with(mContext).load(APIConfig.BASE_IMAGE + "uploads/" + model.getImagecoupon())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.drawable.ic_coupon_white)
                .error(R.drawable.ic_coupon_white)
                .into(holder.iv_coupon);
        holder.tvCoupon.setText(model.getCoupon());
        holder.tvDesc.setText(model.getPercent() + "% " + model.getDescription());
        holder.tvPercent.setText(model.getPercent() + "%");
        String dateOrder = model.getEnddate();
        DateFormat formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            formatter = new SimpleDateFormat("yyyy-MM-dd");
        }
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Date date = null;
                date = (Date) formatter.parse(dateOrder);
                time = date.getTime();
            }
            holder.tvEndDate.setText("Valid unit : " + getDate(time).getTime().toLocaleString().replace("00:00:00", ""));


        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().postSticky(new Coupon(model));
            }
        });
    }

    private Calendar getDate(long time) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTimeInMillis(time);
        return cal;
    }

    private void setAnimation(FrameLayout container, int position) {
        Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
        container.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return couponModelArrayList.size();
    }

}
