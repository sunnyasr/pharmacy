package com.pharmacy.gts.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pharmacy.gts.Database.Address.AddressItem;
import com.pharmacy.gts.EventBus.AddressDelete;
import com.pharmacy.gts.EventBus.AddressSelect;
import com.pharmacy.gts.EventBus.AddressUpdate;
import com.pharmacy.gts.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.MyViewHolder> {

    private List<AddressItem> addressItemList;
    private Context mContext;


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvSName, tvSAddress, tvSScity, tvSPinCode, tvSMobile, tvDefault;
        List<AddressItem> addressItemList = new ArrayList<AddressItem>();
        Button btnEdit, btnDelete;
        Context ctx;

        FrameLayout container;

        MyViewHolder(View view, Context ctx, List<AddressItem> addressItemList) {
            super(view);
            this.addressItemList = addressItemList;
            this.ctx = ctx;
            tvSName = (TextView) view.findViewById(R.id.tv_name_default);
            tvSAddress = (TextView) view.findViewById(R.id.tv_address_default);
            tvSScity = (TextView) view.findViewById(R.id.tv_city_default);
            tvSPinCode = (TextView) view.findViewById(R.id.tv_pin_default);
            tvSMobile = (TextView) view.findViewById(R.id.tv_mobile_default);
            tvDefault = (TextView) view.findViewById(R.id.tv_default);
            btnEdit = (Button) view.findViewById(R.id.btn_edit);
            btnDelete = (Button) view.findViewById(R.id.btn_delete);
            container = (FrameLayout) itemView.findViewById(R.id.single_notification_layout);
            container.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//            Toast.makeText(ctx, ""+addressItemList.get(getAdapterPosition()).getAddressid(), Toast.LENGTH_SHORT).show();
            EventBus.getDefault().postSticky(new AddressSelect(addressItemList.get(getAdapterPosition())));
        }
    }

    public AddressAdapter(Context context, List<AddressItem> empViewArrayList) {
        mContext = context;
        this.addressItemList = empViewArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_address_layout, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView, mContext, addressItemList);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        AddressItem item = addressItemList.get(position);
        holder.tvSName.setText(item.getName());
        holder.tvSAddress.setText(item.getAddress());
        holder.tvSMobile.setText(item.getPhone());
        holder.tvSPinCode.setText(item.getPostcode());
        holder.tvSScity.setText(item.getCity());
//        holder.tvDefault.setVisibility(View.VISIBLE);
//        holder.tvDefault.setText(""+item.getDefautselect());

        if (item.getDefautselect() == 1) {
            holder.tvDefault.setVisibility(View.VISIBLE);
        }
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().postSticky(new AddressDelete(item));
            }
        });


        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().postSticky(new AddressUpdate(item.getAddressid()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return addressItemList.size();
    }

}
