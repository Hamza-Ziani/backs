package com.veviosys.vdigit.models;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Certaficate {
	
	@Getter
	@Setter
	@Id 
	@GeneratedValue(strategy= GenerationType.AUTO)
	private Long id;
     
	@OneToOne 


	@Getter
	@Setter
	@JoinColumn(name = "utilisateur")
	private User  user;
     

	@Getter
	@Setter
    private String validityDate;
     

	@Getter
	@Setter
    private String clientPath; 


	@Setter
    private String pwd;
    
	@Getter
	@Setter
    @Column(length = 3000)
    private String pk;
	@Getter
	@Setter
    private Boolean valid;
    
	@Getter
	@Setter
    private String validationReasons;
    
	
	@JsonIgnore
	public String getPwd2() {
		return pwd;

	}
	
	
	public String getPwd() {
	if (!Objects.isNull(pwd)) {
		if (!pwd.isEmpty()) {
			  return "*******";
		}
		return null;
	}
	return null;

	}
	
}
