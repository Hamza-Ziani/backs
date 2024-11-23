package com.veviosys.vdigit.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DbAttrsConfig {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;
    @Column(length = 50)
    private String configName;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "master")
    private User master;
    String app;
    String host;
    String port;
    @Column(name = "name")

    String user;

    String pass;
    String dbName;
    @Column(name = "tableName")
    String table;
}
