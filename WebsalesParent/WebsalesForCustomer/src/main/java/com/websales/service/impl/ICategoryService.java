package com.websales.service.impl;

import java.util.List;

import com.websales.common.entity.Category;
import com.websales.common.exception.CategoryNotFoundException;

public interface ICategoryService {

	public List<Category> listNoChildrenCategories();
	
	public Category getCategory(String alias) throws CategoryNotFoundException;
	
	public List<Category> getCategoryParents(Category child);
}
