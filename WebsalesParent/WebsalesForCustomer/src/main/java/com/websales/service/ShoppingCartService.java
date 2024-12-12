package com.websales.service;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

 
import com.websales.common.entity.CartItem;
import com.websales.common.entity.Customer;
import com.websales.common.entity.Product;
import com.websales.common.exception.ShoppingCartException;
import com.websales.repository.CartItemRepository;
import com.websales.repository.ProductRepository;
import com.websales.service.impl.IShoppingCartService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ShoppingCartService implements IShoppingCartService{

	@Autowired
	private CartItemRepository cartRepo;
	@Autowired
	private ProductRepository productRepo;

	@Override
	public Integer addProduct(Integer productId, Integer quantity, Customer customer) throws ShoppingCartException {
		// TODO Auto-generated method stub
		
		
		Integer updatedQuantity = quantity;
		
		
		Product product = new Product(productId);
		
 
		CartItem cartItem = cartRepo.findByCustomerAndProduct(customer, product);
		
 
		if (cartItem != null) {
			
			updatedQuantity = cartItem.getQuantity() + quantity;
			
 
			if (updatedQuantity > 5) {
				
 				
				throw new ShoppingCartException("Could not add more " + quantity + " item(s)"
						+ " because there's already " + cartItem.getQuantity() + " item(s) "
						+ "in your shopping cart. Maximum allowed quantity is 5.");
			}
		} else {
			cartItem = new CartItem();
			cartItem.setCustomer(customer);
			cartItem.setProduct(product);
		}

 		
		cartItem.setQuantity(updatedQuantity);
		
 
		cartRepo.save(cartItem);

		return updatedQuantity;
	}

	@Override
	public List<CartItem> listCartItems(Customer customer) {
		// TODO Auto-generated method stub
	 
		return cartRepo.findByCustomer(customer);
	}
	
	@Override
	public float updateQuantity(Integer productId, Integer quantity, Customer customer) {
		
	 
		cartRepo.updateQuantity(quantity, customer.getId(), productId);
		
		Product product = productRepo.findById(productId).get();
		
		 
		
		float subtotal = product.getDiscountPrice() * quantity;
		
 		
		return subtotal;
		
	}

	@Override
	public void removeProduct(Integer productId, Customer customer) {
		// TODO Auto-generated method stub
		
 		
		cartRepo.deleteByCustomerAndProduct(customer.getId(), productId);
	}
	
	@Override
	public void deleteByCustomer(Customer customer) {
		cartRepo.deleteByCustomer(customer.getId());
	}

}
