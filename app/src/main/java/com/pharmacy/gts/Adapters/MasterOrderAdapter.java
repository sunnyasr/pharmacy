package com.pharmacy.gts.Adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pharmacy.gts.EventBus.PayOnline;
import com.pharmacy.gts.EventBus.ViewOrderDetail;
import com.pharmacy.gts.Models.MOrderModel;
import com.pharmacy.gts.R;

import org.greenrobot.eventbus.EventBus;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class MasterOrderAdapter extends RecyclerView.Adapter<MasterOrderAdapter.MyViewHolder> {

    private ArrayList<MOrderModel> mOrderModelArrayList;
    private Context mContext;
    private long time;


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView tvOderID, tvName, tvQTY, tvTotal, tvDiscount, tvGST, tvNett, tvDate, tvDateLabel, tvDueAmount, tvPaidStatus;
        Button btnPay, btnDetail;
        ArrayList<MOrderModel> mOrderModelArrayList = new ArrayList<MOrderModel>();
        Context ctx;
        private FrameLayout container;
        View status;

        MyViewHolder(View view, Context ctx, ArrayList<MOrderModel> mOrderModelArrayList) {
            super(view);
            this.mOrderModelArrayList = mOrderModelArrayList;
            this.ctx = ctx;
            container = (FrameLayout) itemView.findViewById(R.id.single_notification_layout);
            tvOderID = (TextView) view.findViewById(R.id.tv_order);
            tvName = (TextView) view.findViewById(R.id.tv_name);
            tvQTY = (TextView) view.findViewById(R.id.tv_qty);
            tvTotal = (TextView) view.findViewById(R.id.tv_total);
            tvDiscount = (TextView) view.findViewById(R.id.tv_discount);
            tvNett = (TextView) view.findViewById(R.id.tv_nett);
            tvDate = (TextView) view.findViewById(R.id.tv_reg_dt);
            tvDateLabel = (TextView) view.findViewById(R.id.date_label);
            tvDueAmount = (TextView) view.findViewById(R.id.tv_due_amt);
            tvPaidStatus = (TextView) view.findViewById(R.id.tv_paid_status);
            tvGST = (TextView) view.findViewById(R.id.tv_gst);
            btnPay = (Button) view.findViewById(R.id.btn_pay_online);
            btnDetail = (Button) view.findViewById(R.id.btn_view_details);
            status = (View) view.findViewById(R.id.line_status);
            container.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
//            int position = getAdapterPosition();
//            MOrderModel productModel = this.downlineArrayList.get(position);
//            Intent intent = new Intent(this.ctx, DetailProductActivity.class);
//            intent.putExtra("productid", productModel.getProductid());
//            intent.putExtra("pname", productModel.getPname());
//            this.ctx.startActivity(intent);

        }
    }

    public MasterOrderAdapter(Context context, ArrayList<MOrderModel> mOrderModelArrayList) {
        mContext = context;
        this.mOrderModelArrayList = mOrderModelArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_master_order_layout, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView, mContext, mOrderModelArrayList);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        setAnimation(holder.container, position);
        MOrderModel model = mOrderModelArrayList.get(position);

        holder.tvOderID.setText(model.getMorderid());
        holder.tvName.setText(model.getName());
        holder.tvQTY.setText(model.getTotalqty());
        holder.tvTotal.setText(mContext.getString(R.string.currency_symbol) + model.getTotalamount());
        holder.tvDueAmount.setText(mContext.getString(R.string.currency_symbol) + model.getDuenett());
        holder.tvGST.setText(mContext.getString(R.string.currency_symbol) + model.getGST());
        holder.tvDiscount.setText(mContext.getString(R.string.currency_symbol) + model.getDiscount());

        holder.tvNett.setText(mContext.getString(R.string.currency_symbol) + model.getNett());

        if (model.getPaystatus().equals("1")) {
            holder.tvPaidStatus.setText("Paid");
            holder.btnPay.setText("Pay History");
        } else {
            holder.tvPaidStatus.setText("Unpaid");
            holder.btnPay.setText("Pay Online");
        }

        holder.btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().postSticky(new PayOnline(model));
            }
        });


        holder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().postSticky(new ViewOrderDetail(model));
            }
        });
        String dateOrder = null;
        if (model.getStatus().equals("1")) {
            holder.status.setBackgroundResource(R.color.green);
            holder.tvDateLabel.setText("Delivered Date");
            dateOrder = model.getDelivereddate();
        } else {
            holder.status.setBackgroundResource(R.color.red);
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
