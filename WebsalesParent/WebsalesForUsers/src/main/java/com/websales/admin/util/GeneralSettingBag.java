package com.websales.admin.util;

import java.util.List;

import com.websales.common.entity.Setting;
import com.websales.common.entity.SettingBag;

public class GeneralSettingBag extends SettingBag {

	public GeneralSettingBag(List<Setting> listSettings) {
		super(listSettings);
	}

	public void updateCurrencySymbol(String value) {
		super.update("CURRENCY_SYMBOL", value);
	}

	public void updateSiteLogo(String value) {
		super.update("SITE_LOGO", value);
	}
}