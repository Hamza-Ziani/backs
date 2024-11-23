package com.veviosys.vdigit.models;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.veviosys.vdigit.models.pk.DocumentFolderPk;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class DocumentFolder {
	@JsonIgnore
	@EmbeddedId
	private DocumentFolderPk id;
	

   	@ManyToOne
    @MapsId("document_id") 
    @JoinColumn(name = "document_id")
    private Document document;

   	
   	@JsonIgnore
   	@ManyToOne
    @MapsId("foldert_id") 
    @JoinColumn(name = "folder_id")
    private Folder folder;
   	
	
	
}
