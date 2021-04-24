package com.pharmacy.gts.Database.Cart;

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
public interface CartDAO {

    @Query("SELECT * FROM Cart WHERE uid=:uid")
    Flowable<List<CartItem>> getALLCart(String uid);

    @Query("SELECT  COUNT(*) FROM Cart WHERE uid=:uid")
    Single<Integer> countItemInCart(String uid);

    @Query("SELECT  SUM( quantity*mrp) FROM Cart WHERE uid=:uid")
    Single<Double> sumMRP(String uid);

    @Query("SELECT  SUM(quantity*mrp*gst/100) FROM Cart WHERE uid=:uid")
    Single<Double> sumGST(String uid);

    @Query("SELECT  * FROM Cart WHERE productid=:productid AND uid=:uid")
    Single<CartItem> getItemInCart(String productid, String uid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOrReplaceAll(CartItem... cartItem);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Single<Integer> updateCartItems(CartItem cartItem);

    @Delete
    Single<Integer> deleteCartItem(CartItem cart);

    @Query("DELETE  FROM Cart WHERE uid=:uid")
    Single<Integer> cleanCart(String uid);

}
