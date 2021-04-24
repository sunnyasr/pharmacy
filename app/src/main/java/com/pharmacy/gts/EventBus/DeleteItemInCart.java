package com.pharmacy.gts.EventBus;

import com.pharmacy.gts.Database.Cart.CartItem;

public class DeleteItemInCart {

    private CartItem cartItem;

    public DeleteItemInCart(CartItem cartItem) {
        this.cartItem = cartItem;
    }

    public CartItem getCartItem() {
        return cartItem;
    }

    public void setCartItem(CartItem cartItem) {
        this.cartItem = cartItem;
    }
}
