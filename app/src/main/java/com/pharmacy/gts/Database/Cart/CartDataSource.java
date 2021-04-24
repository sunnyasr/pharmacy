package com.pharmacy.gts.Database.Cart;

import java.util.List;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface CartDataSource {

    Flowable<List<CartItem>> getALLCart(String uid);

    Single<Integer> countItemInCart(String uid);

    Single<Double> sumMRP(String uid);

    Single<Double> sumGST(String uid);

    Single<CartItem> getItemInCart(String productid, String uid);

    Completable insertOrReplaceAll(CartItem... cartItem);

    Single<Integer> updateCartItems(CartItem cartItem);

    Single<Integer> deleteCartItem(CartItem cart);

    Single<Integer> cleanCart(String uid);

}
