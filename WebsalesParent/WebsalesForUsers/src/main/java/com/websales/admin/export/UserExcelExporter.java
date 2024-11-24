package com.websales.admin.export;

import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.websales.common.entity.User;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

public class UserExcelExporter extends AbstractExporter {
	
	private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    
    
    public UserExcelExporter() { 
    	workbook = new XSSFWorkbook();
    }
    
	public void export(List<User> listUsers,HttpServletResponse respone) throws IOException { 
		super.setResponseHeader(respone, "application/octet-stream", ".xlsx", "_user");
		
		writeHeader();
		write(listUsers);
		
		ServletOutputStream outputStream = respone.getOutputStream();
		workbook.write(outputStream);
		workbook.close();
		outputStream.close();
	
	}


	private void writeHeader() {
		sheet = workbook.createSheet("User");
		Row row = sheet.createRow(0);
		CellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();	
		font.setBold(true);
		font.setFontHeight(16);
		style.setFont(font);
		createCell(row, 0, "ID", style);
		createCell(row, 1, "Email", style);
		createCell(row, 2, "First name", style);
		createCell(row, 3, "Last name", style);
		createCell(row, 4, "Roles", style);
		createCell(row, 5, "Enabled", style);
	}
	
	private void createCell(Row row, int columnCount, Object valueOfCell, CellStyle style ) {
		sheet.autoSizeColumn(columnCount);
		Cell cell = row.createCell(columnCount);
		if(valueOfCell instanceof Integer)  { 
			cell.setCellValue((Integer) valueOfCell);
		} else if(valueOfCell instanceof Long) { 
			cell.setCellValue((Long) valueOfCell);
		} else if(valueOfCell instanceof String) { 
			cell.setCellValue((String) valueOfCell);
		} else {
			cell.setCellValue((Boolean) valueOfCell);
		}
		
		cell.setCellStyle(style);
	}


	private void write(List<User> listUsers) {
			int rowCount = 1;
			CellStyle style = workbook.createCellStyle();
			XSSFFont font = workbook.createFont();
			font.setFontHeight(14);
			style.setFont(font);
			for (User user : listUsers) { 
				Row row = sheet.createRow(rowCount++);
				int columnCount = 0;
				createCell(row, columnCount++, user.getId(), style);
				createCell(row, columnCount++, user.getEmail(), style);
				createCell(row, columnCount++, user.getFirstName(), style);
				createCell(row, columnCount++, user.getLastName(), style);
				createCell(row, columnCount++, user.getRoles().toString(), style);
				createCell(row, columnCount++, user.isEnabled(), style);
			}
	}

}
