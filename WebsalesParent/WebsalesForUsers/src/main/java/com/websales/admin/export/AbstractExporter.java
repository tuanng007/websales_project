package com.websales.admin.export;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.servlet.http.HttpServletResponse;

public class AbstractExporter {
	
	public void setResponseHeader(HttpServletResponse respone, String contentType, String extension, String prefix) throws IOException{ 
		DateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd_HH-mm-ss");
		String timestamp = dateFormat.format(new Date());
		String fileName = prefix + timestamp + extension;
		
		respone.setContentType(contentType);
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=" + fileName;
		respone.setHeader(headerKey, headerValue);
	}
}
