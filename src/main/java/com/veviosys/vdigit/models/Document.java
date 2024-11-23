package com.veviosys.vdigit.models;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Document {

	@org.hibernate.annotations.Type(type="uuid-char")
	@Id @GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "org.hibernate.id.UUIDGenerator")
	private UUID id;
	
	private String fileName;
	
	//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "userId")
	//@JsonIdentityReference(alwaysAsId = true)
	@ManyToOne
	@JoinColumn(name = "owner")
	private User owner;
	
	
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "userId")
	@JsonIdentityReference(alwaysAsId = true)
	@ManyToOne
	@JoinColumn(name = "master")
	private User master;
	
	@ManyToOne
	@JoinColumn(name = "doc_type")
	private DocumentType type;
	
	@Column(length = 100)
    private String  contentType; 
	private String pathServer;
	/*@JoinTable(name = "folder_document",
	joinColumns=@JoinColumn(name="document_id"),
	inverseJoinColumns = @JoinColumn(name="folder_id")
	)*/
	@OneToMany(mappedBy = "document")
	@JsonIgnore
	private List<DocumentFolder> folders;
	
	@OneToMany(mappedBy = "document")
	private List<DocumentAttributeValue> attributeValues;

	// @Temporal(TemporalType.DATE)
	private Date upload_date;
	
	private Boolean is_deleted;
	
	private Boolean isArchived;
	
	@Temporal(TemporalType.DATE)
	private Date last_edit_date;
	@OneToMany(mappedBy = "document")
	@JsonIgnore
	private List<DocumentVersion> versions;
/*	
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
	@JsonIdentityReference(alwaysAsId = true)
	*/
	@ManyToOne
    @JoinColumn(name="client_id")
	private Client client;
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
	@JsonIdentityReference(alwaysAsId = true)
	@ManyToOne
    @JoinColumn(name="clientDoc_id")
	private ClientDoc clientDoc;
	
	@JsonIgnore
	@OneToMany(mappedBy = "doc",  orphanRemoval = true,cascade = CascadeType.PERSIST)
	private List<InterneMessage> message;	
    private Boolean isGenerated;
	@Lob
	private String HTML_CONTENT;

	private String archiveParent;
	
	private int archiveCm;
}
