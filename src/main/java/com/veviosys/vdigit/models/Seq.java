package com.veviosys.vdigit.models;



import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Seq {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String Seq;
    private String type;
}
