package com.pharmacy.gts.EventBus;

public class AddressUpdate {

    private String addressID;

    public AddressUpdate(String addressID) {
        this.addressID = addressID;
    }

    public String getAddressID() {
        return addressID;
    }

    public void setAddressID(String addressID) {
        this.addressID = addressID;
    }
}
