package com.veviosys.vdigit.models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Client {
	
	
	@GeneratedValue(strategy =  GenerationType.AUTO)
	@Id private Long id;
	// @ManyToOne
	// @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "userId")
	// @JsonIdentityReference(alwaysAsId = true)
    // @JoinColumn(name="master_id")
	// private User master;
	
	private String name;
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
	@JsonIdentityReference(alwaysAsId = true)
	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "contact")
    private Contact contact ;
	
	// @OneToMany(cascade = CascadeType.ALL,mappedBy = "client")
    // @JsonIgnore
    // private List<Folder> folders ;
	
	@Column(nullable = true)
	private Integer field1;
	@Column(nullable = true)
	private String field2;
	@Column(nullable = true)
	 private String field3;
	@Column(nullable = true)
	private String field4;
	@OneToMany(cascade = CascadeType.ALL,mappedBy = "client")
    @JsonIgnore
    private List<Document> documents ;
	
}
