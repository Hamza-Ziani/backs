package com.veviosys.vdigit.classes;

import lombok.Data;

@Data
public class DocTypeIntegration {
 
    private Long id;
    private int selected;
    private int isVersionable;
    private String name;
    private String label;
    private String repDefaultValue;
    private boolean autoFoldering;
    private String mappedWith;
    private String mappedBy;
    private boolean isFolder;
    private String  mapsBy;
    private String represent;
    private String tableRepresentors;
    private String flowType;
    private boolean hasFlow;
    private String flowDefault;
    private boolean userChoise;
    
}
