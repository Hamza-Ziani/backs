package com.veviosys.vdigit.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Contact {

	@Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
	private Long id;
	private String adresse;
	private String zip;
	private String state;
	private String city;
	private String phone;
	private String fax;
	private String gsm;
	@Column(nullable = true)
	private Integer field1;
	@Column(nullable = true)
	private String field2;
	@Column(nullable = true)
	
	private String field3;
	@Column(nullable = true)
	private String field4;
 
	@JsonIgnore
    @OneToOne(mappedBy = "contact")
    private User user;
    @JsonIgnore
    @OneToOne(mappedBy = "contact")
    private Client client;

    
}
