package com.websales.service;

import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.websales.checkout.CheckoutInfo;
import com.websales.common.entity.CartItem;
import com.websales.common.entity.ShippingRate;
import com.websales.service.impl.ICheckoutService;
import com.websales.util.CheckoutUtil;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CheckoutService implements ICheckoutService{
	
	private static final int DIM_DIVISOR = 139;

	@Override
	public CheckoutInfo prepareCheckout(List<CartItem> cartItems, ShippingRate shippingRate) {
		
		
		// TODO Auto-generated method stub
		CheckoutInfo checkoutInfo = new CheckoutInfo();

		float productCost = CheckoutUtil.calculateProductCost(cartItems);
		float productTotal = CheckoutUtil.calculateProductTotal(cartItems);
		float shippingCostTotal = CheckoutUtil.calculateShippingCost(cartItems, shippingRate, DIM_DIVISOR);
		float paymentTotal = productTotal + shippingCostTotal;
	

		checkoutInfo.setProductCost(productCost);
		checkoutInfo.setProductTotal(productTotal);
		checkoutInfo.setShippingCostTotal(shippingCostTotal);
		checkoutInfo.setPaymentTotal(paymentTotal);

		checkoutInfo.setDeliverDays(shippingRate.getDays());
		checkoutInfo.setCodSupported(shippingRate.isCodSupported());
		

		return checkoutInfo;
	}
}
