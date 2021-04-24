package com.pharmacy.gts.Database.Cart;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Cart")
public class CartItem {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "productid")
    private String productid;

    @NonNull
    @ColumnInfo(name = "cartid")
    private String cartid;

    @ColumnInfo(name = "pname")
    private String pname;

    @ColumnInfo(name = "image")
    private String image;


    @ColumnInfo(name = "mrp")
    private Double mrp;

    @ColumnInfo(name = "quantity")
    private int quantity;

    @ColumnInfo(name = "gst")
    private int gst;

    @ColumnInfo(name = "uid")
    private String uid;

    @NonNull
    public String getProductid() {
        return productid;
    }

//    public CartItem() {
//    }

//    public CartItem(@NonNull String cartid, @NonNull String productid, String pname, String image, Double mrp, int quantity, String uid) {
//        this.cartid = cartid;
//        this.productid = productid;
//        this.pname = pname;
//        this.image = image;
//        this.mrp = mrp;
//        this.quantity = quantity;
//        this.uid = uid;
//    }

    @NonNull
    public String getCartid() {
        return cartid;
    }

    public void setCartid(@NonNull String cartid) {
        this.cartid = cartid;
    }

    public void setProductid(@NonNull String productid) {
        this.productid = productid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Double getMrp() {
        return mrp;
    }

    public void setMrp(Double mrp) {
        this.mrp = mrp;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getGst() {
        return gst;
    }

    public void setGst(int gst) {
        this.gst = gst;
    }
}
