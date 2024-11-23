package com.veviosys.vdigit.bulkadd.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.ManyToAny;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class LotGroup {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO
			)
	private Long lgId;
	
	private String lgName;
	
	private String pagesIndex;
	
	
	private Long groupType;
	@ManyToOne(fetch = FetchType.LAZY)
	private Lot lId;
	
	
	
	
	
	
	
	
	
}
