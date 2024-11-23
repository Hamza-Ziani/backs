package com.veviosys.vdigit.bulkadd.model;

import lombok.ToString;

import javax.persistence.Lob;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class LotDataClass {

	@Lob
	public String image;
	public String name;
	
}
