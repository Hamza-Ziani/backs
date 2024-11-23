package com.veviosys.vdigit.models.pk;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor@AllArgsConstructor
public class DocumentFolderPk implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6800037416798866720L;

	@org.hibernate.annotations.Type(type="uuid-char")
	@Column(name = "document_id")
    private UUID document_id;

	@org.hibernate.annotations.Type(type="uuid-char")
    @Column(name = "folder_id")
    private UUID folder_id;

}
