package com.pharmacy.gts.Models;

public class OrderProductModel {

    private String orderid;
    private String morderid;
    private String productid;
    private String addressid;
    private String price;
    private int quantity;
    private int amount;
    private int gst;
    private String discount;
    private String nett;
    private String status;
    private String orderdate;
    private String delivereddate;
    private int catid;
    private String pname;
    private int mrp;
    private String mfr;
    private String salts;
    private String image;
    private String pcode;
    private String brand;
    private String description;
    private int unit;
    private String created_date;
    private String activated;
    private String enabled;
    public OrderProductModel() {
    }

    public OrderProductModel(String orderid, String morderid, String productid, String addressid, String price, int quantity, int amount, int gst, String discount, String nett, String status, String orderdate, String delivereddate, int catid, String pname, int mrp, String mfr, String salts, String image, String pcode, String brand, String description, int unit, String created_date, String activated, String enabled) {
        this.orderid = orderid;
        this.morderid = morderid;
        this.productid = productid;
        this.addressid = addressid;
        this.price = price;
        this.quantity = quantity;
        this.amount = amount;
        this.gst = gst;
        this.discount = discount;
        this.nett = nett;
        this.status = status;
        this.orderdate = orderdate;
        this.delivereddate = delivereddate;
        this.catid = catid;
        this.pname = pname;
        this.mrp = mrp;
        this.mfr = mfr;
        this.salts = salts;
        this.image = image;
        this.pcode = pcode;
        this.brand = brand;
        this.description = description;
        this.unit = unit;
        this.created_date = created_date;
        this.activated = activated;
        this.enabled = enabled;
    }


    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getMorderid() {
        return morderid;
    }

    public void setMorderid(String morderid) {
        this.morderid = morderid;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public String getAddressid() {
        return addressid;
    }

    public void setAddressid(String addressid) {
        this.addressid = addressid;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getGst() {
        return gst;
    }

    public void setGst(int gst) {
        this.gst = gst;
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

    public int getCatid() {
        return catid;
    }

    public void setCatid(int catid) {
        this.catid = catid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public int getMrp() {
        return mrp;
    }

    public void setMrp(int mrp) {
        this.mrp = mrp;
    }

    public String getMfr() {
        return mfr;
    }

    public void setMfr(String mfr) {
        this.mfr = mfr;
    }

    public String getSalts() {
        return salts;
    }

    public void setSalts(String salts) {
        this.salts = salts;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPcode() {
        return pcode;
    }

    public void setPcode(String pcode) {
        this.pcode = pcode;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
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
