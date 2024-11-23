package com.veviosys.vdigit.classes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyValueFk {
   
    private String key;
    private String value;
    private String fk;
}
