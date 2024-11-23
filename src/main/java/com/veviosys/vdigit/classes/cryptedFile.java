package com.veviosys.vdigit.classes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@AllArgsConstructor
public class cryptedFile {
    private String file;
    private String entries;
}
