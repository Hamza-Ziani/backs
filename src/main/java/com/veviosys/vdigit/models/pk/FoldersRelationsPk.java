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
@AllArgsConstructor 
@NoArgsConstructor
public class FoldersRelationsPk implements Serializable {

	/**
	 * 
	 */

	private static final long serialVersionUID = 6800037416798866720L;
	
	@org.hibernate.annotations.Type(type="uuid-char")
	@Column(name = "parent_id")
    private UUID parent_id;
	@org.hibernate.annotations.Type(type="uuid-char")
    @Column(name = "child_id")
    private UUID child_id;

}
