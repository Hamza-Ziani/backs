package com.veviosys.vdigit.classes;

import java.util.List;

import com.veviosys.vdigit.models.receiver;

import lombok.Data;

@Data
public class CourrierInfo {
 
    private List<receiver> dest;
    private String emet__ ;
    private String Objet ;
    private String reference;
}
