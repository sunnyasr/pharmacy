package com.pharmacy.gts.Database.Address;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class LocalAddressDataSource implements AddressDataSource {

    private AddressDAO addressDAO;
    private LocalAddressDataSource instance;

    public LocalAddressDataSource(AddressDAO addressDAO) {
        this.addressDAO = addressDAO;
    }

    public LocalAddressDataSource getInstance(AddressDAO addressDAO) {
        if (addressDAO == null) {
            instance = new LocalAddressDataSource(addressDAO);
        }
        return instance;
    }

    @Override
    public Flowable<List<AddressItem>> getALLAddress(String uid) {
        return addressDAO.getALLAddress(uid);
    }

    @Override
    public Single<Integer> countItemInAddress(String uid) {
        return addressDAO.countItemInAddress(uid);
    }

    @Override
    public Single<AddressItem> getDefaultSelected(String defautselect, String uid) {
        return addressDAO.getDefaultSelected(defautselect, uid);
    }

    @Override
    public Single<AddressItem> getItemInAddress(String addressid, String uid) {
        return addressDAO.getItemInAddress(addressid,uid);
    }

    @Override
    public Completable insertOrReplaceAll(AddressItem... addressItems) {
        return addressDAO.insertOrReplaceAll(addressItems);
    }

    @Override
    public Single<Integer> updateAddressItems(AddressItem addressItem) {
        return addressDAO.updateAddressItems(addressItem);
    }

    @Override
    public Single<Integer> deleteAddressItem(AddressItem addressItem) {
        return addressDAO.deleteAddressItem(addressItem);
    }

    @Override
    public Single<Integer> cleanAddress(String uid) {
        return addressDAO.cleanAddress(uid);
    }
}
