package com.veviosys.vdigit.esign.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.itextpdf.layout.element.Table;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfSignatureAppearance.RenderingMode;
import com.veviosys.vdigit.esign.exeption.ExpireExecption;
import com.veviosys.vdigit.esign.exeption.InvalidException;
import com.veviosys.vdigit.esign.exeption.NotFoundException;
import com.veviosys.vdigit.esign.model.EsignRequest;
import com.veviosys.vdigit.esign.repository.ESignReqeustRepository;
import com.veviosys.vdigit.models.Document;
import com.veviosys.vdigit.models.DocumentAttributeValue;
import com.veviosys.vdigit.models.DocumentVersion;
import com.veviosys.vdigit.models.DocumentVersionAttributeValue;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.models.ValueVersion;
import com.veviosys.vdigit.models.pk.DocumentVesionAttributeValuePK;
import com.veviosys.vdigit.repositories.DocumentRepo;
import com.veviosys.vdigit.repositories.DocumentVersionAttributeValueRepo;
import com.veviosys.vdigit.repositories.DocumentVersionRepo;
import com.veviosys.vdigit.repositories.ValueVersionRepo;
import com.veviosys.vdigit.services.CostumUserDetails;
import com.veviosys.vdigit.services.FileStorageServiceImpl;
import com.veviosys.vdigit.services.mailService;

@Service
public class ESignService {

	
	
	@Autowired
	private mailService mailservice;
	@Autowired
	private DocumentRepo documentRepo;
	
	@Autowired
	private FileStorageServiceImpl fileStorageServiceImpl;
	
	@Autowired
	private ESignReqeustRepository eSignReqeustRepository;
	
	@Autowired
	DocumentVersionRepo dvr;
	
	@Autowired
	private DocumentVersionAttributeValueRepo dvavr;
	
	@Autowired
	private ValueVersionRepo vvr;

	@Value("${documania.esign.reqeust.validation}")
	private int validationTime;
	
	public static final String UploadRoot = System.getProperty("user.dir") + "/upload/versions";
	
	
	
	private User connectedUser() {
		return ((CostumUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
	}
	
	
    
   public EsignRequest signReqeust(UUID documentId,float xPos,float yPos,float h,float w,int p) throws AddressException, MessagingException, IOException, NotFoundException 
   {

        User reqeustFrom = connectedUser();
        Document doc = documentRepo.getOne(documentId);
        if(Objects.nonNull(doc))
        {
            
            EsignRequest esignRequest = new EsignRequest(documentId, reqeustFrom.getUserId(), xPos, yPos, h, w, reqeustFrom.getFullName(), reqeustFrom.getEmail(),p,validationTime,doc.getFileName());
            
        
            mailservice.sendSignReqeust(reqeustFrom.getFullName(), reqeustFrom.getEmail(), esignRequest.getSecrectCodeString(), esignRequest.getValidationTime(),doc.getFileName());
            esignRequest = eSignReqeustRepository.saveAndFlush(esignRequest);
            return esignRequest;    
            
        }
        
        throw new NotFoundException("NF");

            
       
       
   }


public EsignRequest signVerify(UUID requestId, String code,byte[] img) throws IOException, DocumentException, ExpireExecption, InvalidException, NotFoundException {

	
	
	User reqeustFrom = connectedUser();
	
    EsignRequest esignRequest = eSignReqeustRepository.findById(requestId).orElse(null);
 
    
    
    
    
    if (!Objects.isNull(esignRequest)) {
		
    	  if(esignRequest.getIsStillValid())
    	  {
    		  if (esignRequest.Verify(code)) {

    			  
    			  validSig(esignRequest.getDocumentId(),img,esignRequest.getXPos(),esignRequest.getYPos(),esignRequest.getSigPage(),esignRequest.getSigHeight(),esignRequest.getSigWidth(),"‪Temp/");
    			  
    			  eSignReqeustRepository.delete(esignRequest);
    		  }
    		  else {
    			  
    			  throw  new InvalidException("INV");
    		  }
    		  
    	  }
    	  else {
    		  throw  new ExpireExecption("EXP");
		}
    	
    	
    	
    	
    	
	}else {
		
		throw  new NotFoundException("NF");
	}
	
	
	
	

	
	
	return null;
}
	

private void validSig(UUID docId,byte[] img,float imgX, float imgY, int page, float pageH, float pageW,String tmp) throws IOException, DocumentException
{
	
	int lastV = 0;
	
	
	Path docPath = fileStorageServiceImpl.getDocumentFilePath(docId);
	Document dd = documentRepo.findById(docId).orElse(null);
	BufferedReader br = new BufferedReader(new FileReader(dd.getPathServer()));
	StringBuilder sb = new StringBuilder();
	String line = br.readLine();
	
	
	while (line != null) {
		sb.append(line);
		sb.append(System.lineSeparator());
		line = br.readLine();
	}
	String data = sb.toString();
	br.close();
	
	String SavePath = dd.getPathServer();
	
	PdfReader reader = new PdfReader(com.itextpdf.text.pdf.codec.Base64.decode(data));
	
	
	List<DocumentVersion> listVersion = dvr.findByDocument(dd);
	DocumentVersion dv = new DocumentVersion();
	if (listVersion != null) {
		for (DocumentVersion documentVersion : listVersion) {
			if (documentVersion.getNumVersion() > lastV) {
				lastV = documentVersion.getNumVersion();
			}
		}
	}
	lastV++;
	dv.setDocument(dd);
	dv.setEdit_date(new Date());
	dv.setEditedBy(connectedUser());
	dv.setNumVersion(lastV); 
	dv.setContentType(dd.getContentType());
	dv.setEditType("Document");
	String SavePath1 = fileStorageServiceImpl.getDocumentVersionSaveFilePath(docId,lastV);
	dv.setPathServer(SavePath1);
	dv = dvr.saveAndFlush(dv);
	
	
	
	for (DocumentAttributeValue doc : dd.getAttributeValues()) {

		DocumentVesionAttributeValuePK pkId = new DocumentVesionAttributeValuePK(dv.getId(),
				doc.getAttribute().getId());
		DocumentVersionAttributeValue dvav = new DocumentVersionAttributeValue();

		ValueVersion vv = new ValueVersion();
		vv.setValue(doc.getValue().getValue());
		dvav.setId(pkId);
		vv = vvr.saveAndFlush(vv);
		dvav.setValue(vv);
		dvav.setDocument(dv);

		dvav.setAttribute(doc.getAttribute());

		dvavr.save(dvav);
	}
	
	
	Document doc = documentRepo.getOne(docId);

	File f = docPath.toFile();
	Files.copy(docPath, new FileOutputStream(SavePath1));
	String clonePath=SavePath.split("[.]")[0]+".pdf";
	File fclone=new File(clonePath);
	fclone.createNewFile();
	
	FileOutputStream os = new FileOutputStream(clonePath);
	PdfStamper stamper = new PdfStamper(reader, os);
	
	
	
	float offset = 0;
	Rectangle pagesize = reader.getPageSize(1);

	float h = pagesize.getHeight();
	float w = pagesize.getWidth();
	
	offset = ((h / pageH) + (w / pageW)) / 2;
	
	
	com.itextpdf.text.Image signImg = com.itextpdf.text.Image.getInstance(img);
	float llx = (imgX * offset);
	float lly = (h - (imgY * offset) - (signImg.getHeight() * offset));
	float urx = llx + (signImg.getWidth() * offset);
	float ury = lly + (signImg.getHeight() * offset);

	signImg.scaleAbsoluteHeight(signImg.getHeight() * offset);
	signImg.scaleAbsoluteWidth(signImg.getWidth() * offset);
	
	signImg.setAbsolutePosition(llx, lly);
	PdfContentByte overContent = stamper.getOverContent(page); 
	 overContent.addImage(signImg);
	 stamper.close();
	
	 FileInputStream is = new FileInputStream(clonePath);
	 byte[] array = new byte[is.available()];
	   is.read(array);
	   
		byte[] fileContent =array;
		is.close();	
		String datab64 = new String(Base64.getEncoder().encode(fileContent));
		FileWriter myWriter = new FileWriter(doc.getPathServer());
	 
		myWriter.write(datab64);
		myWriter.close();
		fclone.delete();

}


public EsignRequest resendSignReqeust(UUID requestId) throws AddressException, MessagingException, IOException, ExpireExecption {
    User reqeustFrom = connectedUser();
	
    EsignRequest esignRequest = eSignReqeustRepository.findById(requestId).orElse(null);
    if (esignRequest.getIsStillValid()) {
		
    	throw new ExpireExecption("NOT_EXPIRED");
	}
    
    
    try {
		esignRequest.generate2FACode();
	} catch (NoSuchAlgorithmException e) {

	}

    esignRequest.setValidationDate();
    esignRequest = eSignReqeustRepository.saveAndFlush(esignRequest);
    
    mailservice.sendSignReqeust(reqeustFrom.getFullName(), reqeustFrom.getEmail(), esignRequest.getSecrectCodeString(), esignRequest.getValidationTime(),esignRequest.getDocumentName());
	esignRequest = eSignReqeustRepository.saveAndFlush(esignRequest);
    
    
    return esignRequest;

}





}
