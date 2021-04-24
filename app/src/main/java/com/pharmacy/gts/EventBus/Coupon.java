package com.pharmacy.gts.EventBus;

import com.pharmacy.gts.Models.CouponModel;

public class Coupon {

    private CouponModel couponModel;

    public Coupon(CouponModel couponModel) {
        this.couponModel = couponModel;
    }

    public CouponModel getCouponModel() {
        return couponModel;
    }

    public void setCouponModel(CouponModel couponModel) {
        this.couponModel = couponModel;
    }
}
