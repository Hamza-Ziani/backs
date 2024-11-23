package com.veviosys.vdigit.classes;

import java.util.Objects;

import lombok.Data;

@Data
public class JournaReports {

    public JournaReports(String user, String secondary, String date2,  String composant,String type) {
        super();
        this.user =  isNullSetNA(user);
        this.secondary = isNullSetNA(secondary);
        this.date =date2;
        this.composant = isNullSetNA(composant);
        this.type = type;

 //       this.sharedWith = isNullSetNA(sharedWith).replace(",", ", ");
    }
    private String user;
    private String secondary;
    private String date;
  
    private String composant;
    private String type;

  //  private String sharedWith;

    public String isNullSetNA(String o)
    {


        return Objects.isNull(o) ? "N/A" :o ;
    }
}
