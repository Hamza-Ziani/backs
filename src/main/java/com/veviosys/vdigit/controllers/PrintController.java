package com.veviosys.vdigit.controllers;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.veviosys.vdigit.classes.DocumentReport;
import com.veviosys.vdigit.classes.FolderClass;
import com.veviosys.vdigit.classes.KeyValue;
import com.veviosys.vdigit.models.Document;
import com.veviosys.vdigit.models.DocumentAttributeValue;
import com.veviosys.vdigit.models.DocumentFolder;
import com.veviosys.vdigit.models.DocumentsHeadAndFooter;
import com.veviosys.vdigit.models.Folder;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.repositories.DocumentAttributeValueRepo;
import com.veviosys.vdigit.repositories.DocumentHeadFootRepository;
import com.veviosys.vdigit.repositories.DocumentRepo;
import com.veviosys.vdigit.repositories.FolderRepo;
import com.veviosys.vdigit.services.MasterConfigService;
import com.veviosys.vdigit.services.PrintService;
import com.veviosys.vdigit.services.ReportsService;
import com.veviosys.vdigit.services.userService;

import ar.com.fdvs.dj.domain.builders.ColumnBuilderException;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

@RestController
@CrossOrigin(origins = "*")

@RequestMapping("api/v1/print")
public class PrintController {

	@Autowired
	userService us;
	@Autowired
	FolderRepo fr;
	@Autowired
	DocumentHeadFootRepository dhfr;
	@Autowired
	DocumentRepo dr;
	@Autowired
	DocumentAttributeValueRepo davr;
	public String root = System.getProperty("user.dir") + "/src/main/resources/Document reporting";
	@Autowired
	MasterConfigService masterConfigService;

	// @PostMapping()
	// public ResponseEntity<HashMap<String, String>> generatePdfFile(@RequestBody String htmlContent) {
	// 	HashMap<String, String> mapper = new HashMap<String, String>();
	// 	ByteArrayOutputStream baos = new ByteArrayOutputStream();

	// 	try {
	// 		HtmlConverter.convertToPdf(htmlContent, baos, null);
	// 		//.println(baos.size() + " <<");
	// 		String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());
	// 		mapper.put("base64", base64);

	// 		return ResponseEntity.ok(mapper);
	// 	} catch (IOException e) {
	// 		// TODO Auto-generated catch block
	// 		e.printStackTrace();
	// 	}
	// 	return null;
	// }
	  
	
	
	@Autowired 
	    ReportsService reportsService;
	    @Autowired
	    MasterConfigService configService;
	@GetMapping("/{id}/{save}")
	public ResponseEntity<HashMap<String, String>> generatePdfFileBO(@PathVariable UUID id, @PathVariable int save)
			throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InstantiationException, ColumnBuilderException, ClassNotFoundException, JRException {
	
		try {
			List<DocumentReport> l = new ArrayList<DocumentReport>();
	        List<String> attributes = new ArrayList<String>();
	        attributes.add("DateCourrier");
	        attributes.add("Destinataire");
	        attributes.add("DateEnregistrement");
	        attributes.add("Expediteur");
	        attributes.add("Nature_Document");
	        attributes.add("Nom_Dossier");
	        attributes.add("Num_Courrier");
	        
	        HashMap<String,String> parameters = new HashMap<>(); 
	        int index=1;
			HashMap<String, String> mapper = new HashMap<String, String>();
			Folder f = fr.findById(id).orElse(null);
			List<DocumentFolder> docsF = f.getDocuments();
			List<String>headers=new ArrayList<>();
	      	for (int i = 0; i < docsF.size(); i++) {
	      		DocumentFolder documentFolder=docsF.get(i);
	      		DocumentReport documentReport=new DocumentReport(); 
	      		Class<?> clazz = DocumentReport.class;
	      		Object cc = clazz.newInstance();
	      		Document d=documentFolder.getDocument();
				Field f0 = cc.getClass().getDeclaredField("type");
	      		f0.setAccessible(true);
	      		f0.set(cc,d.getType().getLibelle());
	      		Field f2 = cc.getClass().getDeclaredField("doc");
	      		f2.setAccessible(true);
	      		f2.set(cc, d.getFileName());
	      		for (int j = 0; j < documentFolder.getDocument().getAttributeValues().size(); j++) { 
				DocumentAttributeValue attr=documentFolder.getDocument().getAttributeValues().get(j);
	            
				
				
				documentReport.setType(f.getTypeName());
				documentReport.setDoc(documentFolder.getDocument().getFileName());
				for (String attribute : attributes) {

				 if(headers.indexOf(attr.getAttribute().getName())==-1 && !attr.getAttribute().getName().equals("Fichier") && !attribute.equals(attr.getAttribute().getName())) {
					headers.add(attr.getAttribute().getName());
					
					parameters.put("attr"+index, attr.getAttribute().getLabelfr());
					 
					index+=1;
					
				 } 
				}
				if(!attr.getAttribute().getName().equals("Fichier")) {
					  for (Entry<String,String> entry : parameters.entrySet()) {
					        if (entry.getValue().equals(attr.getAttribute().getLabelfr())) {
					        	if(attr.getAttribute().getLabelfr().equals("List") || attr.getAttribute().getLabelfr().equals("ListDep") || attr.getAttribute().getLabelfr().equals("listDb")) {
		                             
					        	    java.lang.reflect.Type listType = new com.google.common.reflect.TypeToken<ArrayList<KeyValue>>() {
					                }.getType();
					                List<KeyValue> convertedObject = new Gson().fromJson(attr.getAttribute().getDefaultValue(), listType);
					                
					                for (KeyValue KeyVal : convertedObject) {
                                         if(KeyVal.getKey().equals(attr.getValue().getValue())) {
                                             Field f1 = cc.getClass().getDeclaredField(entry.getKey());
                                             f1.setAccessible(true);
                                             f1.set(cc, KeyVal.getValue());  
                                         }
                                    }
		                                
					        	}else {
					        	    Field f1 = cc.getClass().getDeclaredField(entry.getKey());
                                    f1.setAccessible(true);
                                    f1.set(cc, attr.getValue().getValue());  
					        	}

					        }
					    }
				      		
				}
				}
	      		 l.add((DocumentReport)cc);
			} 
	    	 
	         String path =  "";
	         String activePath = masterConfigService.findActivePath();
	         String pathFolder = path=activePath+"\\reports\\";
	         
	         final File saveInRootPath1 = new File(pathFolder);
             if (!saveInRootPath1.exists()) {
                 saveInRootPath1.mkdirs();
             }
             
	         path = pathFolder  +Instant.now().toEpochMilli() +"--bo.pdf";
	         
	         
	         JasperPrint print =   reportsService.getReport(l,parameters,f); 
	         JasperExportManager.exportReportToPdfFile(print, path); 
		 
	     	Date date1 = new Date();
			LocalDate localDate = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				String pathSave = activePath + "\\upload\\bordereau" + "\\"
						+ Base64.getEncoder().encodeToString((String.valueOf(localDate.getYear())).getBytes()) + "\\"
						+ Base64.getEncoder().encodeToString((String.valueOf(localDate.getMonth())).getBytes()) + "\\"
						+ Base64.getEncoder().encodeToString((String.valueOf(localDate.getDayOfMonth())).getBytes())
						+ "\\";
				final File saveInRootPath = new File(pathSave);
				if (!saveInRootPath.exists()) {
					saveInRootPath.mkdirs();
				}
				Paths.get(saveInRootPath.toString());
				String saveP = pathSave + f.getId() + "_BO.documania";
				File file=new File(path);
				byte[] fileContent = Files.readAllBytes(file.toPath());
	     	String base64 = Base64.getEncoder().encodeToString(fileContent);
	     	File doc = new File(saveP);
			doc.createNewFile();
			f.setHasBo(1);
			f.setPathBo(saveP);
			FileWriter myWriter = new FileWriter(saveP);
			myWriter.write(base64);
			myWriter.close();
			
			fr.save(f);
			file.delete();
			mapper.put("base64", base64);
			return ResponseEntity.ok(mapper);
		} catch (Exception e) {

			e.printStackTrace();
		}
		return null;
	}

	@PostMapping("/bo")
	
	public ResponseEntity<HashMap<String, String>>	getBoB64(@RequestBody String path) throws IOException
	{
		String base64="";
		HashMap<String, String> mapper = new HashMap<String, String>();
		
		BufferedReader br = new BufferedReader(new FileReader(path));
		StringBuilder sb = new StringBuilder();
		String line = br.readLine();

		while (line != null) {
			sb.append(line);
			sb.append(System.lineSeparator());
			line = br.readLine();
		}
		base64 = sb.toString();
		br.close();
		mapper.put("base64", base64);
		return ResponseEntity.ok(mapper);
	}	
	private String constructPDF(String html)
	{
		
		String baseDoc = "<!DOCTYPE html>\r\n" + 
				"<html lang=\"en\">\r\n" + 
				"<head>\r\n" + 
				"    <meta charset=\"UTF-8\">\r\n" + 
				"    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\r\n" + 
				"    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n" + 
				"    <style>\r\n" + 
				".ql-editor{font-family: Helvetica, Arial, sans-serif;\r\n" + 
				"    font-size: 13px;}"+
				".ql-editor > * {\r\n" + 
				"  cursor: text;\r\n" + 
				"}\r\n" + 
				"li { display: list-item!important }\\r\\n"+
				".ql-editor p,\r\n" + 
				".ql-editor ol,\r\n" + 
				".ql-editor ul,\r\n" + 
				".ql-editor pre,\r\n" + 
				".ql-editor blockquote,\r\n" + 
				".ql-editor h1,\r\n" + 
				".ql-editor h2,\r\n" + 
				".ql-editor h3,\r\n" + 
				".ql-editor h4,\r\n" + 
				".ql-editor h5,\r\n" + 
				".ql-editor h6 {\r\n" + 
				"  margin: 0;\r\n" + 
				"  padding: 0;\r\n" + 
				"  counter-reset: list-1 list-2 list-3 list-4 list-5 list-6 list-7 list-8 list-9;\r\n" + 
				"}\r\n" + 
				".ql-editor ol,\r\n" + 
				".ql-editor ul {\r\n" + 
				"  padding-left: 1.5em;\r\n" + 
				"}\r\n" + 
				".ql-editor ol > li,\r\n" + 
				".ql-editor ul > li {\r\n" + 
				"  list-style-type: none;\r\n" + 
				"}\r\n" + 
				".ql-editor ul > li::before {\r\n" + 
				"  content: '\\2022';\r\n" + 
				"}\r\n" + 
				".ql-editor ul[data-checked=true],\r\n" + 
				".ql-editor ul[data-checked=false] {\r\n" + 
				"  pointer-events: none;\r\n" + 
				"}\r\n" + 
				".ql-editor ul[data-checked=true] > li *,\r\n" + 
				".ql-editor ul[data-checked=false] > li * {\r\n" + 
				"  pointer-events: all;\r\n" + 
				"}\r\n" + 
				".ql-editor ul[data-checked=true] > li::before,\r\n" + 
				".ql-editor ul[data-checked=false] > li::before {\r\n" + 
				"  color: #777;\r\n" + 
				"  cursor: pointer;\r\n" + 
				"  pointer-events: all;\r\n" + 
				"}\r\n" + 
				".ql-editor ul[data-checked=true] > li::before {\r\n" + 
				"  content: '\\2611';\r\n" + 
				"}\r\n" + 
				".ql-editor ul[data-checked=false] > li::before {\r\n" + 
				"  content: '\\2610';\r\n" + 
				"}\r\n" + 
				".ql-editor li::before {\r\n" + 
				"  display: inline-block;\r\n" + 
				"  white-space: nowrap;\r\n" + 
				"  width: 1.2em;\r\n" + 
				"}\r\n" + 
				".ql-editor li:not(.ql-direction-rtl)::before {\r\n" + 
				"  margin-left: -1.5em;\r\n" + 
				"  margin-right: 0.3em;\r\n" + 
				"  text-align: right;\r\n" + 
				"}\r\n" + 
				".ql-editor li.ql-direction-rtl::before {\r\n" + 
				"  margin-left: 0.3em;\r\n" + 
				"  margin-right: -1.5em;\r\n" + 
				"}\r\n" + 
				".ql-editor ol li:not(.ql-direction-rtl),\r\n" + 
				".ql-editor ul li:not(.ql-direction-rtl) {\r\n" + 
				"  padding-left: 1.5em;\r\n" + 
				"}\r\n" + 
				".ql-editor ol li.ql-direction-rtl,\r\n" + 
				".ql-editor ul li.ql-direction-rtl {\r\n" + 
				"  padding-right: 1.5em;\r\n" + 
				"}\r\n" + 
				".ql-editor ol li {\r\n" + 
				"  counter-reset: list-1 list-2 list-3 list-4 list-5 list-6 list-7 list-8 list-9;\r\n" + 
				"  counter-increment: list-0;\r\n" + 
				"}\r\n" + 
				".ql-editor ol li:before {\r\n" + 
				"  content: counter(list-0, decimal) '. ';\r\n" + 
				"}\r\n" + 
				".ql-editor ol li.ql-indent-1 {\r\n" + 
				"  counter-increment: list-1;\r\n" + 
				"}\r\n" + 
				".ql-editor ol li.ql-indent-1:before {\r\n" + 
				"  content: counter(list-1, lower-alpha) '. ';\r\n" + 
				"}\r\n" + 
				".ql-editor ol li.ql-indent-1 {\r\n" + 
				"  counter-reset: list-2 list-3 list-4 list-5 list-6 list-7 list-8 list-9;\r\n" + 
				"}\r\n" + 
				".ql-editor ol li.ql-indent-2 {\r\n" + 
				"  counter-increment: list-2;\r\n" + 
				"}\r\n" + 
				".ql-editor ol li.ql-indent-2:before {\r\n" + 
				"  content: counter(list-2, lower-roman) '. ';\r\n" + 
				"}\r\n" + 
				".ql-editor ol li.ql-indent-2 {\r\n" + 
				"  counter-reset: list-3 list-4 list-5 list-6 list-7 list-8 list-9;\r\n" + 
				"}\r\n" + 
				".ql-editor ol li.ql-indent-3 {\r\n" + 
				"  counter-increment: list-3;\r\n" + 
				"}\r\n" + 
				".ql-editor ol li.ql-indent-3:before {\r\n" + 
				"  content: counter(list-3, decimal) '. ';\r\n" + 
				"}\r\n" + 
				".ql-editor ol li.ql-indent-3 {\r\n" + 
				"  counter-reset: list-4 list-5 list-6 list-7 list-8 list-9;\r\n" + 
				"}\r\n" + 
				".ql-editor ol li.ql-indent-4 {\r\n" + 
				"  counter-increment: list-4;\r\n" + 
				"}\r\n" + 
				".ql-editor ol li.ql-indent-4:before {\r\n" + 
				"  content: counter(list-4, lower-alpha) '. ';\r\n" + 
				"}\r\n" + 
				".ql-editor ol li.ql-indent-4 {\r\n" + 
				"  counter-reset: list-5 list-6 list-7 list-8 list-9;\r\n" + 
				"}\r\n" + 
				".ql-editor ol li.ql-indent-5 {\r\n" + 
				"  counter-increment: list-5;\r\n" + 
				"}\r\n" + 
				".ql-editor ol li.ql-indent-5:before {\r\n" + 
				"  content: counter(list-5, lower-roman) '. ';\r\n" + 
				"}\r\n" + 
				".ql-editor ol li.ql-indent-5 {\r\n" + 
				"  counter-reset: list-6 list-7 list-8 list-9;\r\n" + 
				"}\r\n" + 
				".ql-editor ol li.ql-indent-6 {\r\n" + 
				"  counter-increment: list-6;\r\n" + 
				"}\r\n" + 
				".ql-editor ol li.ql-indent-6:before {\r\n" + 
				"  content: counter(list-6, decimal) '. ';\r\n" + 
				"}\r\n" + 
				".ql-editor ol li.ql-indent-6 {\r\n" + 
				"  counter-reset: list-7 list-8 list-9;\r\n" + 
				"}\r\n" + 
				".ql-editor ol li.ql-indent-7 {\r\n" + 
				"  counter-increment: list-7;\r\n" + 
				"}\r\n" + 
				".ql-editor ol li.ql-indent-7:before {\r\n" + 
				"  content: counter(list-7, lower-alpha) '. ';\r\n" + 
				"}\r\n" + 
				".ql-editor ol li.ql-indent-7 {\r\n" + 
				"  counter-reset: list-8 list-9;\r\n" + 
				"}\r\n" + 
				".ql-editor ol li.ql-indent-8 {\r\n" + 
				"  counter-increment: list-8;\r\n" + 
				"}\r\n" + 
				".ql-editor ol li.ql-indent-8:before {\r\n" + 
				"  content: counter(list-8, lower-roman) '. ';\r\n" + 
				"}\r\n" + 
				".ql-editor ol li.ql-indent-8 {\r\n" + 
				"  counter-reset: list-9;\r\n" + 
				"}\r\n" + 
				".ql-editor ol li.ql-indent-9 {\r\n" + 
				"  counter-increment: list-9;\r\n" + 
				"}\r\n" + 
				".ql-editor ol li.ql-indent-9:before {\r\n" + 
				"  content: counter(list-9, decimal) '. ';\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-indent-1:not(.ql-direction-rtl) {\r\n" + 
				"  padding-left: 3em;\r\n" + 
				"}\r\n" + 
				".ql-editor li.ql-indent-1:not(.ql-direction-rtl) {\r\n" + 
				"  padding-left: 4.5em;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-indent-1.ql-direction-rtl.ql-align-right {\r\n" + 
				"  padding-right: 3em;\r\n" + 
				"}\r\n" + 
				".ql-editor li.ql-indent-1.ql-direction-rtl.ql-align-right {\r\n" + 
				"  padding-right: 4.5em;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-indent-2:not(.ql-direction-rtl) {\r\n" + 
				"  padding-left: 6em;\r\n" + 
				"}\r\n" + 
				".ql-editor li.ql-indent-2:not(.ql-direction-rtl) {\r\n" + 
				"  padding-left: 7.5em;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-indent-2.ql-direction-rtl.ql-align-right {\r\n" + 
				"  padding-right: 6em;\r\n" + 
				"}\r\n" + 
				".ql-editor li.ql-indent-2.ql-direction-rtl.ql-align-right {\r\n" + 
				"  padding-right: 7.5em;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-indent-3:not(.ql-direction-rtl) {\r\n" + 
				"  padding-left: 9em;\r\n" + 
				"}\r\n" + 
				".ql-editor li.ql-indent-3:not(.ql-direction-rtl) {\r\n" + 
				"  padding-left: 10.5em;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-indent-3.ql-direction-rtl.ql-align-right {\r\n" + 
				"  padding-right: 9em;\r\n" + 
				"}\r\n" + 
				".ql-editor li.ql-indent-3.ql-direction-rtl.ql-align-right {\r\n" + 
				"  padding-right: 10.5em;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-indent-4:not(.ql-direction-rtl) {\r\n" + 
				"  padding-left: 12em;\r\n" + 
				"}\r\n" + 
				".ql-editor li.ql-indent-4:not(.ql-direction-rtl) {\r\n" + 
				"  padding-left: 13.5em;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-indent-4.ql-direction-rtl.ql-align-right {\r\n" + 
				"  padding-right: 12em;\r\n" + 
				"}\r\n" + 
				".ql-editor li.ql-indent-4.ql-direction-rtl.ql-align-right {\r\n" + 
				"  padding-right: 13.5em;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-indent-5:not(.ql-direction-rtl) {\r\n" + 
				"  padding-left: 15em;\r\n" + 
				"}\r\n" + 
				".ql-editor li.ql-indent-5:not(.ql-direction-rtl) {\r\n" + 
				"  padding-left: 16.5em;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-indent-5.ql-direction-rtl.ql-align-right {\r\n" + 
				"  padding-right: 15em;\r\n" + 
				"}\r\n" + 
				".ql-editor li.ql-indent-5.ql-direction-rtl.ql-align-right {\r\n" + 
				"  padding-right: 16.5em;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-indent-6:not(.ql-direction-rtl) {\r\n" + 
				"  padding-left: 18em;\r\n" + 
				"}\r\n" + 
				".ql-editor li.ql-indent-6:not(.ql-direction-rtl) {\r\n" + 
				"  padding-left: 19.5em;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-indent-6.ql-direction-rtl.ql-align-right {\r\n" + 
				"  padding-right: 18em;\r\n" + 
				"}\r\n" + 
				".ql-editor li.ql-indent-6.ql-direction-rtl.ql-align-right {\r\n" + 
				"  padding-right: 19.5em;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-indent-7:not(.ql-direction-rtl) {\r\n" + 
				"  padding-left: 21em;\r\n" + 
				"}\r\n" + 
				".ql-editor li.ql-indent-7:not(.ql-direction-rtl) {\r\n" + 
				"  padding-left: 22.5em;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-indent-7.ql-direction-rtl.ql-align-right {\r\n" + 
				"  padding-right: 21em;\r\n" + 
				"}\r\n" + 
				".ql-editor li.ql-indent-7.ql-direction-rtl.ql-align-right {\r\n" + 
				"  padding-right: 22.5em;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-indent-8:not(.ql-direction-rtl) {\r\n" + 
				"  padding-left: 24em;\r\n" + 
				"}\r\n" + 
				".ql-editor li.ql-indent-8:not(.ql-direction-rtl) {\r\n" + 
				"  padding-left: 25.5em;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-indent-8.ql-direction-rtl.ql-align-right {\r\n" + 
				"  padding-right: 24em;\r\n" + 
				"}\r\n" + 
				".ql-editor li.ql-indent-8.ql-direction-rtl.ql-align-right {\r\n" + 
				"  padding-right: 25.5em;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-indent-9:not(.ql-direction-rtl) {\r\n" + 
				"  padding-left: 27em;\r\n" + 
				"}\r\n" + 
				".ql-editor li.ql-indent-9:not(.ql-direction-rtl) {\r\n" + 
				"  padding-left: 28.5em;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-indent-9.ql-direction-rtl.ql-align-right {\r\n" + 
				"  padding-right: 27em;\r\n" + 
				"}\r\n" + 
				".ql-editor li.ql-indent-9.ql-direction-rtl.ql-align-right {\r\n" + 
				"  padding-right: 28.5em;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-video {\r\n" + 
				"  display: block;\r\n" + 
				"  max-width: 100%;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-video.ql-align-center {\r\n" + 
				"  margin: 0 auto;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-video.ql-align-right {\r\n" + 
				"  margin: 0 0 0 auto;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-bg-black {\r\n" + 
				"  background-color: #000;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-bg-red {\r\n" + 
				"  background-color: #e60000;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-bg-orange {\r\n" + 
				"  background-color: #f90;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-bg-yellow {\r\n" + 
				"  background-color: #ff0;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-bg-green {\r\n" + 
				"  background-color: #008a00;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-bg-blue {\r\n" + 
				"  background-color: #06c;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-bg-purple {\r\n" + 
				"  background-color: #93f;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-color-white {\r\n" + 
				"  color: #fff;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-color-red {\r\n" + 
				"  color: #e60000;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-color-orange {\r\n" + 
				"  color: #f90;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-color-yellow {\r\n" + 
				"  color: #ff0;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-color-green {\r\n" + 
				"  color: #008a00;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-color-blue {\r\n" + 
				"  color: #06c;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-color-purple {\r\n" + 
				"  color: #93f;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-font-serif {\r\n" + 
				"  font-family: Georgia, Times New Roman, serif;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-font-monospace {\r\n" + 
				"  font-family: Monaco, Courier New, monospace;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-size-small {\r\n" + 
				"  font-size: 0.75em;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-size-large {\r\n" + 
				"  font-size: 1.5em;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-size-huge {\r\n" + 
				"  font-size: 2.5em;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-direction-rtl {\r\n" + 
				"  direction: rtl;\r\n" + 
				"  text-align: inherit;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-align-center {\r\n" + 
				"  text-align: center;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-align-justify {\r\n" + 
				"  text-align: justify;\r\n" + 
				"}\r\n" + 
				".ql-editor .ql-align-right {\r\n" + 
				"  text-align: right;\r\n" + 
				"}\r\n" + 
				".ql-editor.ql-blank::before {\r\n" + 
				"  color: rgba(0,0,0,0.6);\r\n" + 
				"  content: attr(data-placeholder);\r\n" + 
				"  font-style: italic;\r\n" + 
				"  left: 15px;\r\n" + 
				"  pointer-events: none;\r\n" + 
				"  position: absolute;\r\n" + 
				"  right: 15px;\r\n" + 
				"}\r\n" + 
				".ql-snow.ql-toolbar:after,\r\n" + 
				".ql-snow .ql-toolbar:after {\r\n" + 
				"  clear: both;\r\n" + 
				"  content: '';\r\n" + 
				"  display: table;\r\n" + 
				"}\r\n" + 
				".ql-snow.ql-toolbar button,\r\n" + 
				".ql-snow .ql-toolbar button {\r\n" + 
				"  background: none;\r\n" + 
				"  border: none;\r\n" + 
				"  cursor: pointer;\r\n" + 
				"  display: inline-block;\r\n" + 
				"  float: left;\r\n" + 
				"  height: 24px;\r\n" + 
				"  padding: 3px 5px;\r\n" + 
				"  width: 28px;\r\n" + 
				"}\r\n" + 
				".ql-snow.ql-toolbar button svg,\r\n" + 
				".ql-snow .ql-toolbar button svg {\r\n" + 
				"  float: left;\r\n" + 
				"  height: 100%;\r\n" + 
				"}\r\n" + 
				".ql-snow.ql-toolbar button:active:hover,\r\n" + 
				".ql-snow .ql-toolbar button:active:hover {\r\n" + 
				"  outline: none;\r\n" + 
				"}\r\n" + 
				".ql-snow.ql-toolbar input.ql-image[type=file],\r\n" + 
				".ql-snow .ql-toolbar input.ql-image[type=file] {\r\n" + 
				"  display: none;\r\n" + 
				"}\r\n" + 
				".ql-snow.ql-toolbar button:hover,\r\n" + 
				".ql-snow .ql-toolbar button:hover,\r\n" + 
				".ql-snow.ql-toolbar button:focus,\r\n" + 
				".ql-snow .ql-toolbar button:focus,\r\n" + 
				".ql-snow.ql-toolbar button.ql-active,\r\n" + 
				".ql-snow .ql-toolbar button.ql-active,\r\n" + 
				".ql-snow.ql-toolbar .ql-picker-label:hover,\r\n" + 
				".ql-snow .ql-toolbar .ql-picker-label:hover,\r\n" + 
				".ql-snow.ql-toolbar .ql-picker-label.ql-active,\r\n" + 
				".ql-snow .ql-toolbar .ql-picker-label.ql-active,\r\n" + 
				".ql-snow.ql-toolbar .ql-picker-item:hover,\r\n" + 
				".ql-snow .ql-toolbar .ql-picker-item:hover,\r\n" + 
				".ql-snow.ql-toolbar .ql-picker-item.ql-selected,\r\n" + 
				".ql-snow .ql-toolbar .ql-picker-item.ql-selected {\r\n" + 
				"  color: #06c;\r\n" + 
				"}\r\n" + 
				".ql-snow.ql-toolbar button:hover .ql-fill,\r\n" + 
				".ql-snow .ql-toolbar button:hover .ql-fill,\r\n" + 
				".ql-snow.ql-toolbar button:focus .ql-fill,\r\n" + 
				".ql-snow .ql-toolbar button:focus .ql-fill,\r\n" + 
				".ql-snow.ql-toolbar button.ql-active .ql-fill,\r\n" + 
				".ql-snow .ql-toolbar button.ql-active .ql-fill,\r\n" + 
				".ql-snow.ql-toolbar .ql-picker-label:hover .ql-fill,\r\n" + 
				".ql-snow .ql-toolbar .ql-picker-label:hover .ql-fill,\r\n" + 
				".ql-snow.ql-toolbar .ql-picker-label.ql-active .ql-fill,\r\n" + 
				".ql-snow .ql-toolbar .ql-picker-label.ql-active .ql-fill,\r\n" + 
				".ql-snow.ql-toolbar .ql-picker-item:hover .ql-fill,\r\n" + 
				".ql-snow .ql-toolbar .ql-picker-item:hover .ql-fill,\r\n" + 
				".ql-snow.ql-toolbar .ql-picker-item.ql-selected .ql-fill,\r\n" + 
				".ql-snow .ql-toolbar .ql-picker-item.ql-selected .ql-fill,\r\n" + 
				".ql-snow.ql-toolbar button:hover .ql-stroke.ql-fill,\r\n" + 
				".ql-snow .ql-toolbar button:hover .ql-stroke.ql-fill,\r\n" + 
				".ql-snow.ql-toolbar button:focus .ql-stroke.ql-fill,\r\n" + 
				".ql-snow .ql-toolbar button:focus .ql-stroke.ql-fill,\r\n" + 
				".ql-snow.ql-toolbar button.ql-active .ql-stroke.ql-fill,\r\n" + 
				".ql-snow .ql-toolbar button.ql-active .ql-stroke.ql-fill,\r\n" + 
				".ql-snow.ql-toolbar .ql-picker-label:hover .ql-stroke.ql-fill,\r\n" + 
				".ql-snow .ql-toolbar .ql-picker-label:hover .ql-stroke.ql-fill,\r\n" + 
				".ql-snow.ql-toolbar .ql-picker-label.ql-active .ql-stroke.ql-fill,\r\n" + 
				".ql-snow .ql-toolbar .ql-picker-label.ql-active .ql-stroke.ql-fill,\r\n" + 
				".ql-snow.ql-toolbar .ql-picker-item:hover .ql-stroke.ql-fill,\r\n" + 
				".ql-snow .ql-toolbar .ql-picker-item:hover .ql-stroke.ql-fill,\r\n" + 
				".ql-snow.ql-toolbar .ql-picker-item.ql-selected .ql-stroke.ql-fill,\r\n" + 
				".ql-snow .ql-toolbar .ql-picker-item.ql-selected .ql-stroke.ql-fill {\r\n" + 
				"  fill: #06c;\r\n" + 
				"}\r\n" + 
				".ql-snow.ql-toolbar button:hover .ql-stroke,\r\n" + 
				".ql-snow .ql-toolbar button:hover .ql-stroke,\r\n" + 
				".ql-snow.ql-toolbar button:focus .ql-stroke,\r\n" + 
				".ql-snow .ql-toolbar button:focus .ql-stroke,\r\n" + 
				".ql-snow.ql-toolbar button.ql-active .ql-stroke,\r\n" + 
				".ql-snow .ql-toolbar button.ql-active .ql-stroke,\r\n" + 
				".ql-snow.ql-toolbar .ql-picker-label:hover .ql-stroke,\r\n" + 
				".ql-snow .ql-toolbar .ql-picker-label:hover .ql-stroke,\r\n" + 
				".ql-snow.ql-toolbar .ql-picker-label.ql-active .ql-stroke,\r\n" + 
				".ql-snow .ql-toolbar .ql-picker-label.ql-active .ql-stroke,\r\n" + 
				".ql-snow.ql-toolbar .ql-picker-item:hover .ql-stroke,\r\n" + 
				".ql-snow .ql-toolbar .ql-picker-item:hover .ql-stroke,\r\n" + 
				".ql-snow.ql-toolbar .ql-picker-item.ql-selected .ql-stroke,\r\n" + 
				".ql-snow .ql-toolbar .ql-picker-item.ql-selected .ql-stroke,\r\n" + 
				".ql-snow.ql-toolbar button:hover .ql-stroke-miter,\r\n" + 
				".ql-snow .ql-toolbar button:hover .ql-stroke-miter,\r\n" + 
				".ql-snow.ql-toolbar button:focus .ql-stroke-miter,\r\n" + 
				".ql-snow .ql-toolbar button:focus .ql-stroke-miter,\r\n" + 
				".ql-snow.ql-toolbar button.ql-active .ql-stroke-miter,\r\n" + 
				".ql-snow .ql-toolbar button.ql-active .ql-stroke-miter,\r\n" + 
				".ql-snow.ql-toolbar .ql-picker-label:hover .ql-stroke-miter,\r\n" + 
				".ql-snow .ql-toolbar .ql-picker-label:hover .ql-stroke-miter,\r\n" + 
				".ql-snow.ql-toolbar .ql-picker-label.ql-active .ql-stroke-miter,\r\n" + 
				".ql-snow .ql-toolbar .ql-picker-label.ql-active .ql-stroke-miter,\r\n" + 
				".ql-snow.ql-toolbar .ql-picker-item:hover .ql-stroke-miter,\r\n" + 
				".ql-snow .ql-toolbar .ql-picker-item:hover .ql-stroke-miter,\r\n" + 
				".ql-snow.ql-toolbar .ql-picker-item.ql-selected .ql-stroke-miter,\r\n" + 
				".ql-snow .ql-toolbar .ql-picker-item.ql-selected .ql-stroke-miter {\r\n" + 
				"  stroke: #06c;\r\n" + 
				"}\r\n" + 
				"@media (pointer: coarse) {\r\n" + 
				"  .ql-snow.ql-toolbar button:hover:not(.ql-active),\r\n" + 
				"  .ql-snow .ql-toolbar button:hover:not(.ql-active) {\r\n" + 
				"    color: #444;\r\n" + 
				"  }\r\n" + 
				"  .ql-snow.ql-toolbar button:hover:not(.ql-active) .ql-fill,\r\n" + 
				"  .ql-snow .ql-toolbar button:hover:not(.ql-active) .ql-fill,\r\n" + 
				"  .ql-snow.ql-toolbar button:hover:not(.ql-active) .ql-stroke.ql-fill,\r\n" + 
				"  .ql-snow .ql-toolbar button:hover:not(.ql-active) .ql-stroke.ql-fill {\r\n" + 
				"    fill: #444;\r\n" + 
				"  }\r\n" + 
				"  .ql-snow.ql-toolbar button:hover:not(.ql-active) .ql-stroke,\r\n" + 
				"  .ql-snow .ql-toolbar button:hover:not(.ql-active) .ql-stroke,\r\n" + 
				"  .ql-snow.ql-toolbar button:hover:not(.ql-active) .ql-stroke-miter,\r\n" + 
				"  .ql-snow .ql-toolbar button:hover:not(.ql-active) .ql-stroke-miter {\r\n" + 
				"    stroke: #444;\r\n" + 
				"  }\r\n" + 
				"}\r\n" + 
				".ql-snow {\r\n" + 
				"  box-sizing: border-box;\r\n" + 
				"}\r\n" + 
				".ql-snow * {\r\n" + 
				"  box-sizing: border-box;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-hidden {\r\n" + 
				"  display: none;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-out-bottom,\r\n" + 
				".ql-snow .ql-out-top {\r\n" + 
				"  visibility: hidden;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-tooltip {\r\n" + 
				"  position: absolute;\r\n" + 
				"  transform: translateY(10px);\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-tooltip a {\r\n" + 
				"  cursor: pointer;\r\n" + 
				"  text-decoration: none;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-tooltip.ql-flip {\r\n" + 
				"  transform: translateY(-10px);\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-formats {\r\n" + 
				"  display: inline-block;\r\n" + 
				"  vertical-align: middle;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-formats:after {\r\n" + 
				"  clear: both;\r\n" + 
				"  content: '';\r\n" + 
				"  display: table;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-stroke {\r\n" + 
				"  fill: none;\r\n" + 
				"  stroke: #444;\r\n" + 
				"  stroke-linecap: round;\r\n" + 
				"  stroke-linejoin: round;\r\n" + 
				"  stroke-width: 2;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-stroke-miter {\r\n" + 
				"  fill: none;\r\n" + 
				"  stroke: #444;\r\n" + 
				"  stroke-miterlimit: 10;\r\n" + 
				"  stroke-width: 2;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-fill,\r\n" + 
				".ql-snow .ql-stroke.ql-fill {\r\n" + 
				"  fill: #444;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-empty {\r\n" + 
				"  fill: none;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-even {\r\n" + 
				"  fill-rule: evenodd;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-thin,\r\n" + 
				".ql-snow .ql-stroke.ql-thin {\r\n" + 
				"  stroke-width: 1;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-transparent {\r\n" + 
				"  opacity: 0.4;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-direction svg:last-child {\r\n" + 
				"  display: none;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-direction.ql-active svg:last-child {\r\n" + 
				"  display: inline;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-direction.ql-active svg:first-child {\r\n" + 
				"  display: none;\r\n" + 
				"}\r\n" + 
				".ql-editor h1 {\r\n" + 
				"  font-size: 2em;\r\n" + 
				"}\r\n" + 
				".ql-editor h2 {\r\n" + 
				"  font-size: 1.5em;\r\n" + 
				"}\r\n" + 
				".ql-editor h3 {\r\n" + 
				"  font-size: 1.17em;\r\n" + 
				"}\r\n" + 
				".ql-editor h4 {\r\n" + 
				"  font-size: 1em;\r\n" + 
				"}\r\n" + 
				".ql-editor h5 {\r\n" + 
				"  font-size: 0.83em;\r\n" + 
				"}\r\n" + 
				".ql-editor h6 {\r\n" + 
				"  font-size: 0.67em;\r\n" + 
				"}\r\n" + 
				".ql-editor a {\r\n" + 
				"  text-decoration: underline;\r\n" + 
				"}\r\n" + 
				".ql-editor blockquote {\r\n" + 
				"  border-left: 4px solid #ccc;\r\n" + 
				"  margin-bottom: 5px;\r\n" + 
				"  margin-top: 5px;\r\n" + 
				"  padding-left: 16px;\r\n" + 
				"}\r\n" + 
				".ql-editor code,\r\n" + 
				".ql-editor pre {\r\n" + 
				"  background-color: #f0f0f0;\r\n" + 
				"  border-radius: 3px;\r\n" + 
				"}\r\n" + 
				".ql-editor pre {\r\n" + 
				"  white-space: pre-wrap;\r\n" + 
				"  margin-bottom: 5px;\r\n" + 
				"  margin-top: 5px;\r\n" + 
				"  padding: 5px 10px;\r\n" + 
				"}\r\n" + 
				".ql-editor code {\r\n" + 
				"  font-size: 85%;\r\n" + 
				"  padding: 2px 4px;\r\n" + 
				"}\r\n" + 
				".ql-editor pre.ql-syntax {\r\n" + 
				"  background-color: #23241f;\r\n" + 
				"  color: #f8f8f2;\r\n" + 
				"  overflow: visible;\r\n" + 
				"}\r\n" + 
				".ql-editor img {\r\n" + 
				"  max-width: 100%;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker {\r\n" + 
				"  color: #444;\r\n" + 
				"  display: inline-block;\r\n" + 
				"  float: left;\r\n" + 
				"  font-size: 14px;\r\n" + 
				"  font-weight: 500;\r\n" + 
				"  height: 24px;\r\n" + 
				"  position: relative;\r\n" + 
				"  vertical-align: middle;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker-label {\r\n" + 
				"  cursor: pointer;\r\n" + 
				"  display: inline-block;\r\n" + 
				"  height: 100%;\r\n" + 
				"  padding-left: 8px;\r\n" + 
				"  padding-right: 2px;\r\n" + 
				"  position: relative;\r\n" + 
				"  width: 100%;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker-label::before {\r\n" + 
				"  display: inline-block;\r\n" + 
				"  line-height: 22px;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker-options {\r\n" + 
				"  background-color: #fff;\r\n" + 
				"  display: none;\r\n" + 
				"  min-width: 100%;\r\n" + 
				"  padding: 4px 8px;\r\n" + 
				"  position: absolute;\r\n" + 
				"  white-space: nowrap;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker-options .ql-picker-item {\r\n" + 
				"  cursor: pointer;\r\n" + 
				"  display: block;\r\n" + 
				"  padding-bottom: 5px;\r\n" + 
				"  padding-top: 5px;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker.ql-expanded .ql-picker-label {\r\n" + 
				"  color: #ccc;\r\n" + 
				"  z-index: 2;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker.ql-expanded .ql-picker-label .ql-fill {\r\n" + 
				"  fill: #ccc;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker.ql-expanded .ql-picker-label .ql-stroke {\r\n" + 
				"  stroke: #ccc;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker.ql-expanded .ql-picker-options {\r\n" + 
				"  display: block;\r\n" + 
				"  margin-top: -1px;\r\n" + 
				"  top: 100%;\r\n" + 
				"  z-index: 1;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-color-picker,\r\n" + 
				".ql-snow .ql-icon-picker {\r\n" + 
				"  width: 28px;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-color-picker .ql-picker-label,\r\n" + 
				".ql-snow .ql-icon-picker .ql-picker-label {\r\n" + 
				"  padding: 2px 4px;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-color-picker .ql-picker-label svg,\r\n" + 
				".ql-snow .ql-icon-picker .ql-picker-label svg {\r\n" + 
				"  right: 4px;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-icon-picker .ql-picker-options {\r\n" + 
				"  padding: 4px 0px;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-icon-picker .ql-picker-item {\r\n" + 
				"  height: 24px;\r\n" + 
				"  width: 24px;\r\n" + 
				"  padding: 2px 4px;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-color-picker .ql-picker-options {\r\n" + 
				"  padding: 3px 5px;\r\n" + 
				"  width: 152px;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-color-picker .ql-picker-item {\r\n" + 
				"  border: 1px solid transparent;\r\n" + 
				"  float: left;\r\n" + 
				"  height: 16px;\r\n" + 
				"  margin: 2px;\r\n" + 
				"  padding: 0px;\r\n" + 
				"  width: 16px;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker:not(.ql-color-picker):not(.ql-icon-picker) svg {\r\n" + 
				"  position: absolute;\r\n" + 
				"  margin-top: -9px;\r\n" + 
				"  right: 0;\r\n" + 
				"  top: 50%;\r\n" + 
				"  width: 18px;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker.ql-header .ql-picker-label[data-label]:not([data-label=''])::before,\r\n" + 
				".ql-snow .ql-picker.ql-font .ql-picker-label[data-label]:not([data-label=''])::before,\r\n" + 
				".ql-snow .ql-picker.ql-size .ql-picker-label[data-label]:not([data-label=''])::before,\r\n" + 
				".ql-snow .ql-picker.ql-header .ql-picker-item[data-label]:not([data-label=''])::before,\r\n" + 
				".ql-snow .ql-picker.ql-font .ql-picker-item[data-label]:not([data-label=''])::before,\r\n" + 
				".ql-snow .ql-picker.ql-size .ql-picker-item[data-label]:not([data-label=''])::before {\r\n" + 
				"  content: attr(data-label);\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker.ql-header {\r\n" + 
				"  width: 98px;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker.ql-header .ql-picker-label::before,\r\n" + 
				".ql-snow .ql-picker.ql-header .ql-picker-item::before {\r\n" + 
				"  content: 'Normal';\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker.ql-header .ql-picker-label[data-value=\"1\"]::before,\r\n" + 
				".ql-snow .ql-picker.ql-header .ql-picker-item[data-value=\"1\"]::before {\r\n" + 
				"  content: 'Heading 1';\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker.ql-header .ql-picker-label[data-value=\"2\"]::before,\r\n" + 
				".ql-snow .ql-picker.ql-header .ql-picker-item[data-value=\"2\"]::before {\r\n" + 
				"  content: 'Heading 2';\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker.ql-header .ql-picker-label[data-value=\"3\"]::before,\r\n" + 
				".ql-snow .ql-picker.ql-header .ql-picker-item[data-value=\"3\"]::before {\r\n" + 
				"  content: 'Heading 3';\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker.ql-header .ql-picker-label[data-value=\"4\"]::before,\r\n" + 
				".ql-snow .ql-picker.ql-header .ql-picker-item[data-value=\"4\"]::before {\r\n" + 
				"  content: 'Heading 4';\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker.ql-header .ql-picker-label[data-value=\"5\"]::before,\r\n" + 
				".ql-snow .ql-picker.ql-header .ql-picker-item[data-value=\"5\"]::before {\r\n" + 
				"  content: 'Heading 5';\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker.ql-header .ql-picker-label[data-value=\"6\"]::before,\r\n" + 
				".ql-snow .ql-picker.ql-header .ql-picker-item[data-value=\"6\"]::before {\r\n" + 
				"  content: 'Heading 6';\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker.ql-header .ql-picker-item[data-value=\"1\"]::before {\r\n" + 
				"  font-size: 2em;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker.ql-header .ql-picker-item[data-value=\"2\"]::before {\r\n" + 
				"  font-size: 1.5em;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker.ql-header .ql-picker-item[data-value=\"3\"]::before {\r\n" + 
				"  font-size: 1.17em;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker.ql-header .ql-picker-item[data-value=\"4\"]::before {\r\n" + 
				"  font-size: 1em;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker.ql-header .ql-picker-item[data-value=\"5\"]::before {\r\n" + 
				"  font-size: 0.83em;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker.ql-header .ql-picker-item[data-value=\"6\"]::before {\r\n" + 
				"  font-size: 0.67em;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker.ql-font {\r\n" + 
				"  width: 108px;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker.ql-font .ql-picker-label::before,\r\n" + 
				".ql-snow .ql-picker.ql-font .ql-picker-item::before {\r\n" + 
				"  content: 'Sans Serif';\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker.ql-font .ql-picker-label[data-value=serif]::before,\r\n" + 
				".ql-snow .ql-picker.ql-font .ql-picker-item[data-value=serif]::before {\r\n" + 
				"  content: 'Serif';\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker.ql-font .ql-picker-label[data-value=monospace]::before,\r\n" + 
				".ql-snow .ql-picker.ql-font .ql-picker-item[data-value=monospace]::before {\r\n" + 
				"  content: 'Monospace';\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker.ql-font .ql-picker-item[data-value=serif]::before {\r\n" + 
				"  font-family: Georgia, Times New Roman, serif;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker.ql-font .ql-picker-item[data-value=monospace]::before {\r\n" + 
				"  font-family: Monaco, Courier New, monospace;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker.ql-size {\r\n" + 
				"  width: 98px;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker.ql-size .ql-picker-label::before,\r\n" + 
				".ql-snow .ql-picker.ql-size .ql-picker-item::before {\r\n" + 
				"  content: 'Normal';\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker.ql-size .ql-picker-label[data-value=small]::before,\r\n" + 
				".ql-snow .ql-picker.ql-size .ql-picker-item[data-value=small]::before {\r\n" + 
				"  content: 'Small';\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker.ql-size .ql-picker-label[data-value=large]::before,\r\n" + 
				".ql-snow .ql-picker.ql-size .ql-picker-item[data-value=large]::before {\r\n" + 
				"  content: 'Large';\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker.ql-size .ql-picker-label[data-value=huge]::before,\r\n" + 
				".ql-snow .ql-picker.ql-size .ql-picker-item[data-value=huge]::before {\r\n" + 
				"  content: 'Huge';\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker.ql-size .ql-picker-item[data-value=small]::before {\r\n" + 
				"  font-size: 10px;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker.ql-size .ql-picker-item[data-value=large]::before {\r\n" + 
				"  font-size: 18px;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-picker.ql-size .ql-picker-item[data-value=huge]::before {\r\n" + 
				"  font-size: 32px;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-color-picker.ql-background .ql-picker-item {\r\n" + 
				"  background-color: #fff;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-color-picker.ql-color .ql-picker-item {\r\n" + 
				"  background-color: #000;\r\n" + 
				"}\r\n" + 
				".ql-toolbar.ql-snow {\r\n" + 
				"  border: 1px solid #ccc;\r\n" + 
				"  box-sizing: border-box;\r\n" + 
				"  font-family: 'Helvetica Neue', 'Helvetica', 'Arial', sans-serif;\r\n" + 
				"  padding: 8px;\r\n" + 
				"}\r\n" + 
				".ql-toolbar.ql-snow .ql-formats {\r\n" + 
				"  margin-right: 15px;\r\n" + 
				"}\r\n" + 
				".ql-toolbar.ql-snow .ql-picker-label {\r\n" + 
				"  border: 1px solid transparent;\r\n" + 
				"}\r\n" + 
				".ql-toolbar.ql-snow .ql-picker-options {\r\n" + 
				"  border: 1px solid transparent;\r\n" + 
				"  box-shadow: rgba(0,0,0,0.2) 0 2px 8px;\r\n" + 
				"}\r\n" + 
				".ql-toolbar.ql-snow .ql-picker.ql-expanded .ql-picker-label {\r\n" + 
				"  border-color: #ccc;\r\n" + 
				"}\r\n" + 
				".ql-toolbar.ql-snow .ql-picker.ql-expanded .ql-picker-options {\r\n" + 
				"  border-color: #ccc;\r\n" + 
				"}\r\n" + 
				".ql-toolbar.ql-snow .ql-color-picker .ql-picker-item.ql-selected,\r\n" + 
				".ql-toolbar.ql-snow .ql-color-picker .ql-picker-item:hover {\r\n" + 
				"  border-color: #000;\r\n" + 
				"}\r\n" + 
				".ql-toolbar.ql-snow + .ql-container.ql-snow {\r\n" + 
				"  border-top: 0px;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-tooltip {\r\n" + 
				"  background-color: #fff;\r\n" + 
				"  border: 1px solid #ccc;\r\n" + 
				"  box-shadow: 0px 0px 5px #ddd;\r\n" + 
				"  color: #444;\r\n" + 
				"  padding: 5px 12px;\r\n" + 
				"  white-space: nowrap;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-tooltip::before {\r\n" + 
				"  content: \"Visit URL:\";\r\n" + 
				"  line-height: 26px;\r\n" + 
				"  margin-right: 8px;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-tooltip input[type=text] {\r\n" + 
				"  display: none;\r\n" + 
				"  border: 1px solid #ccc;\r\n" + 
				"  font-size: 13px;\r\n" + 
				"  height: 26px;\r\n" + 
				"  margin: 0px;\r\n" + 
				"  padding: 3px 5px;\r\n" + 
				"  width: 170px;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-tooltip a.ql-preview {\r\n" + 
				"  display: inline-block;\r\n" + 
				"  max-width: 200px;\r\n" + 
				"  overflow-x: hidden;\r\n" + 
				"  text-overflow: ellipsis;\r\n" + 
				"  vertical-align: top;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-tooltip a.ql-action::after {\r\n" + 
				"  border-right: 1px solid #ccc;\r\n" + 
				"  content: 'Edit';\r\n" + 
				"  margin-left: 16px;\r\n" + 
				"  padding-right: 8px;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-tooltip a.ql-remove::before {\r\n" + 
				"  content: 'Remove';\r\n" + 
				"  margin-left: 8px;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-tooltip a {\r\n" + 
				"  line-height: 26px;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-tooltip.ql-editing a.ql-preview,\r\n" + 
				".ql-snow .ql-tooltip.ql-editing a.ql-remove {\r\n" + 
				"  display: none;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-tooltip.ql-editing input[type=text] {\r\n" + 
				"  display: inline-block;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-tooltip.ql-editing a.ql-action::after {\r\n" + 
				"  border-right: 0px;\r\n" + 
				"  content: 'Save';\r\n" + 
				"  padding-right: 0px;\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-tooltip[data-mode=link]::before {\r\n" + 
				"  content: \"Enter link:\";\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-tooltip[data-mode=formula]::before {\r\n" + 
				"  content: \"Enter formula:\";\r\n" + 
				"}\r\n" + 
				".ql-snow .ql-tooltip[data-mode=video]::before {\r\n" + 
				"  content: \"Enter video:\";\r\n" + 
				"}\r\n" + 
				".ql-snow a {\r\n" + 
				"  color: #06c;\r\n" + 
				"}\r\n" + 
				".ql-container.ql-snow {\r\n" + 
				"  border: 1px solid #ccc;\r\n" + 
				"}\r\n" + 
				
				"    p {\r\n" + 
				"\r\n" + 
				"      word-wrap: break-word;\r\n" + 
				"\r\n" + 
				"    }\r\n" + 
				"    h1 {\r\n" + 
				"      font-size: 2em;\r\n" + 
				"    }\r\n" + 
				"    h2 {\r\n" + 
				"      font-size: 1.5em;\r\n" + 
				"    }\r\n" + 
				"    h3 {\r\n" + 
				"      font-size: 1.17em;\r\n" + 
				"    }\r\n" + 
				"    h4 {\r\n" + 
				"      font-size: 1em;\r\n" + 
				"    }\r\n" + 
				"    h5 {\r\n" + 
				"      font-size: 0.83em;\r\n" + 
				"    }\r\n" + 
				"    h6 {\r\n" + 
				"      font-size: 0.67em;\r\n" + 
				"    }\r\n" + 
				"    a {\r\n" + 
				"      text-decoration: underline;\r\n" + 
				"    }\r\n" + 
				"    blockquote {\r\n" + 
				"      border-left: 4px solid #ccc;\r\n" + 
				"      margin-bottom: 5px;\r\n" + 
				"      margin-top: 5px;\r\n" + 
				"      padding-left: 16px;\r\n" + 
				"    }\r\n" + 
				"    code,\r\n" + 
				"    pre {\r\n" + 
				"      background-color: #f0f0f0;\r\n" + 
				"      border-radius: 3px;\r\n" + 
				"    }\r\n" + 
				"    pre {\r\n" + 
				"      white-space: pre-wrap;\r\n" + 
				"      margin-bottom: 5px;\r\n" + 
				"      margin-top: 5px;\r\n" + 
				"      padding: 5px 10px;\r\n" + 
				"    }\r\n" + 
				"    code {\r\n" + 
				"      font-size: 85%;\r\n" + 
				"      padding: 2px 4px;\r\n" + 
				"    }\r\n" + 
				"    pre.ql-syntax {\r\n" + 
				"      background-color: #23241f;\r\n" + 
				"      color: #f8f8f2;\r\n" + 
				"      overflow: visible;\r\n" + 
				"    }\r\n" + 
				"    img {\r\n" + 
				"      max-width: 100%;\r\n" + 
				"    }\r\n" + 
				"    p, ol, ul, pre, blockquote, h1, h2, h3, h4, h5, h6 {\r\n" + 
				"\r\n" + 
				"      margin: 0;\r\n" + 
				"      padding: 0;\r\n" + 
				"  }\r\n" + 
				"\r\n" + 
				"  .ql-align-center {\r\n" + 
				"    text-align: center;\r\n" + 
				"}\r\n" + 
				".ql-size-huge {\r\n" + 
				"  font-size: 2.5em;\r\n" + 
				"}\r\n" + 
				".ql-align-right {\r\n" + 
				"  text-align: right;\r\n" + 
				"}\r\n" + 
				"    @page {\r\n" + 
				"\r\n" + 
				"      margin : 0;\r\n" + 
				"      padding : 0.51in;\r\n" + 
				"    }\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"    \r\n" + 
				"</style>\r\n" + 
				"</head>\r\n" + 
				"<body class=\"ql-snow ql-editor\" style=\"padding:0;margin:0;\">\r\n" + 
				"\r\n" + 
				    html      +"\r\n" + 
				"</body>\r\n" + 
				"</html>";
	return baseDoc;
	}

	String converted="";
	@PostMapping()
	public HashMap<String, String> generatePdfFile(@RequestBody(required = false) List<String> htmlContent) {
		HashMap<String, String> mapper = new HashMap<String, String>();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ConverterProperties properties = new ConverterProperties();
		 
		com.itextpdf.layout.font.FontProvider fontProvider = new com.itextpdf.layout.font.FontProvider();
	    properties.setFontProvider(fontProvider);
		
	    com.itextpdf.kernel.pdf.PdfDocument pdf = new com.itextpdf.kernel.pdf.PdfDocument(new com.itextpdf.kernel.pdf.PdfWriter(baos));
	    pdf.setDefaultPageSize(com.itextpdf.kernel.geom.PageSize.A4);
	    converted="";
        try {
        	htmlContent.forEach(elem -> {
        		
        		
        		
        		converted+=constructPDF(elem);
        	});
        	
        	
       /* 	FileWriter writer = new FileWriter("r:/h.html");
        	writer.write(converted);
        	writer.close();
		*/
			HtmlConverter.convertToPdf(converted, baos,  null);
			
			
			 
			String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());
			mapper.put("base64", base64);
            return mapper;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	@PostMapping(path = "/reports/bordereau")
	public ResponseEntity<Map<String,String>> genDoc(@RequestBody FolderClass courrier,@RequestHeader(name = "secondary") String secondary) throws ColumnBuilderException, ClassNotFoundException, JRException, IOException {
		
         
	    JasperPrint print =  reportsService.genReports(courrier,secondary); 
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    HashMap<String, String> exportedItem = new HashMap<String, String>(); 
	    JasperExportManager.exportReportToPdfStream(print, baos);
	    
	    baos.close();
	    ByteArrayInputStream inStream = new ByteArrayInputStream( baos.toByteArray() );
	    exportedItem.put("file", Base64.getEncoder().encodeToString(baos.toByteArray()));



	    InputStreamResource res = new InputStreamResource(inStream);
	    return ResponseEntity.ok(exportedItem);

	  //  JasperExportManager.exportReportToPdfFile(print, path);
	}
	
	@PostMapping(path = "/reports/bordereau/xl")
    public ResponseEntity<Map<String,String>> genDocXl(@RequestBody FolderClass courrier,@RequestHeader(name = "secondary") String secondary) throws ColumnBuilderException, ClassNotFoundException, JRException, IOException {
        
         
        JasperPrint print =  reportsService.genReports(courrier,secondary); 
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HashMap<String, String> exportedItem = new HashMap<String, String>(); 
       
        JRXlsxExporter exporter = new JRXlsxExporter();
        SimpleXlsxReportConfiguration reportConfigXLS = new SimpleXlsxReportConfiguration();
        reportConfigXLS.setSheetNames(new String[] { "sheet1" });
        reportConfigXLS.setOnePagePerSheet(false);
        reportConfigXLS.setRemoveEmptySpaceBetweenRows(true);
        exporter.setConfiguration(reportConfigXLS);
        exporter.setExporterInput(new SimpleExporterInput(print));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));

        exporter.exportReport();
        
        baos.close();
        ByteArrayInputStream inStream = new ByteArrayInputStream( baos.toByteArray() );
        exportedItem.put("file", Base64.getEncoder().encodeToString(baos.toByteArray()));



        InputStreamResource res = new InputStreamResource(inStream);
        return ResponseEntity.ok(exportedItem);

      //  JasperExportManager.exportReportToPdfFile(print, path);
    }
	
   
	@Autowired
	PrintService printService;
	@PostMapping("/pdf")
    public HashMap<String, String> generatePdfFile2(@RequestBody(required = false) List<String> htmlContent) {
    System.err.println("enter");
    HashMap<String, String> mapper = new HashMap<String, String>();
    try {
            mapper.put("base64", printService.generatePDFBase64(htmlContent));
            return mapper;
           
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
       
   
}
	
}
