package com.pharmacy.gts.Adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pharmacy.gts.App.APIConfig;
import com.pharmacy.gts.Models.OrderProductModel;
import com.pharmacy.gts.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class OrderProductAdapter extends RecyclerView.Adapter<OrderProductAdapter.MyViewHolder> {

    private ArrayList<OrderProductModel> mOrderModelArrayList;
    private Context mContext;
    private long time;


    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView tvPname, tvPrice, tvQty, tvTotal, tvDiscount, tvGST, tvDate, tvDateLabel, tvNett, tvPaidStatus;
        ImageView ivProduct, ivStatus;

        ArrayList<OrderProductModel> mOrderModelArrayList = new ArrayList<OrderProductModel>();
        Context ctx;
        private FrameLayout container;

        MyViewHolder(View view, Context ctx, ArrayList<OrderProductModel> mOrderModelArrayList) {
            super(view);
            this.mOrderModelArrayList = mOrderModelArrayList;
            this.ctx = ctx;
            container = (FrameLayout) itemView.findViewById(R.id.single_notification_layout);
            tvPname = (TextView) view.findViewById(R.id.tv_pname);
            tvPrice = (TextView) view.findViewById(R.id.tv_price);
            tvQty = (TextView) view.findViewById(R.id.tv_qty);
            tvTotal = (TextView) view.findViewById(R.id.tv_total);
            tvGST = (TextView) view.findViewById(R.id.tv_gst);
            tvDiscount = (TextView) view.findViewById(R.id.tv_discount);
            tvNett = (TextView) view.findViewById(R.id.tv_nett);
            tvDate = (TextView) view.findViewById(R.id.tv_reg_dt);
            tvDateLabel = (TextView) view.findViewById(R.id.date_label);
            ivProduct = (ImageView) view.findViewById(R.id.iv_product);
            ivStatus = (ImageView) view.findViewById(R.id.iv_status);
        }

    }

    public OrderProductAdapter(Context context, ArrayList<OrderProductModel> mOrderModelArrayList) {
        mContext = context;
        this.mOrderModelArrayList = mOrderModelArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_product_summary_layout, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView, mContext, mOrderModelArrayList);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        setAnimation(holder.container, position);
        OrderProductModel model = mOrderModelArrayList.get(position);


        Glide.with(mContext).load(APIConfig.BASE_IMAGE + "products/" + model.getImage())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.ivProduct);

        double gst = model.getAmount() * model.getGst() / 100;

        holder.tvPname.setText(model.getPname());
        holder.tvQty.setText("Qty. " + model.getQuantity());
        holder.tvPrice.setText(mContext.getString(R.string.currency_symbol) + model.getPrice());
        holder.tvGST.setText(mContext.getString(R.string.currency_symbol) + String.format("%.2f",gst));
        holder.tvTotal.setText(mContext.getString(R.string.currency_symbol) + model.getAmount());
        holder.tvDiscount.setText(mContext.getString(R.string.currency_symbol) + model.getDiscount());
        holder.tvNett.setText(mContext.getString(R.string.currency_symbol) + model.getNett());
        String dateOrder = null;
        if (model.getStatus().equals("1")) {
            holder.ivStatus.setImageResource(R.drawable.success_icon);
            holder.tvDateLabel.setText("Delivered Date");
            dateOrder = model.getDelivereddate();
        } else {
            holder.ivStatus.setImageResource(R.drawable.ic_pending_order);
            holder.tvDateLabel.setText("Order Date");
            dateOrder = model.getOrderdate();
        }


        DateFormat formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Date date = null;
                date = (Date) formatter.parse(dateOrder);
                time = date.getTime();
            }
            holder.tvDate.setText(getDate(time).getTime().toLocaleString());


        } catch (ParseException e) {
            e.printStackTrace();
        }

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
        return mOrderModelArrayList.size();
    }

}
