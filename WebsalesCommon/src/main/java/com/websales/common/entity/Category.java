package com.websales.common.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name="categories")
public class Category {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(length = 128, nullable = false, unique = true)
	private String name;
	
	@Column(length =  64, nullable = false, unique = true)
	private String alias;
	
	@Column(length  = 128, nullable =  false)
	private String images;
	
	private boolean enabled;
	
	
	@ManyToOne
	@JoinColumn(name = "parent_id")
	private Category parent;

	
	@OneToMany(mappedBy = "parent")
	private Set<Category> children = new HashSet<>();
	
	public Category() { }
	
	public Category(Integer id) { 
		this.id = id;
	}
	
	public static Category copyIDandName(Category category) { 
		Category copyCategory = new Category();
		copyCategory.setId(category.getId());
		copyCategory.setName(category.getName());
		
		return copyCategory;
	}
	
	public static Category copyIDandName(Integer id, String name) { 
		Category copyCategory = new Category();
		copyCategory.setId(id);
		copyCategory.setName(name);
		
		return copyCategory;
	}
	
	public static Category copyAll(Category category) { 
		Category copyCategory = new Category();
		copyCategory.setId(category.getId());
		copyCategory.setName(category.getName());
		copyCategory.setImages(category.getImages());
		copyCategory.setAlias(category.getAlias());	
		copyCategory.setEnabled(category.isEnabled());
		copyCategory.setHasChildren(category.getChildren().size() > 0);
		return copyCategory;
	}
	
	public static Category copyAll(Category category, String name) { 
		Category copyCategory = Category.copyAll(category);
		copyCategory.setName(name);
		return copyCategory;
	} 
	
	public Category(String name) { 
		this.name = name;
		this.alias = name;
		this.images = "default.png";
	}
	
	public Category(String name, Category parent) { 
		this(name);
		this.parent = parent;
	}

	
	
	public Category(Integer id, String name, String alias) {
		this.id = id;
		this.name = name;
		this.alias = alias;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getImages() {
		return images;
	}

	public void setImages(String images) {
		this.images = images;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Category getParent() {
		return parent;
	}

	public void setParent(Category parent) {
		this.parent = parent;
	}

	public Set<Category> getChildren() {
		return children;
	}

	public void setChildren(Set<Category> children) {
		this.children = children;
	}
	
	
	
	@Override
	public String toString() {
		return this.name;
	}

	@Transient
	public String getImagePath() { 
		return "/category-images/" + this.id + "/" + this.images;
	}
	
	@Transient
	private boolean hasChildren;
	
	public boolean isHasChildren() { 
		return hasChildren;
	}
	
	public void setHasChildren(boolean hasChildren) { 
		this.hasChildren = hasChildren;
	}
	
	
}
