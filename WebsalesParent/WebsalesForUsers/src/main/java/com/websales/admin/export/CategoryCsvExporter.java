package com.websales.admin.export;

import java.io.IOException;
import java.util.List;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.websales.common.entity.Category;
import com.websales.common.entity.User;

import jakarta.servlet.http.HttpServletResponse;

public class CategoryCsvExporter extends AbstractExporter {
	public void export(List<Category> listCategories, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "text/csv", ".csv" , "_category");
		
		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
		
		String[] csvHeader = {"Category ID", "Name"};
		String[] fieldMapping = {"id", "name"};
		
		csvWriter.writeHeader(csvHeader);
		
		for (Category cate : listCategories) {
			csvWriter.write(cate, fieldMapping);
		}
		
		csvWriter.close();
	}
}
