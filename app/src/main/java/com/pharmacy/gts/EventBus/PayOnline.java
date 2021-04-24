package com.pharmacy.gts.EventBus;

import com.pharmacy.gts.Models.MOrderModel;

public class PayOnline {

    private MOrderModel mOrderModel;

    public PayOnline(MOrderModel mOrderModel) {
        this.mOrderModel = mOrderModel;
    }

    public MOrderModel getmOrderModel() {
        return mOrderModel;
    }

    public void setmOrderModel(MOrderModel mOrderModel) {
        this.mOrderModel = mOrderModel;
    }
}
