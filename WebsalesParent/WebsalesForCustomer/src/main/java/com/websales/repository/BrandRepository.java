package com.websales.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.websales.common.entity.Brand;

public interface BrandRepository extends JpaRepository<Brand, Integer> {

}
