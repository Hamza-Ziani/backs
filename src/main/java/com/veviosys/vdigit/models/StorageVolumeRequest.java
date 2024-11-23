package com.veviosys.vdigit.models;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StorageVolumeRequest {

    @Id  
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Date requestDate;
   
    private Date endlDate;
    @JsonIgnore
    private String genPass;
    private Long masterId;
    private String path;


    @JsonIgnore
	public Boolean getIsStillValid() {
		
		return (new Date()).before(endlDate);
	}
}
