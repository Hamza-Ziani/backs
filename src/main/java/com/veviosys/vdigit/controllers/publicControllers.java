package com.veviosys.vdigit.controllers;


import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import com.veviosys.vdigit.classes.File64;
import com.veviosys.vdigit.classes.mapClass;
import com.veviosys.vdigit.configuration.DynamicColumnDataSource;
import com.veviosys.vdigit.configuration.DynamicReportBuilder;
import com.veviosys.vdigit.integrationCm.services.IntegrationService;
import com.veviosys.vdigit.models.ClientDoc;
import com.veviosys.vdigit.models.Journal;
import com.veviosys.vdigit.models.MasterConfig;
import com.veviosys.vdigit.models.SupportTech;
import com.veviosys.vdigit.models.Track;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.repositories.CloneEtapeRepo;
import com.veviosys.vdigit.repositories.DocumentFolderRepo;
import com.veviosys.vdigit.repositories.FolderRepo;
import com.veviosys.vdigit.repositories.JournalRepo;
import com.veviosys.vdigit.repositories.MasterConfigRepository;
import com.veviosys.vdigit.repositories.TrackRepo;
import com.veviosys.vdigit.repositories.UserRepository;
import com.veviosys.vdigit.services.AlimentationService;
import com.veviosys.vdigit.services.CaptureMailService;
import com.veviosys.vdigit.services.CostumUserDetailService;
import com.veviosys.vdigit.services.FileStorageServiceImpl;
import com.veviosys.vdigit.services.IldapService;
import com.veviosys.vdigit.services.MasterConfigService;
import com.veviosys.vdigit.services.ReportsService;
import com.veviosys.vdigit.services.SeconnderService;
import com.veviosys.vdigit.services.mailService;
import com.veviosys.vdigit.services.masterService;
import com.veviosys.vdigit.services.userService;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilderException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
public class publicControllers {

    @Autowired
    AlimentationService as;
    @Autowired
    FileStorageServiceImpl fileService;
    @Autowired
    AlimentationService alimentationService;
    
    @Autowired
    IldapService serviceLdap;
    
    @Autowired
    UserRepository ur;
    
    @Autowired
    MasterConfigRepository configRepository;
    
    @Autowired masterService ms;
    @Autowired
	private userService userService;
    @Autowired
    private TrackRepo trackedRepo;
    @Autowired
    private SeconnderService seconnderService;
    @Autowired
    private masterService mst;
    @Autowired
    private IntegrationService inter;
    @Autowired
    private JournalRepo jr;
    
    @GetMapping("/resetpw")
    public ResponseEntity reset(@RequestParam String email) throws AddressException, MessagingException, IOException {

        return mst.resetPassword(email);
    }
    
    private MasterConfig getMasterConfig(User r) {
        return configRepository.findByMasterUserIdAndConfigName(r.getUserId(), "LDAP_CONFIG");
    }
    
    @GetMapping("/configuration/{name}")
    public MasterConfig confugration(@PathVariable String name) {
            return configService.getMasterConfigByNameCapture(name);
    }
    
    @GetMapping("/api/file/zip/{id}")
    public ResponseEntity<String> existFile(@PathVariable UUID id) {
        return ResponseEntity.ok(as.checkifexist(id));
    }

    @GetMapping("/api/checkuser")
    public ResponseEntity<User> checkuser(@RequestParam(name = "code") String code) {
      
        String auth1 = code;
        byte[] decodedBytes = Base64.getDecoder().decode(auth1);
        String decodedString = new String(decodedBytes);
        String username = decodedString.split(":")[0];
        String password = decodedString.split(":")[1];
        
       User u = ur.findByUsernameIgnoreCase(username);
        
       if(Objects.nonNull(u)) {
           if(u.getFromLdap() == 1) {
               if (serviceLdap.logToLdap(username,password, getMasterConfig(u.getMaster()))) {             
                   return new ResponseEntity<User>(u,HttpStatus.OK);
               } else {                             
                   return new ResponseEntity<User>(HttpStatus.UNAUTHORIZED);
               }
           }else {
               return new ResponseEntity<User>(u,HttpStatus.OK);
           }  
       }else {
           return new ResponseEntity<User>(HttpStatus.UNAUTHORIZED); 
       }
       
    }

    
    @GetMapping("api/file/zip/download/{id}/{pw}")
    public File64 getB64File(@PathVariable UUID id,@PathVariable String pw) throws IOException 
    {  //.println(pw);
        return alimentationService.checkPwAndGetFile(id, pw);
    }

    @GetMapping("/api/getmasters")
	public List<mapClass> getMasters() {
		return userService.getMasters();
	}
    @PostMapping(value="/api/storage/support/edit/path/{state}")
    public ResponseEntity editPath(@RequestBody SupportTech tech,@PathVariable int state) {
        //TODO: process POST request
        
        return ms.confirmeEditPath(tech, state);
    }
    @GetMapping(value="/api/storage/support/testexist/{id}/{key}")
    public ResponseEntity testExistParams(@PathVariable Long id,@PathVariable String key)
    {
        return ms.testExist(id, key);
    }
    
    @PostMapping(value = "/api/storage/support/login")
    public ResponseEntity testLogin(@RequestBody SupportTech tech)
    {
     return  ms.testLogin(tech);
    }
    @Autowired mailService mss;
    @Autowired CloneEtapeRepo cer; 
    @Autowired FolderRepo fr;
    @Autowired DocumentFolderRepo dfr; 
    @Autowired CaptureMailService captureMailService;
    @GetMapping(value="/tstmail")
    public void tst( ) throws IOException, JRException, ColumnBuilderException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, InstantiationException
    { 
    	try {
			captureMailService.readData();
		} catch (MessagingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	/*
    	Folder f=new Folder();

          f=fr.findById(UUID.fromString("17062762-f2da-4889-b06a-47371d2a1beb")).orElse(null);
        
      	List<DocumentFolder> docsF = f.getDocuments();
 

	List<String> jsonDocs = new ArrayList<>();
			Gson o=new Gson();  
			
      	for (DocumentFolder documentFolder : docsF) {

HashMap<String,String>data=new HashMap<>();
			for (DocumentAttributeValue dav : documentFolder.getDocument().getAttributeValues()) {
				data.put(dav.getAttribute().getLibelle(),dav.getValue().getValue());
			}
			String _infos="",asciisString=""; 
		_infos=o.toJson(data);
			  for (int i = 0; i < _infos.length(); i++) {
				  asciisString+=(int)_infos.charAt(i)+5;
				  if(i!=_infos.length()-1)
				  asciisString+=',';
				   }
			jsonDocs.add(Base64.getEncoder().encodeToString(asciisString.getBytes(StandardCharsets.UTF_8)));

   
      	}   	*/
       
    	try {
    	 
    		
 
    		/* String input="E:\\Standard version\\lot branch\\2.xlsx";
    		String output="E:\\Standard version\\lot branch\\1.pdf";InputStream docxInputStream = new FileInputStream(input);
   	               OutputStream pdfOutputStream = new FileOutputStream(output);  

   	              IConverter converter = LocalConverter.builder().build();

   	              converter.convert(docxInputStream).as(DocumentType.MS_EXCEL)
   	                      .to(pdfOutputStream).as(DocumentType.PDF)
   	                      .execute();

   	              converter.shutDown();*/
    		
    		/* 
            //Create a Workbook instance
            Workbook workbook = new Workbook();
            //Load an Excel file
            workbook.loadFromFile(input);

            //Fit all worksheets on one page (optional)
            workbook.getConverterSetting().setSheetFitToPage(true);

            //Save the workbook to PDF
            workbook.saveToFile(output, FileFormat.PDF);*/
    		/* OK
    	 	OfficeManager officeManager
            = LocalOfficeManager.builder()
                    .officeHome("R:\\Programs\\OpenOffice 4")
                    .portNumbers(2372)
                    .build();
    officeManager.start();
			   File initialFile = new File("E:\\Standard version\\lot branch\\2.docx");
			    InputStream targetStream = new FileInputStream(initialFile);
			    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			    FileOutputStream fos = new FileOutputStream(new File("E:\\Standard version\\lot branch\\1.pdf"));
		        final DocumentConverter converter = LocalConverter.builder()
		                .officeManager(officeManager)
		                .build();
		        converter
             .convert(targetStream)
             .to(outputStream)
             .as(DefaultDocumentFormatRegistry.getFormatByExtension("pdf"))
             .execute();
		        outputStream.writeTo(fos);
		        officeManager.stop();*/
    		
    		/* 2
            InputStream templateInputStream = new FileInputStream("E:\\Standard version\\lot branch\\2.docx");
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(templateInputStream);
            MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();

            String outputfilepath = "E:\\Standard version\\lot branch\\1.pdf";
            FileOutputStream os = new FileOutputStream(outputfilepath);
            Docx4J.toPDF(wordMLPackage,os);
            os.flush();
            os.close();*/
    		
 /*   	       try (InputStream docxInputStream = new FileInputStream(input);
    	               OutputStream pdfOutputStream = new FileOutputStream(output)) {

    	              IConverter converter = LocalConverter.builder().build();

    	              converter.convert(docxInputStream).as(DocumentType.XLSX)
    	                      .to(pdfOutputStream).as(DocumentType.PDF)
    	                      .execute();

    	              converter.shutDown();
    	       }*/
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

    }

    
    
    @GetMapping(value = "/tst/file")
    public List<Journal> testLogin() 
    {
        return null;
        

    }
    
    public static boolean set(Object object, String fieldName, Object fieldValue) {
        Class<?> clazz = object.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(object, fieldValue);
                return true;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return false;
    }
    @Autowired 
    ReportsService reportsService;
    @Autowired
    MasterConfigService configService;
  
     	// ADD Emetteur
	@PostMapping("/clientdoc/add/{mst}/{nom}/{prenom}/{num}/{titre}/{objet}/{mail}/{message}/{code}")
	public ClientDoc addEmm(@PathVariable Long mst, @PathVariable String nom, @PathVariable String prenom,
			@PathVariable String num, @PathVariable String titre, @PathVariable String objet, @PathVariable String mail,
			@PathVariable String message) {
		// @RequestBody ClientDoc d

	    
	    
	    
	    
		return as.addClientDoc(mst, mail, message, nom, prenom, num, titre, objet);
	}

	// ADD DOCUMENT BY CLIENT
	@PostMapping("/clientdoc/add/doc/{mst}/{clt}/{file}")
	public Map<String, String> addDocByClient(@PathVariable String file, @PathVariable Long mst,
			@PathVariable Long clt) {
		return as.addDocByClient(file, mst, clt);
	}

	@PostMapping(path = "/clientdoc/documentfile/{id}")
	public ResponseEntity postDocClientAndFile(@RequestParam("file") MultipartFile file, @PathVariable UUID id) {

		try { 

			as.saveClientDoc(id, file);

		} catch (IOException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		// return new Document();
		return new ResponseEntity<>(HttpStatus.OK);
		// return service.GetFolder();
	}
	
	
	public void runReport(List<String> columnHeaders, List<List<String>> rows) throws JRException {

		
		InputStream is = getClass().getResourceAsStream("../../../DynamicColumns.jrxml");
		 JasperDesign jasperReportDesign = JRXmlLoader.load(is);
		DynamicReportBuilder reportBuilder = new DynamicReportBuilder(jasperReportDesign, columnHeaders.size());
		 reportBuilder.addDynamicColumns();
		JasperReport jasperReport = JasperCompileManager.compileReport(jasperReportDesign);
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("REPORT_TITLE", "Sample Dynamic Columns Report");
		DynamicColumnDataSource pdfDataSource = new DynamicColumnDataSource(columnHeaders, rows);
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, pdfDataSource);
		JasperExportManager.exportReportToPdfFile(jasperPrint, "DynamicColumns.pdf");
		 }
	
	@GetMapping("/public/validity/check/{name}")
    public int checkValidity(@PathVariable String name) {
    	return userService.checkValidity(name);
    }

	@GetMapping("/secondary/userlogin/{username}/{password}")
	    public ResponseEntity<User>  getSecondaryUser(@PathVariable("username") String username,@PathVariable("password") String password) {
	        return  seconnderService.getSecondaryUserLogin(username,password);
    }
	
	
	
	@GetMapping(path = "/readEmail/{id}")
    public ResponseEntity  TrackreadEmail(@PathVariable Long id) {
	     
	    
        Optional<Track> tracked = trackedRepo.findById(id);
        
        if(tracked.isPresent()) {
            
            tracked.get().setRead(1);
            
            trackedRepo.saveAndFlush(tracked.get());
            
            return new ResponseEntity<>(HttpStatus.OK);
        }
        
        return new ResponseEntity<>(HttpStatus.CONFLICT);

    }
	
	@GetMapping(path = "/saveTrackedLink/{id}")
    public ResponseEntity  TrackreadEmailClicked(@PathVariable Long id) {
         
	    Optional<Track> tracked = trackedRepo.findById(id);
	    
        if(tracked.isPresent()) {
            
            tracked.get().setClicked(1);
            tracked.get().setRead(1);
            
            trackedRepo.saveAndFlush(tracked.get());
            
            return new ResponseEntity<>(HttpStatus.OK);
        }
        
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
@Autowired
MasterConfigService masterConfigService ;
}
