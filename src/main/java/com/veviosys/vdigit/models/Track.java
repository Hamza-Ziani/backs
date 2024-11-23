package com.veviosys.vdigit.models;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import reactor.util.annotation.Nullable;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "tracks")
public class Track {

    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private Long id;
    
    private String email;
    
    
    
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="folder_id")
    private Folder folder;
    
    @Nullable
    private int clicked = 0;
    
    @Nullable
    private int read = 0;
    
}
