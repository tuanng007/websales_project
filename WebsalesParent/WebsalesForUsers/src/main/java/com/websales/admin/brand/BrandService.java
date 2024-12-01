package com.websales.admin.brand;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.websales.common.entity.Brand;
import com.websales.common.entity.Category;

@Service
public class BrandService {
	
	public static final int BRANDS_PER_PAGE = 4;
	
	
	@Autowired
	private BrandRepository brandRepo;
	
	public List<Brand> listAll() {
		return (List<Brand>) brandRepo.findAll();
	}
	
	public Page<Brand> listByPage(int pageNum, String sortDir, String keyword) { 
		Sort sort = Sort.by("name");
		
		if(sortDir.equals("asc")) { 
			sort = sort.ascending();
		} else if(sortDir.equals("desc")) { 
			sort = sort.descending();
		}
		Pageable pageable = PageRequest.of(pageNum - 1, BRANDS_PER_PAGE, sort);
		
		
		if(keyword != null) { 
			return brandRepo.findAll(keyword, pageable);
		} else {
			return  brandRepo.findAll(pageable);
		}
		
		
	}
	
	public Brand get(Integer id) throws BrandNotFoundException{
		try {
			return brandRepo.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new BrandNotFoundException("Could not find any Brand with ID: " + id);
		}
	}
	
	public void delete(Integer id) throws BrandNotFoundException { 
		Long brandById = brandRepo.countById(id);
	
		if(brandById == 0 || brandById == null) { 
			throw new BrandNotFoundException("Could not find nay Brand with ID: " + id);
		} else { 
			brandRepo.deleteById(id);
		}
	
	}
	
	public String checkUnique(Integer id, String name) {
		boolean isCreatingNew = (id == null || id == 0);
		
		Brand brandByName = brandRepo.findByName(name);
		
		if(isCreatingNew) { 
			if(brandByName != null) { 
				return "Duplicate";
			}
		} else  { 
			if(brandByName != null && brandByName.getId() != id) {
				return "Duplicate";
			}
		}
			
		return "OK";
	}
	
	public Brand save(Brand brand) {
		return brandRepo.save(brand);
	}
	
}
