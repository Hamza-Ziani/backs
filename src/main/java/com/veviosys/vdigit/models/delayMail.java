package com.veviosys.vdigit.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class delayMail {
    @Id
	@GeneratedValue(strategy= GenerationType.AUTO)
	private Long id; 
    private int delayPerDay;
    private int delaySup;
    private int delayUs;
   
	@OneToOne(mappedBy = "delayMail")
    private User user;

}