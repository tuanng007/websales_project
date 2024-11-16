package com.websales.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.websales.common.entity.Role;
import com.websales.common.entity.User;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTest {
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateUser() { 
		Role roleAdmin = entityManager.find(Role.class, 1);
		User userTuan = new User("tuanng007@gmail.com", "123", "Nguyen Hoang", "Tuan");
		userTuan.addRole(roleAdmin);
		
		User savedUser = userRepo.save(userTuan);
		assertThat(savedUser.getId()).isGreaterThan(0);
	}
	
	
	@Test
	public void testCreateUserWithMoreRoles() { 
		User userDavid = new User("david585@gmail.com","1234", "David", "Goggin");
		Role roleStorekeeper = new Role(3);
		Role roleShipper = new Role(4);
		
		userDavid.addRole(roleShipper);
		userDavid.addRole(roleStorekeeper);
		
		User savedUser = userRepo.save(userDavid);
		assertThat(savedUser.getId()).isGreaterThan(0);
		
	}
	
	@Test
	public void testListAllUsers() { 
		Iterable<User> listUsers = userRepo.findAll();
		listUsers.forEach(user -> System.out.println(user));
	}
	
	@Test
	public void deleteUser() { 
		Integer userId = 2;
		userRepo.deleteById(userId);
	}
	
	@Test
	public void getUserByEmail() { 
		User user = userRepo.getUserByEmail("tuanng007@gmail.com");
		System.out.println(user);
		assertThat(user).isNotNull();
	}
	
	@Test
	public void testCountById() { 
		Integer id = 1;
		Long countById = userRepo.countById(id);
		
		assertThat(countById).isNotNull().isGreaterThan(0);
	}
	
	@Test
	public void testChangeStatusUser() { 
		Integer id = 1;
		userRepo.updateEnabledStatus(id, false);
	}
	
	@Test
	public void testListFirstPage() { 
		int pageNumber = 0;
		int pageSize = 4;
		
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page = userRepo.findAll(pageable);
		List<User> listUsers = page.getContent();
		listUsers.forEach(user -> System.out.println(user));
		
		assertThat(listUsers.size()).isEqualTo(pageSize);
		
	}
 }
