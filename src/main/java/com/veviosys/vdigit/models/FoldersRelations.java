package com.veviosys.vdigit.models;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import com.veviosys.vdigit.models.pk.FoldersRelationsPk;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FoldersRelations {
	
	@EmbeddedId
	private FoldersRelationsPk id;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @MapsId("parent_id") 
    @JoinColumn(name = "parent_id")
	private Folder parent;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
    @MapsId("child_id") 
    @JoinColumn(name = "child_id")
	private Folder child; 

}
