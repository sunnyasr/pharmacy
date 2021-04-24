package com.pharmacy.gts.EventBus;

import com.pharmacy.gts.Models.MOrderModel;

public class ViewOrderDetail {

    private MOrderModel mOrderModel;

    public ViewOrderDetail(MOrderModel mOrderModel) {
        this.mOrderModel = mOrderModel;
    }

    public MOrderModel getmOrderModel() {
        return mOrderModel;
    }

    public void setmOrderModel(MOrderModel mOrderModel) {
        this.mOrderModel = mOrderModel;
    }
}
