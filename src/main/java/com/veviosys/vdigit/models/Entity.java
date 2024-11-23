package com.veviosys.vdigit.models;

import java.util.List;

import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

import enums.EntityCat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@javax.persistence.Entity
public class Entity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String name;
	@JsonIgnore
	@OneToMany(mappedBy = "entity", fetch = FetchType.LAZY)
	private List<User> users; 
	private EntityCat cat;
	//private Long process;
    @JsonIgnore
	@ManyToOne
	@JoinColumn(name="master")
	private User master;
}
