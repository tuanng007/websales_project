package com.websales.service.impl;

import java.util.List;


import com.websales.common.entity.EmailSettingBag;
import com.websales.common.entity.Setting;
import com.websales.setting.CurrencySettingBag;
import com.websales.setting.PaymentSettingBag;

public interface ISettingService {

	public List<Setting> getGeneralSettings();
	public EmailSettingBag getEmailSettings();
	public CurrencySettingBag getCurrencySettings();
	public String getCurrencyCode();
	public PaymentSettingBag getPaymentSettings();
}
