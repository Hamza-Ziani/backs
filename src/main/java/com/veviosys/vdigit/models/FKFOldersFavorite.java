package com.veviosys.vdigit.models;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
public class FKFOldersFavorite implements Serializable {
    
    
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "user_id")
   // @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "userId")
	//@JsonIdentityReference(alwaysAsId = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user;
    
    @ManyToOne
    //@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "userId")
	//@JsonIdentityReference(alwaysAsId = true)
    @JoinColumn(name = "folder_id", referencedColumnName = "id")
    private Folder folder;
}
