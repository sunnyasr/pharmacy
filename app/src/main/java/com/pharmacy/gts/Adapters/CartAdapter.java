package com.pharmacy.gts.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.pharmacy.gts.App.APIConfig;
import com.pharmacy.gts.Database.Cart.CartItem;
import com.pharmacy.gts.EventBus.DeleteItemInCart;
import com.pharmacy.gts.EventBus.UpdateItemInCart;
import com.pharmacy.gts.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    private List<CartItem> cartItemList;
    private Context mContext;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView cart_image;
        TextView product_name, product_price, gst, total;
        ElegantNumberButton numberButton;
        ImageView ivDelete;

        List<CartItem> cartItemList = new ArrayList<CartItem>();
        Context ctx;

        MyViewHolder(View view, Context ctx, List<CartItem> cartItemList) {
            super(view);
            this.cartItemList = cartItemList;
            this.ctx = ctx;
            cart_image = (ImageView) view.findViewById(R.id.iv_cart);
            product_name = (TextView) view.findViewById(R.id.tv_product_name);
            product_price = (TextView) view.findViewById(R.id.tv_product_price);
            gst = (TextView) view.findViewById(R.id.tv_gst);
            total = (TextView) view.findViewById(R.id.tv_total);
            ivDelete = (ImageView) view.findViewById(R.id.iv_delete);
            numberButton = (ElegantNumberButton) view.findViewById(R.id.number_button);
        }

    }

    public CartAdapter(Context context, List<CartItem> empViewArrayList) {
        mContext = context;
        this.cartItemList = empViewArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_cart_item_layout, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView, mContext, cartItemList);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

//        setAnimation(holder.container, position);
        CartItem item = cartItemList.get(position);

        Glide.with(mContext).load(APIConfig.BASE_IMAGE + "products/" + item.getImage())
                .into(holder.cart_image);
        holder.product_price.setText("₹ " + String.format("%.2f", item.getMrp()) + " x " + item.getQuantity());
        holder.product_name.setText(item.getPname());
        holder.gst.setText("+ GST : " + item.getGst() + "%");

        holder.total.setText("₹ " + ((item.getMrp() * item.getQuantity()) +(item.getMrp() * item.getQuantity() * item.getGst() / 100)));

        holder.numberButton.setNumber(String.valueOf(item.getQuantity()));

        //Event
        holder.numberButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                item.setQuantity(newValue);
                EventBus.getDefault().postSticky(new UpdateItemInCart(item));
            }
        });

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().postSticky(new DeleteItemInCart(item));
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

}
