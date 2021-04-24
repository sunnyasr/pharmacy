package com.pharmacy.gts.Models;

public class MOrderModel {

    private String morderid;
    private String memberid;
    private String addressid;
    private String totalamount;
    private String totalqty;
    private String discount;
    private String nett;
    private String GST;
    private String status;
    private String orderdate;
    private String delivereddate;
    private String name;
    private String phone;
    private String address;
    private String hno;
    private String postcode;
    private String city;
    private String paystatus;
    private String duenett;
    private int addefault;
    private String created_date;
    private String activated;
    private String enabled;

    public MOrderModel() {
    }


    public MOrderModel(String morderid, String memberid, String addressid, String totalamount, String totalqty, String discount, String nett, String GST, String status, String orderdate, String delivereddate, String name, String phone, String address, String hno, String postcode, String city, String paystatus, String duenett, int addefault, String created_date, String activated, String enabled) {
        this.morderid = morderid;
        this.memberid = memberid;
        this.addressid = addressid;
        this.totalamount = totalamount;
        this.totalqty = totalqty;
        this.discount = discount;
        this.nett = nett;
        this.GST = GST;
        this.status = status;
        this.orderdate = orderdate;
        this.delivereddate = delivereddate;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.hno = hno;
        this.postcode = postcode;
        this.city = city;
        this.paystatus = paystatus;
        this.duenett = duenett;
        this.addefault = addefault;
        this.created_date = created_date;
        this.activated = activated;
        this.enabled = enabled;
    }

    public String getMorderid() {
        return morderid;
    }

    public void setMorderid(String morderid) {
        this.morderid = morderid;
    }

    public String getMemberid() {
        return memberid;
    }

    public void setMemberid(String memberid) {
        this.memberid = memberid;
    }

    public String getAddressid() {
        return addressid;
    }

    public void setAddressid(String addressid) {
        this.addressid = addressid;
    }

    public String getTotalamount() {
        return totalamount;
    }

    public void setTotalamount(String totalamount) {
        this.totalamount = totalamount;
    }

    public String getTotalqty() {
        return totalqty;
    }

    public void setTotalqty(String totalqty) {
        this.totalqty = totalqty;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getNett() {
        return nett;
    }

    public void setNett(String nett) {
        this.nett = nett;
    }

    public String getGST() {
        return GST;
    }

    public void setGST(String GST) {
        this.GST = GST;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderdate() {
        return orderdate;
    }

    public void setOrderdate(String orderdate) {
        this.orderdate = orderdate;
    }

    public String getDelivereddate() {
        return delivereddate;
    }

    public void setDelivereddate(String delivereddate) {
        this.delivereddate = delivereddate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHno() {
        return hno;
    }

    public void setHno(String hno) {
        this.hno = hno;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPaystatus() {
        return paystatus;
    }

    public void setPaystatus(String paystatus) {
        this.paystatus = paystatus;
    }

    public String getDuenett() {
        return duenett;
    }

    public void setDuenett(String duenett) {
        this.duenett = duenett;
    }

    public int getAddefault() {
        return addefault;
    }

    public void setAddefault(int addefault) {
        this.addefault = addefault;
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
