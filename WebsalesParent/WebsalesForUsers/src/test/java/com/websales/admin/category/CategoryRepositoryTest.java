package com.websales.admin.category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.websales.admin.repository.CategoryRepository;
import com.websales.common.entity.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTest {
	
	@Autowired
	private CategoryRepository categoryRepo;
	 
	@Test
	public void testCreateRootCategory() { 
		Category category = new Category("Computers");
		Category savedCategory = categoryRepo.save(category);
		
		assertThat(savedCategory.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateSubCategory() { 
		Category parent = new Category(9);
		Category subCategory = new Category("Memory", parent);
		Category savedSubCategory = categoryRepo.save(subCategory);
		
		assertThat(savedSubCategory.getId()).isGreaterThan(0);
		
	}
	
	@Test
	public void testCreateMultiSubCategory() { 
		Category parent = new Category(1);
		Category desktops = new Category("Desktops", parent);
		Category camera = new Category("Camera", parent);
		
		categoryRepo.saveAll(List.of(desktops, camera));
	}
	
	@Test
	public void testGetCategory()  { 
		Category category = categoryRepo.findById(1).get();
		System.out.println(category.getName());
		
		Set<Category> children = category.getChildren();
		
		for(Category subCategory : children) { 
			System.out.println(subCategory.getName());
		}
		
		assertThat(children.size()).isGreaterThan(0);
	}
	
	
	@Test
	public void testPrintHierachicalCategories() { 
		Iterable<Category> categories = categoryRepo.findAll();
		
		for(Category category : categories) { 
			if(category.getParent() == null) { 
				System.out.println(category.getName());
			}
			
			Set<Category> children = category.getChildren();
			
			for(Category subCategory : children) { 
				System.out.println("--" + subCategory.getName());
				printChildren(subCategory, 1);
			}
		}
		
	}
	
	private void printChildren(Category parent, int sublevel) { 
		int newSubLevel = sublevel + 1;
		Set<Category> childen = parent.getChildren();
		
		for(Category subCategory : childen) { 
			for(int i = 0; i < newSubLevel; i++) { 
				System.out.print("--");
			}
			printChildren(subCategory, newSubLevel);

		}
				
	}
	
	
}
