package com.pharmacy.gts.Models;

public class ProductModel {

    private String productid;
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

    public ProductModel() {
    }

    public ProductModel(String productid, int catid, String pname, int mrp, String mfr, String salts, String image, String pcode, String brand, String description, int unit, String created_date, String activated, String enabled) {
        this.productid = productid;
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

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
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
