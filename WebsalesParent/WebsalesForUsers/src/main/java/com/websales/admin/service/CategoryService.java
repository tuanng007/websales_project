package com.websales.admin.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.poi.poifs.property.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.websales.admin.exception.CategoryNotFoundException;
import com.websales.admin.repository.CategoryRepository;
import com.websales.common.entity.Category;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository categoryRepo;
	
	public List<Category> listAll(){ 
		List<Category> listRootCategories = categoryRepo.findRootCategories();
		
		return listHierarchicalCategories(listRootCategories);
	}
	
	private List<Category> listHierarchicalCategories(List<Category> listRootCategories) {
		List<Category> hierarchicalCategories = new ArrayList<>();
		
		for(Category rootCategory : listRootCategories) {
			hierarchicalCategories.add(Category.copyAll(rootCategory));
			
			Set<Category> children = rootCategory.getChildren();
			
			for(Category subCategory : children) { 
				String name = "--" + subCategory.getName();
				hierarchicalCategories.add(Category.copyAll(subCategory, name));
				
				listSubHierarchicalCategories(hierarchicalCategories, subCategory, 1);
			}
		}
		
		return hierarchicalCategories;
	}

	
	private void listSubHierarchicalCategories(List<Category> hierarchicalCategories, Category parent, int subLevel) {
		int newSubLevel = subLevel + 1;
		Set<Category> children = parent.getChildren();
		
		for(Category subCategory : children) {
			String name = "";
			for(int i = 0; i < newSubLevel; i++) {
				name += "--";
			} 
			name += subCategory.getName();
			
			hierarchicalCategories.add(Category.copyAll(subCategory, name));
			
			listSubCategoriesUsedInForm(hierarchicalCategories, subCategory, newSubLevel);
		
		}
	}
	
	public List<Category> listCategoriesInForm() { 
		List<Category> categoriesInForm = new ArrayList<>();
		
		Iterable<Category> categoriesInDB = categoryRepo.findAll();
		
		for(Category category : categoriesInDB) {
			if(category.getParent() == null) { 
				categoriesInForm.add(Category.copyIDandName(category));

			
			Set<Category> children = category.getChildren();
						
			for(Category subCategory : children) { 
				String name = "--" + subCategory.getName();
				categoriesInForm.add(Category.copyIDandName(subCategory.getId(), name));
				
				listSubCategoriesUsedInForm(categoriesInForm, subCategory, 1);
				}
			}
		}
		
		return categoriesInForm;
		
	}
	
	
	private void listSubCategoriesUsedInForm(List<Category> categoriesInForm , Category parent, int sublevel) { 
		int newSubLevel = sublevel + 1;
		Set<Category> children = parent.getChildren();
		
		for(Category subCategory : children) {
			String name = "";
			for(int i = 0; i < newSubLevel; i++) {
				name += "--";
			} 
			name += subCategory.getName();
			
			categoriesInForm.add(Category.copyIDandName(subCategory.getId(), name));
			
			listSubCategoriesUsedInForm(categoriesInForm, subCategory, newSubLevel);
		} 
	}
	
	public Category get(Integer id) throws CategoryNotFoundException { 
		try { 
			return categoryRepo.findById(id).get();
		}catch (NoSuchElementException e) {
			throw new CategoryNotFoundException("Could not find any categories id: " + id);
		}
	}
	
	public String checkUniqueCategory(Integer id, String name, String alias)  { 
		boolean isCreatingNew = (id == null || id == 0);
		
		Category categoryByName = categoryRepo.findByName(name);
		
		if(isCreatingNew) { 
			if(categoryByName != null) { 
				return "DuplicateName";
			} else { 
				Category categoryByAlias = categoryRepo.findByAlias(alias);
				if(categoryByAlias != null) { 
					return "DuplicateAlias";
				}
			}
		} else { 
			if(categoryByName != null && categoryByName.getId() != id) { 
				return "DuplicateName";
			}
			
			Category categoryByAlias = categoryRepo.findByAlias(alias);
			if(categoryByAlias != null && categoryByAlias.getId() != id)  { 
				return "DuplicateAlias";
			}
		}
		
		return "OK";
	}
	
	public Category save(Category category) {
		return categoryRepo.save(category);
	}
	
}
