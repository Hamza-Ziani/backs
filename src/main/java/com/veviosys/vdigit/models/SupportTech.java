package com.veviosys.vdigit.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SupportTech {
    
    @Id
	@GeneratedValue(strategy =  GenerationType.AUTO)
    private Long userId;
    private String username;
    private String pw;
    private Long masterId;
    
}
