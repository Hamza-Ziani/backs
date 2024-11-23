package com.veviosys.vdigit.classes;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RechHierDraft {

    private String key;
    private String parent;
    private Long type;
    private Long typeParent;

}
