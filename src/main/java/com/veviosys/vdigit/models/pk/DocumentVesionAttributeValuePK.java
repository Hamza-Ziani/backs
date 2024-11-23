package com.veviosys.vdigit.models.pk;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter@Setter
@Embeddable
public class DocumentVesionAttributeValuePK  implements Serializable {
	private static final long serialVersionUID = 6800037416798866720L;
	
	@Column(name = "document_id")
    private Long documentV_id;

    @Column(name = "attribute_id")
    private Long attribute_id;

}
 