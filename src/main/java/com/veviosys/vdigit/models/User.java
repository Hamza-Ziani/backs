package com.veviosys.vdigit.models;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.veviosys.vdigit.controllers.PermissionCourrier;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "Utilisateur", uniqueConstraints = { @UniqueConstraint(columnNames = { "username" }),
		@UniqueConstraint(columnNames = { "email" }) })
@ToString
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long userId;
	private String fullName;
	@JsonIgnore
	private int isValid;
	@JsonIgnore
	private Date registrationDate;
	private String username;
	@JsonIgnore
	private int archiviste;
	private String nomClient;
	private int hasAccessClient;
	@Column(nullable = true)
    private int hasAccessSecondary;
	private int fromLdap;
	
	private Integer tentativeNumber;

	@JsonIgnore
	@Lob
	private String logo;
	private String email;
	
	private Date lastLogin;
	@JsonIgnore
	@Column(nullable = true)
	private Integer field1;
	@JsonIgnore
	@Column(nullable = true)
	private String field2;
	@JsonIgnore
	@Column(nullable = true)
	private String field3;
	@Column(nullable = true)
	@JsonIgnore
	private String field4;

	private String mat;
	private String contetType;
	@JsonIgnore
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<FrequencySearch> searches;
	@JsonIgnore

	private String password;

	
	private int TraiterDepart;
	
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
	@JsonIdentityReference(alwaysAsId = true)
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "contact",referencedColumnName = "id")
	private Contact contact;
	

   
    private Long groupId;
    

    
  
    @JsonIgnore
	private long secondary;
	
	
	@JsonIgnore
	@Column(nullable = true)
	@OneToMany(mappedBy = "master", cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	private Set<User> usersM;

	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "userId")
	@JsonIdentityReference(alwaysAsId = true)
	@ManyToOne
	@JoinColumn(name = "master")
	private User master;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "user_group", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "groupId"))
	private List<Groupe> groups;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	@JsonIgnore
	private Set<Role> roles;
	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "folder_type_user", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "folder_type_id"))
	private List<FolderType> folder_types;

	@JsonIgnore
	@OneToMany(mappedBy = "master")
	private List<arborescence> arborescence;
	@JsonIgnore
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<Journal> userActions;
	@JsonIgnore
	@OneToMany(mappedBy = "master", fetch = FetchType.LAZY)
	private List<Journal> journals;
	// @JsonIgnore
	// @ManyToMany
	// @JoinTable(name = "user_favorite_folders", joinColumns = @JoinColumn(name =
	// "user_id"), inverseJoinColumns = @JoinColumn(name = "folder_id"))
	// private List<Folder> favorite_folders;

	// add
	@JsonIgnore
	@OneToMany(mappedBy = "master", fetch = FetchType.LAZY)
	private List<FolderType> folders_type;
	@JsonIgnore
	@OneToMany(mappedBy = "master", fetch = FetchType.LAZY)
	private List<DocumentType> documents_type;
	@JsonIgnore
	@OneToMany(mappedBy = "master", fetch = FetchType.LAZY)
    private List<Quality> qualities;
	@JsonIgnore
    @OneToMany(mappedBy = "master", fetch = FetchType.LAZY)
    private List<GroupUser> groupusers;
	@JsonIgnore
	@OneToMany(mappedBy = "master", fetch = FetchType.LAZY)
	private List<Groupe> ownerGroups;
	@JsonIgnore
	@OneToMany(mappedBy = "master", fetch = FetchType.LAZY)
	private List<Processus> processus;
	// @JsonIgnore
	// @OneToMany(mappedBy = "master")
	// private List<Client> clients;
	@JsonIgnore
	@OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
	private List<Folder> owned_folders;
	@JsonIgnore
	@OneToMany(mappedBy = "editedBy", fetch = FetchType.LAZY)
	private List<DocumentVersion> editedDocuments;
	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "doc_type_user", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "doc_type_id"))
	private List<DocumentType> docs_type;

	@JsonIgnore
	private Long isClient;

	@JsonIgnore
	@OneToMany(mappedBy = "sender")
	private List<InterneMessage> sentMessage;
	@JsonIgnore

	@OneToMany(mappedBy = "user")
	private List<RecieveMessage> recieveMessage;
	@JsonIgnore
	@OneToMany(mappedBy = "master", fetch = FetchType.LAZY)
	private List<PermissionDocument> owned_permissions;
	@JsonIgnore
	@OneToMany(mappedBy = "master", fetch = FetchType.LAZY)
	private List<PermissionCourrier> owned_permissionsC;
	@JsonIgnore
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<ProfilsAbsence> profilsAbsence;
	@JsonIgnore
	@OneToMany(mappedBy = "master", fetch = FetchType.LAZY)
	private List<ProfilsAbsence> allProfilsAbsence;
	@JsonIgnore
	@OneToMany(mappedBy = "supleant", fetch = FetchType.LAZY)
	private List<ProfilsAbsence> supleant;
	@JsonIgnore
	@OneToMany(mappedBy = "master", fetch = FetchType.LAZY)
	private List<Etape> allEtapes;
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "user_etape", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "etape_id"))
	@JsonIgnore
	private List<Etape> etapes;
	@JoinTable(name = "user_gestion_etape", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "clone_etape_id"))
	@JsonIgnore
	@ManyToMany(cascade = CascadeType.ALL)
	private List<CloneEtape> gestionetape;
	@JsonIgnore
	@OneToMany(mappedBy = "master", fetch = FetchType.LAZY)
	private List<Nature> nature;
	@JsonIgnore
	@Transient
	@OneToMany(mappedBy = "traitant")
	private List<CloneEtape> EtapesTraite;
	private Long parent;
	@JsonIgnore
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "notifMail", referencedColumnName = "id")
	private NotifMail notifMail;

	@JsonIgnore
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "delayMail", referencedColumnName = "id")
	private delayMail delayMail;
	private String sexe;
	private String title;
	private int secLevel;

	@JsonIgnore
	@OneToMany(mappedBy = "master")
	private List<MasterConfig> configs;

	@JsonIgnore
	@OneToMany(mappedBy = "master")
	private List<Attribute> attributes;
	private String picPath;
	@JsonIgnore

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<EtapeDetail> details;
	// public String getUserPic() {

	// String ret;
	// if(this.picPath!=null)
	// try {
	// ret = "data:" + this.getContetType() + ";base64,"
	// +
	// Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(this.picPath,
	// null)));
	// return ret ;
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return null;

	// }

	@JsonIgnore
	@OneToMany(mappedBy = "master")
	private List<receiver> recievers;
	@JsonIgnore
	@OneToMany(mappedBy = "master")
	private List<Sender> senders;



	@JsonIgnore
    @ManyToOne
    @JoinColumn(name="entity")
    private com.veviosys.vdigit.models.Entity entity;
    @JsonIgnore
    @OneToMany(mappedBy = "master")
    private List<com.veviosys.vdigit.models.Entity> entities;
    
	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}
    
    
    
}
