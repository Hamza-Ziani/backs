package com.veviosys.vdigit.models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentVersion {
	
	@Id 
	@GeneratedValue(strategy =  GenerationType.AUTO)
	private Long id;
	
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
	@JsonIdentityReference(alwaysAsId = true)
   	@ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;
   	private int NumVersion;
    
	@OneToMany(mappedBy = "document")
	private List<DocumentVersionAttributeValue> attributeValues;
	@Temporal(TemporalType.DATE)
	private Date edit_date;

	@ManyToOne
	@JoinColumn(name = "edited_by")
	private User editedBy;
	private String editType;
	private String pathServer;
	@Lob
	private String HTML_CONTENT;
	private String contentType;
} 
