package com.websales.util;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

   
import com.websales.common.entity.EmailSettingBag;
import com.websales.common.entity.Order;
import com.websales.service.SettingService;
import com.websales.setting.CurrencySettingBag;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

public class OrderUtil {
	

	public static void sendOrderConfirmationEmail(HttpServletRequest request, Order order, SettingService settingService) 
			throws UnsupportedEncodingException, MessagingException {
		
		
		EmailSettingBag emailSettings = settingService.getEmailSettings();
		JavaMailSenderImpl mailSender = CustomerRegisterUtil.prepareMailSender(emailSettings);
		mailSender.setDefaultEncoding("utf-8");

		String toAddress = order.getCustomer().getEmail();
		String subject = emailSettings.getOrderConfirmationSubject();
		String content = emailSettings.getOrderConfirmationContent();
	

		subject = subject.replace("[[orderId]]", String.valueOf(order.getId()));
		

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

		helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
		helper.setTo(toAddress);
		helper.setSubject(subject);

		DateFormat dateFormatter =  new SimpleDateFormat("HH:mm:ss E, dd MMM yyyy");
		String orderTime = dateFormatter.format(order.getOrderTime());

		CurrencySettingBag currencySettings = settingService.getCurrencySettings();
		String totalAmount = FormatCurrencyUtil.formatCurrency(order.getTotal(), currencySettings);

		content = content.replace("[[name]]", order.getCustomer().getFullName());
		content = content.replace("[[orderId]]", String.valueOf(order.getId()));
		content = content.replace("[[orderTime]]", orderTime);
		content = content.replace("[[shippingAddress]]", order.getShippingAddress());
		content = content.replace("[[total]]", totalAmount);
		content = content.replace("[[paymentMethod]]", order.getPaymentMethod().toString());
		
 
		helper.setText(content, true);
		mailSender.send(message);		
	}
}
