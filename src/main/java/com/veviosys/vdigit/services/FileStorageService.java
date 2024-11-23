package com.veviosys.vdigit.services;

import java.util.UUID;

import com.itextpdf.io.IOException;
import com.itextpdf.text.DocumentException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface FileStorageService {
	
	  public void init();

	  public void  save(MultipartFile file,UUID id,int convert,int codebr, float x, float y, float h, float w,String val,int type,MultipartFile img) throws IOException, DocumentException, java.io.IOException;
	 
	  
	  
	  public void  saveFile(MultipartFile file,UUID id,int convert,int codebr, float x, float y, float h, float w,String val,int type,MultipartFile img) throws IOException, DocumentException, java.io.IOException;
		 
	  //public void save(MultipartFile file,UUID id,int convert,int codebr, float x, float y, float h, float w,String val,int type) throws IOException, DocumentException, java.io.IOException;
 
	  public void delete(String fildeName,UUID id);



}
