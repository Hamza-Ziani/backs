package com.veviosys.vdigit.classes;

import java.util.HashMap;
 

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentTypeClass {

    public Long id;
    public String name;
    public HashMap<String, Integer> attr;

}
