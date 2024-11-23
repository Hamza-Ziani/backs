package com.veviosys.vdigit.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity(name="tempo")
public class TempSession {
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name ="id")
    private Long idSess;
    private Long userId;
    @Column(name ="user_name")

    private String username;
    @Column(name ="sessionData")

    private String session;
}
