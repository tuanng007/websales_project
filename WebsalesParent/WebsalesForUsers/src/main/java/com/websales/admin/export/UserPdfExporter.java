package com.websales.admin.export;

import java.io.IOException;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.CMYKColor;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.websales.common.entity.User;

import jakarta.servlet.http.HttpServletResponse;

public class UserPdfExporter extends AbstractExporter{
	
	public void export(List<User> listUsers, HttpServletResponse response) throws IOException { 
		super.setResponseHeader(response,"application/pdf" , ".pdf");
		
		Document document = new Document(PageSize.A4);
		
		PdfWriter.getInstance(document, response.getOutputStream());
		
		document.open();
		
		Font fontTitle = FontFactory.getFont(FontFactory.TIMES_ROMAN);
		fontTitle.setSize(20);
		
		Paragraph paragraph1 = new Paragraph("List of the Users", fontTitle);
		
		paragraph1.setAlignment(Paragraph.ALIGN_CENTER);
		
		document.add(paragraph1);
		
		PdfPTable table = new PdfPTable(6);
		
	    table.setWidthPercentage(100f);
	    table.setWidths(new int[] {3,3,3,3,3,3});
	    table.setSpacingBefore(5);
	    
	    PdfPCell cell = new PdfPCell();
	    
	    cell.setBackgroundColor(CMYKColor.BLUE);
	    cell.setPadding(5);
	    
	    Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN);
	    font.setColor(CMYKColor.WHITE);
	    
	    cell.setPhrase(new Phrase("ID", font));
	    table.addCell(cell);
	    cell.setPhrase(new Phrase("Email", font));
	    table.addCell(cell);
	    cell.setPhrase(new Phrase("First name", font));
	    table.addCell(cell);
	    cell.setPhrase(new Phrase("Last name", font));
	    table.addCell(cell);
	    cell.setPhrase(new Phrase("Roles", font));
	    table.addCell(cell);
	    cell.setPhrase(new Phrase("Enabled", font));
	    table.addCell(cell);
	    
	    
	    for(User user : listUsers) { 
	    	table.addCell(String.valueOf(user.getId()));
	    	table.addCell(user.getEmail());
	    	table.addCell(user.getFirstName());
	    	table.addCell(user.getLastName());
	    	table.addCell(String.valueOf(user.getRoles()));
	    	table.addCell(String.valueOf(user.isEnabled()));
	    }
	    
	    document.add(table);
	    
	    document.close();
	} 
}
