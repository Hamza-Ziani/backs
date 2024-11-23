package com.veviosys.vdigit.classes;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ElementDraft {


    private String idElement;

    private String nomElement;
    private String isLast;

    private String dateCreation;
    private String dateFin;

    private UUID idParent;

    private Long idTypeElement;

    public void logdata()
    {
      //  System.out.println("nom "+nomElement+" last");
    }
}
