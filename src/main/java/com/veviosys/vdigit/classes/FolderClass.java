package com.veviosys.vdigit.classes;

import java.util.Date;
import java.util.List;

import com.veviosys.vdigit.models.Sender;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import reactor.util.annotation.Nullable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FolderClass {

	private String id;

	private Long type;

	private Long client;

	private Long owner;

	private String dateReception;
	
	private List<String> newSenders;
	
	private String reference;
	
	private String receiver;
	
	private Long number;
	
	private String objet;
	
	private String refAuto;
	
	private String order;
	
	// @JsonFormat(shape = JsonFormat.Shape.STRING ,pattern = "yyyy-dd-MM")
	private String date;

	// @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-dd-MM")
	private Date creation_date;
	// @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-dd-MM")
	private Date last_edit_date;

	private Boolean is_deleted;

	private String parent_folder;
	
	private Integer field1;

	private String field2;
	
	private String finalise;
	
	private String fini;
	
	private int mode;
	
	private List<Long> dest;
	
	private String sender;

	private String field3;
	
	private Integer accuse;
	
	private String field4;
	
	private Long destinataire;
	
	private Long nature;
	
	private String deDate;
	
	private String toDate;
	
	@Override
	public String toString() {
		return "FolderClass [id=" + id + ", type=" + type + ", client=" + client + ", owner=" + owner + ", reference="
				+ reference + ", number=" + number + ", date=" + date + ", creation_date=" + creation_date
				+ ", last_edit_date=" + last_edit_date + ", is_deleted=" + is_deleted + ", parent_folder="
				+ parent_folder + ", field1=" + field1 + ", field2=" + field2 + ", field3=" + field3 + ", field4="
				+ instru + "]";
	}

	private String motif;
	
	private String instru;

}
