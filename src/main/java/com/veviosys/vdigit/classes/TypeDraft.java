package com.veviosys.vdigit.classes;
 

import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TypeDraft {



        private String nomTypeElement;
        private String picPath;
        private Long idParent;
        private FileModel fileModel;



}