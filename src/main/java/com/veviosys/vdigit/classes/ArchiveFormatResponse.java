package com.veviosys.vdigit.classes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArchiveFormatResponse {

    private String owner;
    private String fileName;
    private String isFolder;
    private String ClientsName;
    private String typeName;
    private String Datedenregistrement;
    private String id ;
    private String isFav;
    private String content;
    private String repVal;
    private String last_edit_date;
        
}
