package com.websales.admin.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.poi.poifs.property.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.websales.admin.repository.CategoryRepository;
import com.websales.common.entity.Category;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository categoryRepo;
	
	public List<Category> listAll(){ 
		return  (List<Category>) categoryRepo.findAll();
	}
	
	public List<Category> listCategoriesInForm() { 
		List<Category> categoriesInForm = new ArrayList<>();
		
		Iterable<Category> categoriesInDB = categoryRepo.findAll();
		
		for(Category category : categoriesInDB) {
			if(category.getParent() == null) { 
				categoriesInForm.add(new Category(category.getName()));

			
			Set<Category> children = category.getChildren();
						
			for(Category subCategory : children) { 
				String name = "--" + subCategory.getName();
				categoriesInForm.add(new Category(name));
				
				listChildren(categoriesInForm, subCategory, 1);
				}
			}
		}
		
		return categoriesInForm;
		
	}
	
	
	private void listChildren(List<Category> categoriesInForm , Category parent, int sublevel) { 
		int newSubLevel = sublevel + 1;
		Set<Category> children = parent.getChildren();
		
		for(Category subCategory : children) {
			String name = "";
			for(int i = 0; i < newSubLevel; i++) {
				name += "--";
			} 
			name += subCategory.getName();
			
			categoriesInForm.add(new Category(name));
			
			listChildren(categoriesInForm, subCategory, newSubLevel);
		} 
	}
	
	
	
}
