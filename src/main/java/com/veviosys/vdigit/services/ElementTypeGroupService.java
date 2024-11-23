package com.veviosys.vdigit.services;

import java.lang.annotation.Documented;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.veviosys.vdigit.classes.mapSearch;
import com.veviosys.vdigit.models.Attribute;

import com.veviosys.vdigit.models.DocumentType;
import com.veviosys.vdigit.models.ElementTypeGroup;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.reposietories.ElementTypeGroupRepository;

import com.veviosys.vdigit.repositories.DocumentTypeRepo;

@Service
public class ElementTypeGroupService {

	
	
	@Autowired
	private DocumentTypeRepo documentTypeRepo;
	

	
	
	
	ElementTypeGroup getStandard() {
	
		
		return elementTypeGroupRepository.findAll(PageRequest.of(0, 1)).getContent().get(0);

	}
	
	
	CostumUserDetails getUser(){
		return (CostumUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}
	
	
	private @Autowired ElementTypeGroupRepository elementTypeGroupRepository;
	


	public ElementTypeGroup getOne(Long Id) {
		return elementTypeGroupRepository.findById(Id).get();
	}
	public ElementTypeGroup add(ElementTypeGroup elementTypeGroup) {
		
		
		elementTypeGroup.setMaster(getUser().getUser());
		
		String[] types =  elementTypeGroup.get_documentTypes().split(",");
		
		List<DocumentType> documentTypes = new ArrayList<DocumentType>();
		
		
		ElementTypeGroup eg = elementTypeGroupRepository.saveAndFlush(elementTypeGroup);
		
		
		
		DocumentType dt =null;
		
		List<Attribute> allAttributes = new ArrayList<>();
		
		
		
		
		
		
		for (int i = 0; i < types.length; i++) {
		
			dt=documentTypeRepo.findById(Long.valueOf(types[i])).get();
			
			
			dt.setGroup(eg);
			documentTypeRepo.save(dt);
		
			
			
		}
		
		
		elementTypeGroup.setDocumentTypes(documentTypes);
		
		return elementTypeGroupRepository.findById(eg.getGoupId()).get();
	}



	public void delete(Long id) {
		// TODO Auto-generated method stub
		elementTypeGroupRepository.deleteById(id);
	}



	public Page<ElementTypeGroup> getAll(Pageable pageable, String q) {
		// TODO Auto-generated method stub
		return (Page<ElementTypeGroup>) elementTypeGroupRepository.findByGroupLabelContainingIgnoreCase(q,pageable);
	}
	
	public List<ElementTypeGroup> getAllWithoutPage() {
        // TODO Auto-generated method stub
        return (List<ElementTypeGroup>) elementTypeGroupRepository.findAll();
    }
	
	
	public List<ElementTypeGroup> getAll() {
		// TODO Auto-generated method stub
		return elementTypeGroupRepository.findAll();
	}

	public List<Attribute> getCommonAttrs(Long id) {
		// TODO Auto-generated method stub
		ElementTypeGroup elementTypeGroup = elementTypeGroupRepository.getOne(id);
		
		List<Attribute> commonAttrs = new ArrayList<>();
		List<Attribute> attrs = new ArrayList<>();
		List<Attribute> firstList = new ArrayList<>();
		
		
		List<DocumentType> documentTypes =  elementTypeGroup.getDocumentTypes();
		
		Long i = 0L;
		for (DocumentType documentType : documentTypes) {
			 
			if(i.equals(0L))
				firstList.addAll(documentType.getAttributes());
		  	attrs.addAll(documentType.getAttributes());
			i++;
		}
		
		
		for (Attribute attribute : firstList) {
		
			int count=Collections.frequency(attrs, attribute);
			if((new Long(count)).equals(i))
					commonAttrs.add(attribute);
			
			
		}

		
		return commonAttrs;
	}
	
	public List<Attribute>	getCommonAttrsByTypes(List<Long>types) throws SQLException{
	 	
		List<Attribute> commonAttrs = new ArrayList<>();
		List<Attribute> attrs = new ArrayList<>();
		List<Attribute> firstList = new ArrayList<>();
		
		
		// List<DocumentType> documentTypes =  elementTypeGroup.getDocumentTypes();
		
		Long i = 0L;
		for (Long id  : types) {
			DocumentType	documentType=documentTypeRepo.findById(id).orElse(null);
			if(i.equals(0L))
				firstList.addAll(documentType.getAttributes());
		  	attrs.addAll(documentType.getAttributes());
			i++;
		}
		
		
		for (Attribute attribute : firstList) {
		
			int count=Collections.frequency(attrs, attribute);
			if((new Long(count)).equals(i))
			{

				commonAttrs.add(attribute);
					}
			
			
		}

		
		return commonAttrs;
	}


	public void incrementSeq(ElementTypeGroup group) {
		
		group.setSeq(group.getSeq()+1L);
		elementTypeGroupRepository.save(group);
	}
	public ElementTypeGroup update(ElementTypeGroup elementTypeGroup) {
		
		
		
		

		ElementTypeGroup savedElementTypeGroup = elementTypeGroupRepository.getOne(elementTypeGroup.getGoupId());
		
		savedElementTypeGroup.setGroupDesc(elementTypeGroup.getGroupDesc());
		savedElementTypeGroup.setGroupName(elementTypeGroup.getGroupName());
		savedElementTypeGroup.setGroupLabel(elementTypeGroup.getGroupLabel());
		
		
		for (DocumentType i : savedElementTypeGroup.getDocumentTypes()) {
			 i = documentTypeRepo.getOne(i.getId());
			 i.setGroup(null);
			 documentTypeRepo.save(i);
		}
		savedElementTypeGroup.setDocumentTypes(elementTypeGroup.getDocumentTypes());
		
		for (DocumentType i : elementTypeGroup.getDocumentTypes()) {
			i = documentTypeRepo.getOne(i.getId());
			 i.setGroup(savedElementTypeGroup);
			 documentTypeRepo.save(i);
		}
		
		
		
		
		elementTypeGroupRepository.save(savedElementTypeGroup);

		
		return elementTypeGroup;
	}
	public ElementTypeGroup enalbeAutoNum(ElementTypeGroup one) {
		one.setIsAutoNum(true);
		
		one = elementTypeGroupRepository.save(one);
		return one;
	}
	public ElementTypeGroup disalbeAutoNum(ElementTypeGroup one) {
		one.setIsAutoNum(false);
		
		one = elementTypeGroupRepository.save(one);
		return one;
	}
	
	public ElementTypeGroup resetSeq(ElementTypeGroup one) {
		one.setSeq(0L);
		
		one = elementTypeGroupRepository.save(one);
		return one;
	}
	
	public ElementTypeGroup deleteElementGroup(ElementTypeGroup one) {
		
		for (DocumentType i : one.getDocumentTypes()) {
			 i = documentTypeRepo.getOne(i.getId());
			 i.setGroup(null);
			 documentTypeRepo.save(i);
		}
		
		elementTypeGroupRepository.delete(one);
		return one;
	}
	public void createStandard() {

		if(elementTypeGroupRepository.count()== new Long(0)) {
			
			ElementTypeGroup e = new ElementTypeGroup();
			e.setGroupDesc("Groupe standard");
			e.setGroupLabel("Standard");
			e.setGroupName("Standard");
			e.setIsAutoNum(true);
			e.setStandard(true);
			e=elementTypeGroupRepository.saveAndFlush(e);
			
	
	
			
			e=elementTypeGroupRepository.saveAndFlush(e);
			
			
			
		}
		

	}
	
	public Page<DocumentType> getAll(org.springframework.data.domain.Pageable pageable) {

		
		User u = getUser().getUser();
		Long idM;
		if (u.getMaster() == null) {
			idM = u.getUserId();
		} else {
			idM = u.getMaster().getUserId();
		}
		List<DocumentType> dt = documentTypeRepo.findByUser(getUser().getUser().getUserId(), idM);
		for (int i = 0; i < dt.size(); i++) {

			dt.get(i).setSelected(1);
		}
		int pos = dt.size();

		dt.addAll(documentTypeRepo.findOthers(getUser().getUser().getUserId(), idM));// =
																// dtr.findByUser(getUser().getUserId());
		for (int i = pos; i < dt.size(); i++) {
			dt.get(i).setSelected(0);
		}

		int start = (int) pageable.getOffset();
		int end = (int) (start + pageable.getPageSize()) > dt.size() ? dt.size() : (start + pageable.getPageSize());
		Page<DocumentType> pages = new PageImpl<DocumentType>(dt.subList(start, end), pageable, dt.size());

		return pages;
//		User u = getUser().getUser();
//		Long idM;
//		if (u.getMaster() == null) {
//			idM = u.getUserId();
//		} else {
//			idM = u.getMaster().getUserId();
//		}
//		List<DocumentType> dt = documentTypeRepo.findOthers(getUser().getUser().getUserId(), idM);
//
//		int start = (int) pageable.getOffset();
//		int end = (int) (start + pageable.getPageSize()) > dt.size() ? dt.size() : (start + pageable.getPageSize());
//		Page<DocumentType> pages = new PageImpl<DocumentType>(dt.subList(start, end), pageable, dt.size());
//
//		return pages;
	}

	
	
	
	
	
	
	
	
	
	
	
	
}
