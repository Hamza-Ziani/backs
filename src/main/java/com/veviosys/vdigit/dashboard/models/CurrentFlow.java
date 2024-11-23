package com.veviosys.vdigit.dashboard.models;

import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString()
public class CurrentFlow {

	
	private Long count;
	private String name;
}
