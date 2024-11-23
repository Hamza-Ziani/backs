package com.veviosys.vdigit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.veviosys.vdigit.models.DocumentVersionAttributeValue;
import com.veviosys.vdigit.models.pk.DocumentVesionAttributeValuePK;

public interface DocumentVersionAttributeValueRepo extends JpaRepository<DocumentVersionAttributeValue,DocumentVesionAttributeValuePK> {

}
