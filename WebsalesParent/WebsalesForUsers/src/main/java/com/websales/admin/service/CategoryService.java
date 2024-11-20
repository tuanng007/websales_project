package com.websales.admin.service;

import java.util.List;

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
	
}
