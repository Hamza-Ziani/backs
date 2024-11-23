package com.veviosys.vdigit.classes;
 
import java.util.List;
import java.util.UUID;
 
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public  class DocumnentClass {
	
	public UUID id;
	public long type;
	public String fileName;
	public String content;
	public String order;
	public Boolean isGenerated;
	public String htmlContent;
	public String fullText;
	public List<AttributeClass> attrs;

}
