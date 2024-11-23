package com.veviosys.vdigit.classes;

import com.veviosys.vdigit.models.AttributeType;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class SearchResultColumn {

    public  String key;
    public String value;  
    public String labelfr;
    public String labelar;
    public String labeleng;
    public String defaultValue;
    public String owner;
    public int rep;
    public AttributeType type;
    
    public SearchResultColumn(String key, String value, String labelfr, String labelar, String labeleng,
            String defaultValue,String owner) {
        super();
        this.key = key;
        this.value = value;
        this.labelfr = labelfr;
        this.labelar = labelar;
        this.labeleng = labeleng;
        this.defaultValue = defaultValue;
        this.owner = owner;
    }
 
    
}
