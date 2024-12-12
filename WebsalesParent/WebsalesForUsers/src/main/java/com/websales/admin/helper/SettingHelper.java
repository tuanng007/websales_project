package com.websales.admin.helper;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.websales.admin.setting.CurrencyRepository;
import com.websales.admin.setting.SettingService;
import com.websales.admin.util.FileUploadUtil;
import com.websales.admin.util.GeneralSettingBag;
import com.websales.common.entity.Currency;
import com.websales.common.entity.Setting;

import jakarta.servlet.http.HttpServletRequest;

public class SettingHelper {

	public static void saveSiteLogo(MultipartFile multipartFile, GeneralSettingBag settingBag) throws IOException {
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			String value = "/site-logo/" + fileName;
			settingBag.updateSiteLogo(value);

			// Image Folder 
			String uploadDir = "../site-logo/";
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			
		}
	}

	public static void saveCurrencySymbol(HttpServletRequest request, GeneralSettingBag settingBag, CurrencyRepository currencyRepo) {
		Integer currencyId = Integer.parseInt(request.getParameter("CURRENCY_ID"));
		Optional<Currency> findByIdResult = currencyRepo.findById(currencyId);

		if (findByIdResult.isPresent()) {
			Currency currency = findByIdResult.get();
			settingBag.updateCurrencySymbol(currency.getSymbol());
		}
	}

	public static void updateSettingValuesFromForm(HttpServletRequest request, List<Setting> listSettings,SettingService service) {
		for (Setting setting : listSettings) {
			String value = request.getParameter(setting.getKey());
			if (value != null) {
				setting.setValue(value);
			}
		}

		service.saveAll(listSettings);
	}
}
