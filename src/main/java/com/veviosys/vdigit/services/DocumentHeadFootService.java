package com.veviosys.vdigit.services;

import java.util.List;
import java.util.Objects;

import com.veviosys.vdigit.models.DocumentsHeadAndFooter;
import com.veviosys.vdigit.repositories.DocumentHeadFootRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocumentHeadFootService {

	
	@Autowired private DocumentHeadFootRepository headFootRepository;
	
	public List<DocumentsHeadAndFooter> getDocumenHeadFoot(Long master) {
		
		
		return headFootRepository.findByMasterId(master);
	}

	public DocumentsHeadAndFooter updateHeader(Long userId, String newHeader) {
		// TODO Auto-generated method stub
		
		DocumentsHeadAndFooter andHeaderFooter = headFootRepository.findByMasterIdAndType(userId,'h');
		if(Objects.isNull(andHeaderFooter))
			{
			  andHeaderFooter = new DocumentsHeadAndFooter();
			  andHeaderFooter.setHtml(newHeader);
			  andHeaderFooter.setType('h');
			  andHeaderFooter.setMasterId(userId);
			  return headFootRepository.saveAndFlush(andHeaderFooter);
			}
		
		else {
			
			andHeaderFooter.setHtml(newHeader);
			 return headFootRepository.saveAndFlush(andHeaderFooter);
		}
		
	
	}
	
	public DocumentsHeadAndFooter updateFooter(Long userId, String newHeader) {
		// TODO Auto-generated method stub
		
		DocumentsHeadAndFooter andHeaderFooter = headFootRepository.findByMasterIdAndType(userId,'f');
		if(Objects.isNull(andHeaderFooter))
			{
			  andHeaderFooter = new DocumentsHeadAndFooter();
			  andHeaderFooter.setHtml(newHeader);
			  andHeaderFooter.setType('f');
			  andHeaderFooter.setMasterId(userId);
			  return headFootRepository.saveAndFlush(andHeaderFooter);
			}
		
		else {
			
			andHeaderFooter.setHtml(newHeader);
			 return headFootRepository.saveAndFlush(andHeaderFooter);
		}
		
	
	}
}
