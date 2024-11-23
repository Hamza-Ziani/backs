package com.veviosys.vdigit.models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class AttributeType {

	
	@GeneratedValue(strategy =  GenerationType.AUTO)
	@Id private Long id;
	@JsonBackReference
	@OneToMany(mappedBy = "type")
	private List<Attribute> attributes;
	
	@Column(length = 30)
	private String name;
	
	
}
