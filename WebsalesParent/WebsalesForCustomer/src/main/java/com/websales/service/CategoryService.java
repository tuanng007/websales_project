package com.websales.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.websales.common.entity.Category;
import com.websales.common.exception.CategoryNotFoundException;
import com.websales.repository.CategoryRepository;
import com.websales.service.impl.ICategoryService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CategoryService implements ICategoryService{

	@Autowired 
	private CategoryRepository repo;
	
	public CategoryService(CategoryRepository repo) {
		super();
		this.repo = repo;
	}

	@Override
	public List<Category> listNoChildrenCategories() {
		
		List<Category> listNoChildrenCategories = new ArrayList<>();

		List<Category> listEnabledCategories = repo.findAllEnabled();

		listEnabledCategories.forEach(category -> {
			Set<Category> children = category.getChildren();
			if (children == null || children.size() == 0) {
				listNoChildrenCategories.add(category);
			}
		});

		return listNoChildrenCategories;
	}
	
	@Override
	public Category getCategory(String alias) throws CategoryNotFoundException {
		Category category = repo.findByAliasEnabled(alias);
		if (category == null) {
			throw new CategoryNotFoundException("Could not find any categories with alias " + alias);
		}

		return category;
	}

	@Override
	public List<Category> getCategoryParents(Category child) {
		List<Category> listParents = new ArrayList<>();

		Category parent = child.getParent();

		while (parent != null) {
			listParents.add(0, parent);
			parent = parent.getParent();
		}

		listParents.add(child);

		return listParents;
	}

}
