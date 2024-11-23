package com.veviosys.vdigit.bulkadd.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.veviosys.vdigit.bulkadd.model.Lot;
import com.veviosys.vdigit.bulkadd.model.LotClass;
import com.veviosys.vdigit.bulkadd.model.LotDataClass;
import com.veviosys.vdigit.bulkadd.model.LotGroup;
import com.veviosys.vdigit.bulkadd.model.LotGroupBlob;
import com.veviosys.vdigit.bulkadd.model.LotGroupClass;
import com.veviosys.vdigit.bulkadd.repositories.LotGroupBlobRepo;
import com.veviosys.vdigit.bulkadd.repositories.LotGroupRepo;
import com.veviosys.vdigit.bulkadd.repositories.LotRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@RestController
@RequestMapping("api/v1/bulkadd")
public class BulkAddController {

	@Autowired
	LotRepository lotrepo;

	@Autowired
	LotGroupRepo loGrepo;

	@Autowired
	LotGroupBlobRepo lotGroupBlobRepo;

	
	
	@GetMapping("/all")
	public Page<Lot> GetAllLots(Pageable page){
	   
		
		return lotrepo.findBySubmitedTrue(page);
		
		
	}
	
	@PostMapping("/new")
	public  Lot PostNewLot(@RequestBody LotClass lotObj)  {
		System.err.println(lotObj);
		Lot lot = new Lot(null,lotObj.gName,null,lotObj.gDocTypes,lotObj.commonAttrsVal,lotObj.lotType,false,0L,LocalDateTime.now(),lotObj.lotGroupName);
return lotrepo.save(lot);
		
	
		
	}
	
	@Transactional
	@PostMapping("/{lot}/push")
	public  LotGroup  PostNewLotGroup(@RequestBody LotGroupClass group, @PathVariable("lot") Long lot)  {
		
		
		try {
			LotGroup lg = loGrepo.save(new LotGroup(null, group.groupName, null, group.groupType, lotrepo.getOne(lot)));
		
			Lot lotAdd = lotrepo.getOne(lot);
			lotAdd.setGroupsCount(lotAdd.getGroupsCount() +1);
			
			lotrepo.save(lotAdd);
			
			return lg;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
		
		
		
		
		

	}
	Long indexs= 0L;
	@Transactional
	@PostMapping("{lotGr}/upload")
	public List<LotGroupBlob>  PostNewLotFiles(@RequestBody List<LotDataClass> files , @PathVariable("lotGr") Long lotGr) throws IOException {

		List<LotGroupBlob> lt = new ArrayList<>();
		LotGroup lG = loGrepo.getOne(lotGr);
		files.forEach(img -> {
			
			lt.add(lotGroupBlobRepo.save(new LotGroupBlob(null,lG,indexs++,img.image)));				
		});
		indexs = 0L;
		
		Lot lot = lG.getLId();
		
		lot.setSubmited(true);
		lotrepo.save(lot);
		
		
		return lt;
		
		
		
	}

}
