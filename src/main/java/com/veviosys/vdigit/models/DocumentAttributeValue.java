package com.veviosys.vdigit.models;


import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.veviosys.vdigit.models.pk.DocumentAttributeValuePK;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "document_attribute_value")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DocumentAttributeValue {

	    @JsonIgnore
	   	@EmbeddedId
	    private DocumentAttributeValuePK id;
	   	@JsonIgnore
	   	@ManyToOne
	    @MapsId("document_id") 
	    @JoinColumn(name = "document_id")
	    private Document document;

	    @ManyToOne
	    @MapsId("attribute_id")
	    @JoinColumn(name = "attribute_id")
	    private Attribute attribute;
	    
	    @ManyToOne
	    @JoinColumn(name = "value")
	    private AttributeValue value;
	    
	    
	    
}


