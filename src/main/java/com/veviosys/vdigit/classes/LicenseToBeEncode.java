package com.veviosys.vdigit.classes;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LicenseToBeEncode {


    private UUID id;

    private String numberUtilisateur;

    private String nomCompany;

    private String dateFin;
    
    private String type;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LicenseToBeEncode other = (LicenseToBeEncode) obj;
        if (dateFin == null) {
            if (other.dateFin != null)
                return false;
        } else if (!dateFin.equals(other.dateFin))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (nomCompany == null) {
            if (other.nomCompany != null)
                return false;
        } else if (!nomCompany.equals(other.nomCompany))
            return false;
        if (numberUtilisateur == null) {
            if (other.numberUtilisateur != null)
                return false;
        } else if (!numberUtilisateur.equals(other.numberUtilisateur))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dateFin == null) ? 0 : dateFin.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((nomCompany == null) ? 0 : nomCompany.hashCode());
        result = prime * result + ((numberUtilisateur == null) ? 0 : numberUtilisateur.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }
    
    

}

