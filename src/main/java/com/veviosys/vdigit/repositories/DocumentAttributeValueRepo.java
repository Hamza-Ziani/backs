package com.veviosys.vdigit.repositories;

import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.veviosys.vdigit.models.DocumentAttributeValue;
import com.veviosys.vdigit.models.pk.DocumentAttributeValuePK;

public interface DocumentAttributeValueRepo extends JpaRepository<DocumentAttributeValue, DocumentAttributeValuePK>{

	
	
	//ELMEHDI ALILOU
    @Transactional
    @Query(value="select dav.document_id from "
            + "document_attribute_value dav inner join attribute_value av inner join attribute a inner join document d "
            + "where d.id=dav.document_id and a.id=dav.attribute_id and dav.value=av.id and d.doc_type=:type and a.name=:idA and av.value Like %:val% and d.master=:masterId ",nativeQuery = true)
    List<UUID> findDocs(@Param("idA")String attName,@Param("val")String Val,@Param("type")Long type,@Param("masterId") Long masterId);
   
    DocumentAttributeValue findByDocumentIdAndAttributeName(UUID id,String name);
   

}
