package com.websales.admin.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.websales.common.entity.Brand;
import com.websales.common.entity.Category;
import com.websales.common.entity.Product;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ProductRepositoryTest {

	@Autowired
	private ProductRepository productRepo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateProduct() { 
		Brand brand = entityManager.find(Brand.class, 8);
		Category category = entityManager.find(Category.class, 2);
		
		Product product = new Product();
		product.setName("Lenovo Yoga 9 Slim Pro");
		product.setAlias("Lenovo Yoga 9");
		product.setShortDescription("A product from Lenovo");
		product.setFullDescription("Luxury and high end Laptop");
		
		product.setBrand(brand);
		product.setCategory(category);
		
		product.setPrice(200);
		product.setCost(400);
		product.setEnabled(true);
		product.setInStock(true);
		product.setCreatedTime(new Date());
		product.setUpdatedTime(new Date());
		
		Product savedProduct = productRepo.save(product);
		
		assertThat(savedProduct).isNotNull();
		assertThat(savedProduct.getId()).isGreaterThan(0);
		
	}
	
	@Test
	public void testListAllProducts() { 
		Iterable<Product> iterableProducts = productRepo.findAll();
		
		iterableProducts.forEach(pro -> System.out.println(pro));
		
	}	
	
	
	@Test
	public void testGetProduct() { 
		Integer id = 1;
		Product product = productRepo.findById(id).get();
		
		System.out.println(product);
		assertThat(product).isNotNull();
	}
	
	@Test
	public void testUpdateProduct()  {
		Integer id = 1;
		Product product = productRepo.findById(id).get();
		product.setPrice(10000000);
		
		productRepo.save(product);
		
		Product updatedProduct = entityManager.find(Product.class, id);
		
		assertThat(updatedProduct.getPrice()).isEqualTo(10000000);
	} 
	
	
	@Test
	public void testDeleteProduct() { 
		Integer id = 4;
		productRepo.deleteById(id);
		
		Optional<Product> result = productRepo.findById(id);
		
		assertThat(!result.isPresent());
	}	
	
	@Test
	public void testSaveImageProduct() {
		Integer productId = 1;
		Product product = productRepo.findById(productId).get();
		
		product.setMainImage("main Image.png");
		product.addExtraImage("extra_image 1.png");
		product.addExtraImage("extra_image 2.png");
		product.addExtraImage("extra_image 3.png");
		
		Product savedProduct = productRepo.save(product);
		
		assertThat(savedProduct.getImages().size()).isEqualTo(3);

	}
}
