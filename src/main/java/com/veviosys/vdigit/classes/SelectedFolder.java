package com.veviosys.vdigit.classes;

import com.veviosys.vdigit.models.Folder;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class SelectedFolder {
	
	Folder folder;
	Boolean isSelected;
	

}
