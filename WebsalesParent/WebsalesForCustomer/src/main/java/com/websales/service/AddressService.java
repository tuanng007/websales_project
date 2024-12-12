package com.websales.service;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.websales.common.entity.Address;
import com.websales.common.entity.Customer;
import com.websales.repository.AddressRepository;
import com.websales.service.impl.IAddressService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class AddressService implements IAddressService{

	@Autowired 
	private AddressRepository repo;
	
	@Override
	public List<Address> listAddressBook(Customer customer) {
		// TODO Auto-generated method stub
		return repo.findByCustomer(customer);
	}
	
	@Override
	public void save(Address address) {
		repo.save(address);
	}

	@Override
	public Address get(Integer addressId, Integer customerId) {
		return repo.findByIdAndCustomer(addressId, customerId);
	}

	@Override
	public void delete(Integer addressId, Integer customerId) {
		repo.deleteByIdAndCustomer(addressId, customerId);
	}
	
	@Override
	public void setDefaultAddress(Integer defaultAddressId, Integer customerId) {
		if (defaultAddressId > 0) {
			repo.setDefaultAddress(defaultAddressId);
		}

		repo.setNonDefaultForOthers(defaultAddressId, customerId);
	}
	
	@Override
	public Address getDefaultAddress(Customer customer) {
		return repo.findDefaultByCustomer(customer.getId());
	}

}
