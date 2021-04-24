package com.pharmacy.gts.EventBus;

import com.pharmacy.gts.Database.Address.AddressItem;

public class AddressSelect {

    private AddressItem addressItem;

    public AddressSelect(AddressItem addressItem) {
        this.addressItem = addressItem;
    }

    public AddressItem getAddressItem() {
        return addressItem;
    }

    public void setAddressItem(AddressItem addressItem) {
        this.addressItem = addressItem;
    }
}
