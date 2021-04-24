package com.pharmacy.gts.EventBus;

import com.pharmacy.gts.Database.Address.AddressItem;

public class AddressDelete {

    private AddressItem addressItem;

    public AddressDelete(AddressItem addressItem) {
        this.addressItem = addressItem;
    }

    public AddressItem getAddressItem() {
        return addressItem;
    }

    public void setAddressItem(AddressItem addressItem) {
        this.addressItem = addressItem;
    }
}
