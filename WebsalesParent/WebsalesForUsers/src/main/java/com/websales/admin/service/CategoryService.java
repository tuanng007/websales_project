package com.websales.admin.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.websales.admin.exception.CategoryNotFoundException;
import com.websales.admin.repository.CategoryRepository;
import com.websales.common.entity.Category;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CategoryService {
	
	public static final int CATEGORIES_PER_PAGE = 4;
	
	@Autowired
	private CategoryRepository categoryRepo;
	
	public List<Category> listByPage(CategoryPageInfo pageInfo,int pageNum, String sortDir, String keyword) {
		Sort sort = Sort.by("name");
		
		if(sortDir.equals("asc")) { 
			sort = sort.ascending();
		} else if(sortDir.equals("desc")) {
			sort = sort.descending();
		}
		
		Pageable pageable = PageRequest.of(pageNum - 1, CATEGORIES_PER_PAGE, sort);
		
		Page<Category> pageCategory = null;
		
		if(keyword != null)  {
			pageCategory = categoryRepo.search(keyword, pageable);
			
		} else { 
			pageCategory = categoryRepo.findRootCategories(pageable);
		}
		
		List<Category> listRootCategories = pageCategory.getContent();
		
		pageInfo.setTotalPages(pageCategory.getTotalPages());
		pageInfo.setTotalElements(pageCategory.getTotalElements());
		
		if(keyword != null) { 
			List<Category> searchCategory = pageCategory.getContent();
			for(Category category : searchCategory) { 
				category.setHasChildren(category.getChildren().size() > 0);
			}
			
			return searchCategory;
		} else {
			return listHierarchicalCategories(listRootCategories, sortDir);
		}
	}
	
	public List<Category> listAll(String sortDir){
		Sort sort = Sort.by("name");
		
		if(sortDir.equals("asc")) { 
			sort = sort.ascending();
		} else if(sortDir.equals("desc")) { 
			sort = sort.descending();
		}
		
		List<Category> listRootCategories = categoryRepo.findRootCategories(sort);
		
		return listHierarchicalCategories(listRootCategories, sortDir);
	}
	
	private List<Category> listHierarchicalCategories(List<Category> listRootCategories, String sortDir) {
		List<Category> hierarchicalCategories = new ArrayList<>();
		
		for(Category rootCategory : listRootCategories) {
			hierarchicalCategories.add(Category.copyAll(rootCategory));
			
			Set<Category> children = sortedSubCategories(rootCategory.getChildren(), sortDir);
			
			for(Category subCategory : children) { 
				String name = "--" + subCategory.getName();
				hierarchicalCategories.add(Category.copyAll(subCategory, name));
				
				listSubHierarchicalCategories(hierarchicalCategories, subCategory, 1, sortDir);
			}
		}
		
		return hierarchicalCategories;
	}

	
	private void listSubHierarchicalCategories(List<Category> hierarchicalCategories, 
			Category parent, int subLevel, String sortDir) {
		int newSubLevel = subLevel + 1;
		Set<Category> children = sortedSubCategories(parent.getChildren(), sortDir);
		
		for(Category subCategory : children) {
			String name = "";
			for(int i = 0; i < newSubLevel; i++) {
				name += "--";
			} 
			name += subCategory.getName();
			
			hierarchicalCategories.add(Category.copyAll(subCategory, name));
			
			listSubHierarchicalCategories(hierarchicalCategories, subCategory, newSubLevel, sortDir);
		
		}
	}
	
	public List<Category> listCategoriesInForm() { 
		List<Category> categoriesInForm = new ArrayList<>();
		
		Iterable<Category> categoriesInDB = categoryRepo.findRootCategories(Sort.by("name").ascending());
		
		for(Category category : categoriesInDB) {
			if(category.getParent() == null) { 
				categoriesInForm.add(Category.copyIDandName(category));

			
			Set<Category> children = sortedSubCategories(category.getChildren());
						
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
		Set<Category> children = sortedSubCategories(parent.getChildren());
		
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
	
	private SortedSet<Category> sortedSubCategories(Set<Category> children) { 
		return sortedSubCategories(children,"asc");
	}
	
	private SortedSet<Category> sortedSubCategories(Set<Category> children, String sortDir) {
		SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {

			@Override
			public int compare(Category o1, Category o2) {
				if("asc".equals(sortDir)) {
					return o1.getName().compareTo(o2.getName());
			} else  { 
				return o2.getName().compareTo(o1.getName());
				}
			}
		}	
		);
		
		sortedChildren.addAll(children);
		
		return sortedChildren; 
		
	}
	
	public void updateCategoryEnabledStatus(Integer id, boolean status) { 
		categoryRepo.updateEnabledStatus(id, status);
	}
	
	public void delete(Integer id) throws CategoryNotFoundException { 
		Long categoryById = categoryRepo.countById(id);
		if(categoryById == null || categoryById == 0) { 
			throw new CategoryNotFoundException("Could not find any category ID: " + categoryById);
		}
		else  {
		categoryRepo.deleteById(id);
		}
	}
	
	
	public Category save(Category category) {
		return categoryRepo.save(category);
	}
	
	
}
