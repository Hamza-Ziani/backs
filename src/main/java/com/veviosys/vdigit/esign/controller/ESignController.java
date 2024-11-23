package com.veviosys.vdigit.esign.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.DocumentException;
import com.veviosys.vdigit.esign.exeption.ExpireExecption;
import com.veviosys.vdigit.esign.exeption.InvalidException;
import com.veviosys.vdigit.esign.exeption.NotFoundException;
import com.veviosys.vdigit.esign.model.EsignRequest;
import com.veviosys.vdigit.esign.service.ESignService;

import lombok.Data;

@RestController
@RequestMapping(path = "api/v1/esign")
public class ESignController {

	@Autowired
	private ESignService esignService;
	
	
	   @GetMapping("request/{documentId}")
	    public ResponseEntity SignReqeust(@PathVariable("documentId") UUID documentId,float xPos,float yPos,float h,float w,int p) {
	        
	        HashMap<String, String> ex = new HashMap<String, String>();
	            try {
	                return new ResponseEntity(esignService.signReqeust(documentId, xPos, yPos, h, w,p),HttpStatus.OK);
	            } catch (AddressException e) {
	                ex.put("ex", "ADDRESS_MAIL");
	            } catch (MessagingException e) {
	                ex.put("ex", "MESSAGING");
	            } catch (IOException e) {
	                ex.put("ex", "IO");
	            } catch (NotFoundException e) {
	                ex.put("ex", "DOCUMENT_NOT_FOUND");
	            }   
	        
	            return new ResponseEntity(ex, HttpStatus.EXPECTATION_FAILED);
	        
	        
	        
	    }
	   
//	@GetMapping("request/{documentId}/{folderRef}")
//	public ResponseEntity SignReqeust(@PathVariable("documentId") UUID documentId,@PathVariable("folderRef") String folderRef,float xPos,float yPos,float h,float w,int p) {
//		
//		HashMap<String, String> ex = new HashMap<String, String>();
//			try {
//				return new ResponseEntity(esignService.signReqeust(documentId,folderRef, xPos, yPos, h, w,p),HttpStatus.OK);
//			} catch (AddressException e) {
//				ex.put("ex", "ADDRESS_MAIL");
//			} catch (MessagingException e) {
//				ex.put("ex", "MESSAGING");
//			} catch (IOException e) {
//				ex.put("ex", "IO");
//			} catch (NotFoundException e) {
//				ex.put("ex", "DOCUMENT_NOT_FOUND");
//			}   
//		
//			return new ResponseEntity(ex, HttpStatus.EXPECTATION_FAILED);
//		
//		
//		
//	}
	
	@GetMapping("request/{reqId}/resend")
	public ResponseEntity ResendSignReqeust(@PathVariable("reqId") UUID reqId) {
		
		HashMap<String, String> ex = new HashMap<String, String>();
			try {
				return new ResponseEntity(esignService.resendSignReqeust(reqId),HttpStatus.OK);
			} catch (AddressException e) {
				ex.put("ex", "ADDRESS_MAIL");
			} catch (MessagingException e) {
				ex.put("ex", "MESSAGING");
			} catch (IOException e) {
				ex.put("ex", "IO");
			}
			catch (ExpireExecption e) {
				ex.put("ex", e.getMessage());
			}   
		
			return new ResponseEntity(ex, HttpStatus.EXPECTATION_FAILED);
		
		
		
	}
	
	@PostMapping("request/{reqId}/verify")
	public ResponseEntity SignReqeustVerify(@PathVariable("reqId") UUID requestId,@RequestParam("sign") MultipartFile sign,@RequestParam("code") String code) {
		
            HashMap<String, String> ex = new HashMap<String, String>();
			try {
				return  new ResponseEntity(HttpStatus.OK).ok(esignService.signVerify(requestId,code,sign.getBytes()))   ;
			} catch (IOException e) {
				ex.put("ex", e.getMessage());
				
			} catch (DocumentException e) {
				ex.put("ex", e.getMessage());
			} catch (ExpireExecption e) {
				ex.put("ex", e.getMessage());
			} catch (InvalidException e) {
				ex.put("ex", e.getMessage());
			} catch (NotFoundException e) {
				ex.put("ex", e.getMessage());
			}
			
			return new ResponseEntity(ex, HttpStatus.EXPECTATION_FAILED);

		
		
		
	}
	
	

	
	
	
	
}


