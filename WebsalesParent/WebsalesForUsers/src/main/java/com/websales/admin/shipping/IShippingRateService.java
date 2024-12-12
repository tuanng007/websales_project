package com.websales.admin.shipping;

import java.util.List;
import com.websales.common.entity.Country;
import com.websales.common.entity.ShippingRate;

public interface IShippingRateService {

	public void listByPage(int pageNum, com.websales.admin.paging.PagingAndSortingHelper helper);
	
	public List<Country> listAllCountries();
	
	public void save(ShippingRate rateInForm) throws ShippingRateAlreadyExistsException;
	
	public ShippingRate get(Integer id) throws ShippingRateNotFoundException;
	
	public void updateCODSupport(Integer id, boolean codSupported) throws ShippingRateNotFoundException;
	
	public void delete(Integer id) throws ShippingRateNotFoundException;
	
}
