package com.veviosys.vdigit.classes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserClass {
    private Long userId;
	private String fullName;
	private int isValid; 
	private String username;
    private Long parent;
    private String mat;
    private String sexe;
    private String email;

	private int hasAccessClient;
    private Long id; 
	private String adresse;
	private String zip;
	private String state;
	private String city;
	private String phone;
	private String fax;
	private String gsm;

}
