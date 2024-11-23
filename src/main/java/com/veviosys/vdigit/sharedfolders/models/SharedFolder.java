package com.veviosys.vdigit.sharedfolders.models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.veviosys.vdigit.models.Folder;
import com.veviosys.vdigit.models.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SharedFolder {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String message;
	
	@ManyToOne
	@JoinColumn(name = "sender_id")
	
	private User sender;
	
//	@JsonIgnore
	@OneToMany(mappedBy = "message")
	List<SharedWith> receivers;
	
	@ManyToOne
	@JoinColumn(name = "folder")
	
	private Folder folder;
	private Date sentDate;
}
