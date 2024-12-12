package com.websales.service;

import java.util.List;

 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

  
import com.websales.common.entity.Currency;
import com.websales.common.entity.EmailSettingBag;
import com.websales.common.entity.Setting;
import com.websales.common.entity.SettingCategory;
import com.websales.repository.CurrencyRepository;
import com.websales.repository.SettingRepository;
import com.websales.service.impl.ISettingService;
import com.websales.setting.CurrencySettingBag;
import com.websales.setting.PaymentSettingBag;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class SettingService implements ISettingService{
	
	@Autowired
	private SettingRepository settingRepo;
	@Autowired
	private CurrencyRepository currencyRepo;
	


	@Override
	public List<Setting> getGeneralSettings() {
		// TODO Auto-generated method stub
		return settingRepo.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
	}
	
	@Override
	public EmailSettingBag getEmailSettings() {
		List<Setting> settings = settingRepo.findByCategory(SettingCategory.MAIL_SERVER);
		settings.addAll(settingRepo.findByCategory(SettingCategory.MAIL_TEMPLATES));

		return new EmailSettingBag(settings);
	}
	
	@Override
	public CurrencySettingBag getCurrencySettings() {
		List<Setting> settings = settingRepo.findByCategory(SettingCategory.CURRENCY);
		return new CurrencySettingBag(settings);
	}
	
	@Override
	public PaymentSettingBag getPaymentSettings() {
		List<Setting> settings = settingRepo.findByCategory(SettingCategory.PAYMENT);
		return new PaymentSettingBag(settings);
	}

	@Override
	public String getCurrencyCode() {
		Setting setting = settingRepo.findByKey("CURRENCY_ID");
		Integer currencyId = Integer.parseInt(setting.getValue());
		Currency currency = currencyRepo.findById(currencyId).get();

		return currency.getCode();
	}

}
