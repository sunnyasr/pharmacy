package com.pharmacy.gts.Adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pharmacy.gts.Models.OrderModel;
import com.pharmacy.gts.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {

    private ArrayList<OrderModel> mOrderModelArrayList;
    private Context mContext;
    private long time;


    public class MyViewHolder extends RecyclerView.ViewHolder  {


        TextView tvOderID, tvTransID, tvRecieved, tvDate, tvDateLabel, tvDueAmount, tvPaidStatus;

        ArrayList<OrderModel> mOrderModelArrayList = new ArrayList<OrderModel>();
        Context ctx;
        private FrameLayout container;

        MyViewHolder(View view, Context ctx, ArrayList<OrderModel> mOrderModelArrayList) {
            super(view);
            this.mOrderModelArrayList = mOrderModelArrayList;
            this.ctx = ctx;
            container = (FrameLayout) itemView.findViewById(R.id.single_notification_layout);
            tvOderID = (TextView) view.findViewById(R.id.tv_order);
            tvTransID = (TextView) view.findViewById(R.id.tv_transid);
            tvRecieved = (TextView) view.findViewById(R.id.tv_amtrecieved);
            tvDueAmount = (TextView) view.findViewById(R.id.tv_due_amt);
            tvPaidStatus = (TextView) view.findViewById(R.id.tv_paid_status);
            tvDate = (TextView) view.findViewById(R.id.tv_reg_dt);
            tvDateLabel = (TextView) view.findViewById(R.id.date_label);

        }

    }

    public OrderAdapter(Context context, ArrayList<OrderModel> mOrderModelArrayList) {
        mContext = context;
        this.mOrderModelArrayList = mOrderModelArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_order_layout, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView, mContext, mOrderModelArrayList);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        setAnimation(holder.container, position);
        OrderModel model = mOrderModelArrayList.get(position);

        holder.tvOderID.setText("#"+model.getMorderid());
        holder.tvTransID.setText(model.getTransid());
        holder.tvRecieved.setText(mContext.getString(R.string.currency_symbol) + model.getAmtreceived());
        holder.tvDueAmount.setText(mContext.getString(R.string.currency_symbol) + model.getDueblc());
        holder.tvPaidStatus.setText("Paid");

        String dateOrder = model.getCreated_date();

        holder.tvDateLabel.setText("Pay Date");


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
