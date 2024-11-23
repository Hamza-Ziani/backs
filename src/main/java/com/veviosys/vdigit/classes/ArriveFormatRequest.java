package com.veviosys.vdigit.classes;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArriveFormatRequest {
  
    private List<String> docTypes;
    private String fieldName;
    private String fieldValue;
}
