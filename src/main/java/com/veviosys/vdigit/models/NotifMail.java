package com.veviosys.vdigit.models;

import javax.persistence.Column;
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
public class NotifMail {
    @Id
	@GeneratedValue(strategy= GenerationType.AUTO)
	private Long id; 
    private String mail;
    private String password;
    private String host;
    private String port; 
    @Column(name = "Secssl")
    private Boolean ssl;
	@OneToOne(mappedBy = "notifMail")
    private User user;

}