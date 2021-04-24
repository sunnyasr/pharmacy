package com.pharmacy.gts.Database.Address;

import java.util.List;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface AddressDataSource {

    Flowable<List<AddressItem>> getALLAddress(String uid);

    Single<Integer> countItemInAddress(String uid);

    Single<AddressItem> getDefaultSelected(String defautselect, String uid);

    Single<AddressItem> getItemInAddress(String addressid, String uid);

    Completable insertOrReplaceAll(AddressItem... addressItems);

    Single<Integer> updateAddressItems(AddressItem addressItem);

    Single<Integer> deleteAddressItem(AddressItem addressItem);

    Single<Integer> cleanAddress(String uid);

}
