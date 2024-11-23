package com.veviosys.vdigit.models;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class DocumentFullText {
	
	@Id
	@org.hibernate.annotations.Type(type="uuid-char")
	private UUID id;
	
	@Column(nullable = false)
    @Lob
	private String fullText; 
	
	private Long masterId;
	
	private Long typeId;
	
	
	
	

}
