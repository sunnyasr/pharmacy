package com.pharmacy.gts.Database.Cart;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class LocalCartDataSource implements CartDataSource {

    private CartDAO cartDAO;
    private static LocalCartDataSource instance;

    public LocalCartDataSource(CartDAO cartDAO) {
        this.cartDAO = cartDAO;
    }

    public static LocalCartDataSource getInstance(CartDAO cartDAO) {
        if (instance == null)
            instance = new LocalCartDataSource(cartDAO);
        return instance;
    }


    @Override
    public Flowable<List<CartItem>> getALLCart(String uid) {
        return cartDAO.getALLCart(uid);
    }

    @Override
    public Single<Integer> countItemInCart(String uid) {
        return cartDAO.countItemInCart(uid);
    }

    @Override
    public Single<Double> sumMRP(String uid) {
        return cartDAO.sumMRP(uid);
    }

    @Override
    public Single<Double> sumGST(String uid) {
        return cartDAO.sumGST(uid);
    }

    @Override
    public Single<CartItem> getItemInCart(String productid, String uid) {
        return cartDAO.getItemInCart(productid,uid);
    }

    @Override
    public Completable insertOrReplaceAll(CartItem... cartItem) {
        return cartDAO.insertOrReplaceAll(cartItem);
    }

    @Override
    public Single<Integer> updateCartItems(CartItem cartItem) {
        return cartDAO.updateCartItems(cartItem);
    }

    @Override
    public Single<Integer> deleteCartItem(CartItem cart) {
        return cartDAO.deleteCartItem(cart);
    }

    @Override
    public Single<Integer> cleanCart(String uid) {
        return cartDAO.cleanCart(uid);
    }
}
