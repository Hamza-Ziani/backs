package com.veviosys.vdigit.classes;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AttributeClass {

	
	public AttributeClass() {
		// TODO Auto-generated constructor stub
	}
	public Long id;
    public String type;
    public String libelle;
	public String name;
    public String val;
    public String labelfr;
    public String labelar;
    public String labeleng;
    public String defaultValue;
    public Long listDep;
    public int rep;
    public String source;
    public String tableConfig;
    
    public AttributeClass(Long id, String type, String libelle, String name, String val, String labelfr, String labelar,
            String labeleng, String defaultValue) {
        super();
        this.id = id;
        this.type = type;
        this.libelle = libelle;
        this.name = name;
        this.val = val;
        this.labelfr = labelfr;
        this.labelar = labelar;
        this.labeleng = labeleng;
        this.defaultValue = defaultValue;
    }
    public Long configId;
    
    
}
