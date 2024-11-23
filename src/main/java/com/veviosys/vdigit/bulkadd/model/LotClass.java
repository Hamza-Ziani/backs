package com.veviosys.vdigit.bulkadd.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import com.veviosys.vdigit.classes.AttributeClass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@ToString
@AllArgsConstructor
@Data
@NoArgsConstructor
public class LotClass {
	
	public Long gId;
	
	public String gName;
	
	public Long imgsLength;
	
	public String gDocTypes;
	
	public String  commonAttrsVal;
	
	public Long lotType;
	
	public String lotObj;
	
	public String lotGroupName;
}
