package com.websales.service.impl;

import java.util.List;

import com.websales.common.entity.Address;
import com.websales.common.entity.Customer;

public interface IAddressService {

	public List<Address> listAddressBook(Customer customer);
	
	public void save(Address address);

	public Address get(Integer addressId, Integer customerId);
	
	public void delete(Integer addressId, Integer customerId);
	
	public void setDefaultAddress(Integer defaultAddressId, Integer customerId) ;

	public Address getDefaultAddress(Customer customer);
}
