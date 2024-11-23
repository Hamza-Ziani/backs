package com.veviosys.vdigit.bulkadd.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.veviosys.vdigit.bulkadd.repositories.LotGroupRepo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lot {

	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long gId;
	
	private String gName;
	
	private Long imgsLength;
	
	private String gDocTypes;
	
	
	@Lob
	private String  commonAttrsVal;
	
	
	
	private Long lotType;
	
	private boolean submited = false; 
	
	
	
	
	private Long groupsCount;
	
	
	@JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
	private LocalDateTime addDate = LocalDateTime.now();

	private String lotGroupName; 
		
}
