package com.veviosys.vdigit.models;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Role {

	@Id
	@GeneratedValue(strategy =  GenerationType.AUTO)
	private Long id;
	private String role;
	@ManyToMany(mappedBy = "roles",fetch = FetchType.LAZY)
	private Set<User> users;
	

	
   
}
