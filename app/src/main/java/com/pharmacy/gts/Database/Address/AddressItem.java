package com.pharmacy.gts.Database.Address;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Address")
public class AddressItem {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "addressid")
    private String addressid;

    @NonNull
    @ColumnInfo(name = "uid")
    private String uid;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "myaddress")
    private String address;

    @ColumnInfo(name = "hno")
    private String hno;

    @ColumnInfo(name = "phone")
    private String phone;


    @ColumnInfo(name = "city")
    private String city;

    @ColumnInfo(name = "postcode")
    private String postcode;

    @ColumnInfo(name = "defautselect")
    private int defautselect;


//    public AddressItem() {
//    }

//    public AddressItem(@NonNull String addressid, @NonNull String uid, String name, String address, String hno, String phone, String city, String postcode, int defautselect) {
//        this.addressid = addressid;
//        this.uid = uid;
//        this.name = name;
//        this.address = address;
//        this.hno = hno;
//        this.phone = phone;
//        this.city = city;
//        this.postcode = postcode;
//        this.defautselect = defautselect;
//    }

    @NonNull
    public String getAddressid() {
        return addressid;
    }

    public void setAddressid(@NonNull String addressid) {
        this.addressid = addressid;
    }

    @NonNull
    public String getUid() {
        return uid;
    }

    public void setUid(@NonNull String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public int getDefautselect() {
        return defautselect;
    }

    public void setDefautselect(int defautselect) {
        this.defautselect = defautselect;
    }
}
