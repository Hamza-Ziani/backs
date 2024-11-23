package com.veviosys.vdigit.models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class ValueVersion {
	@GeneratedValue(strategy =  GenerationType.AUTO)
	@Id private Long id;
	
	@Column(length = 500)
	private String value;

	@JsonIgnore
	@Transient
	@OneToMany(mappedBy = "value")
	private List<DocumentVersionAttributeValue> documenAttribute;
}
