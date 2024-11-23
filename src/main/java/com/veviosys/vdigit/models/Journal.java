package com.veviosys.vdigit.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

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
public class Journal {
    @Id
	@GeneratedValue(strategy =  GenerationType.AUTO)
	private Long id;
  @Lob   
private String action;
private String typeEv;
private String composante;
private String ConnectedSacondaryName;
@Column(name = "method")
private String mode;
@Column(name = "Action_Date")
private Date date;

 
@ManyToOne
@JoinColumn(name = "user_id")
    private User user;
    @JsonIgnore
	@ManyToOne
	@JoinColumn(name="master")
    private User master;
}
