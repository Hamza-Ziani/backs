package com.veviosys.vdigit.classes;

import lombok.Data;

@Data
public class AttributeIntegration {
    
    private Long id;
    private AttrType type;
    private String name;
    private String required;
    private String visib;
    private String uniq;
    private String rep;
    private String reprs;
    private String pos;
    private String listDep;
    private String defaultValue;
    private String libelle;
    private String configId;
    private String tableConfig;
    private boolean pubAccess;
    private boolean pubRestriction;
    private String dv;

}
