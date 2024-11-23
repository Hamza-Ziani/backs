package com.veviosys.vdigit.models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@javax.persistence.Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ElementTypeGroup {

	
	
	
	@Id
	@GeneratedValue(strategy =  GenerationType.AUTO)
	private Long goupId;
	
	@Column(length = 100)
	private String groupName;
	@Column(length = 100)
	private String groupLabel;
	@Column(length = 300)
	private String groupDesc;
	
	@Transient
	private String _documentTypes;
	@OneToMany(mappedBy="group",fetch = FetchType.LAZY)
	List<DocumentType> documentTypes;
	

	
   
	private Boolean isAutoNum;
	
	private Long seq = 0L;
	
	@ManyToOne (fetch = FetchType.LAZY)
	@JsonIgnore
	private User master;
	
	private boolean isStandard = false;

	
	
}
