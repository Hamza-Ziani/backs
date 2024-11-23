package com.veviosys.vdigit.integrationCm.classes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Attrs {

    private Long id;
    private String name;
    private String Type;
    private String val = null;
}
