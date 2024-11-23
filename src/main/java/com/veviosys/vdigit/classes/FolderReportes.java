package com.veviosys.vdigit.classes;

import java.util.Date;
import java.util.Objects;

import lombok.Data;

@Data
public class FolderReportes {


    public FolderReportes(String ref, String nature, String date2, String emetOrDests, String object,String lblNature,String type) {
        super();
        this.ref =  isNullSetNA(ref);
        this.nature = isNullSetNA(nature);
        this.date =date2;
        this.emetOrDests = isNullSetNA(emetOrDests);
        this.object = isNullSetNA(object);
        this.lblNature = lblNature;
        this.type = type;

 //       this.sharedWith = isNullSetNA(sharedWith).replace(",", ", ");
    }
    private String ref;
    private String nature;
    private String date;
    private String emetOrDests;
    private String object;
    private String lblNature;
    private String type;

  //  private String sharedWith;

    public String isNullSetNA(String o)
    {


        return Objects.isNull(o) ? "N/A" :o ;
    }


}