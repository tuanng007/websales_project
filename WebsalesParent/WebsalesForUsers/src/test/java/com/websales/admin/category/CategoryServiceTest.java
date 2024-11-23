package com.websales.admin.category;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.websales.admin.repository.CategoryRepository;
import com.websales.admin.service.CategoryService;
import com.websales.common.entity.Category;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class CategoryServiceTest {
	
	@MockBean
	private CategoryRepository cateRepo;
	
	@InjectMocks
	private CategoryService cateService;
	
	@Test
	public void testCheckUniqueAndReturnDuplicateName() { 
		Integer id = null;
		String name = "Computers";
		String alias = "abc";
		
		Category category = new Category(id, name, alias);
		Mockito.when(cateRepo.findByName(name)).thenReturn(category);
		Mockito.when(cateRepo.findByAlias(alias)).thenReturn(null);
		
		String result = cateService.checkUniqueCategory(id, name, alias);
		assertThat(result).isEqualTo("DupicateName"); 
	}
	
	@Test
	public void testCheckUniqueAndReturnDuplicateAlias() { 
		Integer id = null;
		String name = "Nothing";
		String alias = "Computers";
		
		Category category = new Category(id, name, alias);
		
		Mockito.when(cateRepo.findByName(name)).thenReturn(null);
		Mockito.when(cateRepo.findByAlias(alias)).thenReturn(category);
		
		String result = cateService.checkUniqueCategory(id, name, alias);
		assertThat(result).isEqualTo("DuplicateAlias");
	}
	
	@Test
	public void testCheckUniqueAndReturnOk() { 
		Integer id = 1;
		String name = "Nothing";
		String alias = "Abc";
		
		Category category = new Category(id, name, alias);
		
		Mockito.when(cateRepo.findByName(name)).thenReturn(null);
		Mockito.when(cateRepo.findByAlias(alias)).thenReturn(category);
		
		String result = cateService.checkUniqueCategory(id, name, alias);
		assertThat(result).isEqualTo("OK");
	}
}
