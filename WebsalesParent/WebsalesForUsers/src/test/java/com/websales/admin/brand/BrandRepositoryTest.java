package com.websales.admin.brand;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.websales.common.entity.Brand;
import com.websales.common.entity.Category;

@DataJpaTest(showSql = true)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class BrandRepositoryTest {
	
	@Autowired
	private BrandRepository brandRepo;
	
	@Test
	public void testCreateBrand() {
		Brand brand = new Brand("HyperX");
		Category category = new Category(4);
		brand.getCategories().add(category);
		
		Brand savedBrand = brandRepo.save(brand);
		
		assertThat(savedBrand).isNotNull();
		assertThat(savedBrand.getId()).isGreaterThan(0);
	}
	
	
	@Test 
	public void testCreateBrand2() {
		Brand brand  = new Brand("Samsung");
		Category category = new Category(4);
		brand.getCategories().add(category);
		
		Brand savedBrand = brandRepo.save(brand);
		
		assertThat(savedBrand).isNotNull();
		assertThat(savedBrand.getId()).isGreaterThan(0);
	} 	
	
	@Test
	public void testCreatBrand3() { 
		Brand brand = new Brand("Google Pixel");
		Category category = new Category(1);
		Category category2 = new Category(2);
		
		brand.getCategories().add(category);
		brand.getCategories().add(category2);
		
		Brand savedBrand = brandRepo.save(brand);
		
		assertThat(savedBrand).isNotNull();
		assertThat(savedBrand.getId()).isGreaterThan(0);
		
	}
	
	@Test
	public void testListBrands() { 
		Iterable<Brand> listBrands = brandRepo.findAll();
		listBrands.forEach(brand -> System.out.println(brand));
		
		assertThat(listBrands).isNotEmpty();
	}
	
	@Test
	public void testEditBrand() { 
		String name = "Samsung";
		Brand hyperx = brandRepo.findById(2).get();
		
		hyperx.setName(name);
		
		Brand savedBrand = brandRepo.save(hyperx);
		
		assertThat(savedBrand.getId()).isGreaterThan(0);
	}
	
	
	
	@Test
	public void testDeleteBrand() { 
		Integer id = 2;
		
		brandRepo.deleteById(id);
		
		Optional<Brand> result = brandRepo.findById(id);
		
		assertThat(result.isEmpty());
	}
	
}
