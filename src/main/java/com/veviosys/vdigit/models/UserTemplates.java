package com.veviosys.vdigit.models;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@javax.persistence.Entity
@Table(name = "usertemplates")
public class UserTemplates {

@org.hibernate.annotations.Type(type="uuid-char")
@Id @GeneratedValue(generator="system-uuid")
@GenericGenerator(name="system-uuid", strategy = "org.hibernate.id.UUIDGenerator")
private UUID id;
   
@Column(name = "UT_NAME")
    private String name;
   
@Column(name = "UT_desc")
    private String desc;
   
    private Date creationDate;
   
    private Date modificationDate;
   
    private Long userId;
   
   private Boolean hasFooter;
   
   @Transient
   private String content;
   private Boolean hasHeader;
   
    @JsonIgnore
    private String templatePath;
}
