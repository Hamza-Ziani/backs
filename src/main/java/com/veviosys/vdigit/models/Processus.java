package com.veviosys.vdigit.models;
import java.util.ArrayList;
import java.util.Date;
import java.util.List; 
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn; 
import javax.persistence.Lob; 
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany; 
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; 
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Processus {
	@Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private Long id;
    private String name;
    private Date DateCreation;
    
	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="master")
	private User master;
	private int delai;
   @Lob
	private String model;
	@JsonIgnore
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
	@OneToMany(mappedBy = "processus",fetch=FetchType.LAZY)
	private List<Etape> etapes;
 
	@JsonIgnore
	@OneToMany(mappedBy = "process",  orphanRemoval = true,cascade = CascadeType.PERSIST,fetch = FetchType.LAZY)
	
	private List<Nature> natures;
	 private Long parent;
}
