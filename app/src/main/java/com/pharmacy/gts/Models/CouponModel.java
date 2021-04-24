package com.pharmacy.gts.Models;

public class CouponModel {

    private String couponid;
    private String coupon;
    private String imagecoupon;
    private String memberid;
    private String percent;
    private String minpurchase;
    private String startdate ;
    private String enddate;
    private String description;
    private String created_date;
    private String activated;
    private String enabled;

    public CouponModel() {
    }

    public CouponModel(String couponid, String coupon, String imagecoupon, String memberid, String percent, String minpurchase, String startdate, String enddate, String description, String created_date, String activated, String enabled) {
        this.couponid = couponid;
        this.coupon = coupon;
        this.imagecoupon = imagecoupon;
        this.memberid = memberid;
        this.percent = percent;
        this.minpurchase = minpurchase;
        this.startdate = startdate;
        this.enddate = enddate;
        this.description = description;
        this.created_date = created_date;
        this.activated = activated;
        this.enabled = enabled;
    }

    public String getCouponid() {
        return couponid;
    }

    public void setCouponid(String couponid) {
        this.couponid = couponid;
    }

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public String getImagecoupon() {
        return imagecoupon;
    }

    public void setImagecoupon(String imagecoupon) {
        this.imagecoupon = imagecoupon;
    }

    public String getMemberid() {
        return memberid;
    }

    public void setMemberid(String memberid) {
        this.memberid = memberid;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public String getMinpurchase() {
        return minpurchase;
    }

    public void setMinpurchase(String minpurchase) {
        this.minpurchase = minpurchase;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getActivated() {
        return activated;
    }

    public void setActivated(String activated) {
        this.activated = activated;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }
}
