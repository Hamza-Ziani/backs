package com.veviosys.vdigit.classes;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileModel {


    private String fileName;
    private String fileId;
    private String fileBase64;


}
