package com.veviosys.vdigit.classes;

import java.util.List;
 
import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class Search {
	
	
	  public Long id;
	  
	  public String name;
	  
	  public List<SearchAttrs> attributes;
	   public String searchForm; 
	  public Search() {
	
	  }

}


