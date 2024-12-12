package com.websales.admin.setting;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.websales.admin.util.GeneralSettingBag;
import com.websales.common.entity.Setting;
import com.websales.common.entity.SettingCategory;

@Service
public class SettingService implements ISettingService{

	@Autowired 
	private SettingRepository repo;
	
	@Override
	public List<Setting> listAllSettings() {
		// TODO Auto-generated method stub
		return (List<Setting>) repo.findAll();
	}

	@Override
	public GeneralSettingBag getGeneralSettings() {
		List<Setting> settings = new ArrayList<>();

		List<Setting> generalSettings = repo.findByCategory(SettingCategory.GENERAL);
		List<Setting> currencySettings = repo.findByCategory(SettingCategory.CURRENCY);

		settings.addAll(generalSettings);
		settings.addAll(currencySettings);

		return new GeneralSettingBag(settings);
	}

	@Override
	public void saveAll(Iterable<Setting> settings) {
		repo.saveAll(settings);
	}
	
	@Override
	public List<Setting> getMailServerSettings() {
		return repo.findByCategory(SettingCategory.MAIL_SERVER);
	}

	@Override
	public List<Setting> getMailTemplateSettings() {
		return repo.findByCategory(SettingCategory.MAIL_TEMPLATES);
	}

	@Override
	public List<Setting> getCurrencySettings() {
		return repo.findByCategory(SettingCategory.CURRENCY);
	}
	
	@Override
	public List<Setting> getPaymentSettings(){
		return repo.findByCategory(SettingCategory.PAYMENT);
	}
}
