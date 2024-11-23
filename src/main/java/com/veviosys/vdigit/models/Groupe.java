package com.veviosys.vdigit.models;


import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;


import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Groupe {
	
@Id
@GeneratedValue(strategy = GenerationType.AUTO)
private Long groupId;
private String groupName;
@ManyToMany(fetch = FetchType.EAGER,
cascade = {
	    CascadeType.PERSIST,
	    CascadeType.MERGE
	},mappedBy = "groups")

private  List<User> users;
@JsonIgnore
@ManyToOne
@JoinColumn(name="master")
private User master;

@OneToMany(mappedBy = "group",fetch = FetchType.LAZY)
private  List<PermissionGroup>permissionGroup;

@OneToMany(mappedBy = "group",fetch = FetchType.LAZY)
private List<PermissionGroupN>permissionGroupN;

}
