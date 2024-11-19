package com.websales.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.websales.admin.repository.RoleRepository;
import com.websales.common.entity.Role;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class RoleRepositoryTest {
	
	@Autowired
	private RoleRepository repo;
	
	@Test
	public void testCreateFirstRole() { 
		
		Role roleAdmin = new Role("Admin", "Manage everything");
		Role savedRole = repo.save(roleAdmin);
		
		assertThat(savedRole.getId()).isGreaterThan(0);
		
	}
	
	@Test
	public void testCreateRestRoles() { 
		Role roleSaleperson = new Role("Saleperon", "Manage orders, questions, product reviews and sales report");
		Role roleStorekeeper = new Role("Storekeeper", "Manage products, brands, categories");
		Role roleShipper = new Role("Shipper", "View products, view orders, update order status");
		
		repo.saveAll(List.of(roleSaleperson, roleStorekeeper, roleShipper));
		
	}
	
}
