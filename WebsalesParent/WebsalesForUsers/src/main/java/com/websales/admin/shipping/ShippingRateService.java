package com.websales.admin.shipping;

import java.util.List;
import java.util.NoSuchElementException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.websales.admin.country.CountryRepository;
import com.websales.admin.paging.PagingAndSortingHelper;
import com.websales.admin.product.ProductRepository;
import com.websales.common.entity.Country;
import com.websales.common.entity.Product;
import com.websales.common.entity.ShippingRate;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ShippingRateService implements IShippingRateService{
	
	public static final int RATES_PER_PAGE = 10;
	private static final int DIM_DIVISOR = 139;	
 
	@Autowired
	private ShippingRateRepository shipRepo;
	
	@Autowired
	private CountryRepository countryRepo;
	
	@Autowired
	private ProductRepository productRepo;


	@Override
	public void listByPage(int pageNum, PagingAndSortingHelper helper) {
		// TODO Auto-generated method stub
		helper.listEntities(pageNum, RATES_PER_PAGE, shipRepo);
	}
	
	@Override
	public List<Country> listAllCountries() {
		// TODO Auto-generated method stub
		return countryRepo.findAllByOrderByNameAsc();
	}

	@Override
	public void save(ShippingRate rateInForm) throws ShippingRateAlreadyExistsException {
		// TODO Auto-generated method stub
		ShippingRate rateInDB = shipRepo.findByCountryAndState(
				rateInForm.getCountry().getId(), rateInForm.getState());
		boolean foundExistingRateInNewMode = rateInForm.getId() == null && rateInDB != null;
		boolean foundDifferentExistingRateInEditMode = rateInForm.getId() != null && rateInDB != null && !rateInDB.equals(rateInForm);

		if (foundExistingRateInNewMode || foundDifferentExistingRateInEditMode) {
			throw new ShippingRateAlreadyExistsException("There's already a rate for the destination "
						+ rateInForm.getCountry().getName() + ", " + rateInForm.getState()); 					
		}
		shipRepo.save(rateInForm);
	}

	@Override
	public ShippingRate get(Integer id) throws ShippingRateNotFoundException {
		// TODO Auto-generated method stub
		try {
			return shipRepo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + id);
		}
	}

	@Override
	public void updateCODSupport(Integer id, boolean codSupported) throws ShippingRateNotFoundException {
		// TODO Auto-generated method stub
		Long count = shipRepo.countById(id);
		if (count == null || count == 0) {
			throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + id);
		}

		shipRepo.updateCODSupport(id, codSupported);
	}

	@Override
	public void delete(Integer id) throws ShippingRateNotFoundException {
		// TODO Auto-generated method stub
		Long count = shipRepo.countById(id);
		if (count == null || count == 0) {
			throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + id);

		}
		shipRepo.deleteById(id);
	}
	
	public float calculateShippingCost(Integer productId, Integer countryId, String state) 
			throws ShippingRateNotFoundException {
		ShippingRate shippingRate = shipRepo.findByCountryAndState(countryId, state);

		if (shippingRate == null) {
			throw new ShippingRateNotFoundException("No shipping rate found for the given "
					+ "destination. You have to enter shipping cost manually.");
		}

		Product product = productRepo.findById(productId).get();

		float dimWeight = (product.getLength() * product.getWidth() * product.getHeight()) / DIM_DIVISOR;
		float finalWeight = product.getWeight() > dimWeight ? product.getWeight() : dimWeight;

		return finalWeight * shippingRate.getRate();
	}


}
