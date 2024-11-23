package com.veviosys.vdigit.models;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Password {

	@Id
	@GeneratedValue(strategy =  GenerationType.AUTO)
	private Long id;
	private String password;
	private String generatedPassword;
	private Date generatedDate;
	private Date expirationDate;
	private int passwordTentative;
	private int isValid;
	@Column(nullable = true)
	private Integer field1;
	@Column(nullable = true)
	private String field2;
	@Column(nullable = true)
	private String field3;
	@Column(nullable = true)
	private String field4;
	// @OneToOne(mappedBy = "pw")
	// @JsonIgnore
	// @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
	// @JsonIdentityReference(alwaysAsId = true)
	// @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	// @JoinColumn(name = "user", referencedColumnName = "userId")
    // private User user;
	 
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getGeneratedPassword() {
		return generatedPassword;
	}
	public void setGeneratedPassword(String generatedPassword) {
		this.generatedPassword = generatedPassword;
	}
	public Date getGeneratedDate() {
		return generatedDate;
	}
	public void setGeneratedDate(Date generatedDate) {
		this.generatedDate = generatedDate;
	}
	public Date getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}
	public int getPasswordTentative() {
		return passwordTentative;
	}
	public void setPasswordTentative(int passwordTentative) {
		this.passwordTentative = passwordTentative;
	}
	public int getIsValid() {
		return isValid;
	}
	public void setIsValid(int isValid) {
		this.isValid = isValid;
	}
	// public User getUser() {
	// 	return user;
	// }
	// public void setUser(User user) {
	// 	this.user = user;
	// }
	
	
	
}
