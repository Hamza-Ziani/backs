package com.veviosys.vdigit.models;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;




@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class FrequencySearch {

	@GeneratedValue(strategy =  GenerationType.AUTO)
	private @Id Long Id;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	
	private String name;
	private String searchForm;
	@OneToMany(mappedBy = "search",fetch = FetchType.LAZY)
	private List<SearchAttributeValue>  search;
	
	private LocalDate search_date;

    private LocalDate last_edit_date;
     
}
