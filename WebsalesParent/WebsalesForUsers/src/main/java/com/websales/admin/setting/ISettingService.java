package com.websales.admin.setting;

import java.util.List;

import com.websales.admin.util.GeneralSettingBag;
import com.websales.common.entity.Setting;

public interface ISettingService {

	public List<Setting> listAllSettings();
	
	public GeneralSettingBag getGeneralSettings();
	
	public void saveAll(Iterable<Setting> settings);
	
	public List<Setting> getMailServerSettings();
	
	public List<Setting> getMailTemplateSettings();

	public List<Setting> getCurrencySettings();

	public List<Setting> getPaymentSettings();
}
