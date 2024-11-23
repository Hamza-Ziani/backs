package com.veviosys.vdigit.bulkadd.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity

public class LotGroupBlob {

	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long blobId;
	
	@JsonIgnore
	@ManyToOne()
	private LotGroup lotGroup;
	
	private Long pIndex;
	
	 @Lob
	 private String data;

	
}
