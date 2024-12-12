package com.websales.admin.setting;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.websales.common.entity.Setting;
import com.websales.common.entity.SettingCategory;

public interface SettingRepository extends CrudRepository<Setting, String>{
	
	public List<Setting> findByCategory(SettingCategory category);
}
