package com.veviosys.vdigit.dashboard.models;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity

public class UserDashboardSettings {
	
	@Id 
	private Long userId;
	
	private Long hierarchPeriod;
	private Long hierarchUser;
	private String hierarchUnit;
	private String hierarchShowBy;
	
	private Long donePeriod;
	private String doneUnit;
	private String doneShowBy;
	
	private Long itemsPeriod;
	private String itemsUnit;
	private String itemsShowBy;
	
	private Long grPeriod= 3L;
	private String grUnit= "month";
	private String grShowBy= "month";
	
	
	
	
	
	

}
