package com.veviosys.vdigit.models;

import javax.annotation.Generated;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data@AllArgsConstructor
@NoArgsConstructor
public class DocumentsHeadAndFooter {
    
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private Long masterId;
	
	private char type;
	
	private @Lob String html;
	
	
}
