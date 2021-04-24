package com.pharmacy.gts.Database.Address;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface AddressDAO {

    @Query("SELECT * FROM Address WHERE uid=:uid")
    Flowable<List<AddressItem>> getALLAddress(String uid);

    @Query("SELECT  COUNT(*) FROM Address WHERE uid=:uid")
    Single<Integer> countItemInAddress(String uid);

    @Query("SELECT   * FROM Address WHERE defautselect=:defautselect AND uid =:uid")
    Single<AddressItem> getDefaultSelected(String defautselect, String uid);

    @Query("SELECT  * FROM Address WHERE addressid=:addressid AND uid=:uid")
    Single<AddressItem> getItemInAddress(String addressid, String uid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOrReplaceAll(AddressItem... addressItems);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Single<Integer> updateAddressItems(AddressItem addressItem);

    @Delete
    Single<Integer> deleteAddressItem(AddressItem addressItem);

    @Query("DELETE  FROM Address WHERE uid=:uid")
    Single<Integer> cleanAddress(String uid);

}
