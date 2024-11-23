package com.veviosys.vdigit.models;
import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.veviosys.vdigit.models.pk.DocumentAttributeValuePK;
import com.veviosys.vdigit.models.pk.SearchAttributeValuePk;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class SearchAttributeValue implements Serializable{

	
	

	@EmbeddedId
    private SearchAttributeValuePk id;
	
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
    @MapsId("search_id") 
    @JoinColumn(name = "search_id")
	private FrequencySearch search;
    
	@ManyToOne(fetch = FetchType.LAZY)
    @MapsId("attribute_id")
    @JoinColumn(name = "attribute_id")
    private SearchAttribute attribute;
    
    private String value;
    
    private String field1;
    
 
	
	
}
