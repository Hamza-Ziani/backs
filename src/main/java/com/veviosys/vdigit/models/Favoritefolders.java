package com.veviosys.vdigit.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Favoritefolders implements Serializable {
    
    @EmbeddedId
    private FKFOldersFavorite id;

    
    @Column(nullable = true)
	private Integer field1;
	@Column(nullable = true)
	private String field2;
	@Column(nullable = true)
	private String field3;
	@Column(nullable = true)
	private String field4;
}