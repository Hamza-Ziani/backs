package com.veviosys.vdigit.classes;

import java.util.List; 

import com.veviosys.vdigit.models.Etape;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProcessRequest {

	
	private List<Etape> etapes;
	private String model;
}
