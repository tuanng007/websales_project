package com.websales.service.impl;

import java.util.List;

import com.websales.checkout.CheckoutInfo;
import com.websales.common.entity.CartItem;
import com.websales.common.entity.ShippingRate;

public interface ICheckoutService {
	public CheckoutInfo prepareCheckout(List<CartItem> cartItems, ShippingRate shippingRate);
}
