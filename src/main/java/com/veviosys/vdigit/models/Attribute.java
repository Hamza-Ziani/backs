package com.veviosys.vdigit.models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.google.gson.Gson;
import com.veviosys.vdigit.classes.KeyValue;
import com.veviosys.vdigit.classes.KeyValueFk;

import org.springframework.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
// @Table(uniqueConstraints={@UniqueConstraint(columnNames={"name"})})

public class Attribute {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "attribute_type")
    @JsonManagedReference
    private AttributeType type;

    @Column(length = 50)
    private String name;
    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "type_document_attribute", joinColumns = @JoinColumn(name = "attribute_id"), inverseJoinColumns = @JoinColumn(name = "document_type_id"))
    private List<DocumentType> document_types;
    @JsonIgnore
    @OneToMany(mappedBy = "attribute", fetch = FetchType.LAZY)
    private List<DocumentAttributeValue> documentAttributeValues;
    @JsonIgnore
    @OneToMany(mappedBy = "attribute", fetch = FetchType.LAZY)
    private List<DocumentVersionAttributeValue> documentAttributeValuesVersion;

    private int required;
    private int rep;
    @Column(nullable = true)
    private int visib;
    private String libelle;
    @ManyToOne()
    @JsonIgnore
    @JoinColumn(name = "master")
    private User master;
    private String labelfr;
    private String labelar;
    private String labeleng;
    @Lob
    private String defaultValue;

    private Long configId;

    private String tableConfig;

    private Long listDep;
    
    public String getDefaultValue() {
        
        
        System.out.println(getType().getName());
        
        if(getType().getName().equals("ListDep"))
        {
        
            java.lang.reflect.Type listType = new com.google.common.reflect.TypeToken<ArrayList<KeyValueFk>>() {
            }.getType();
            List<KeyValueFk> convertedObject = new Gson().fromJson(defaultValue, listType);
            
            convertedObject.sort(Comparator.comparing(KeyValueFk::getValue));
            System.out.println(convertedObject);
            return (new Gson()).toJson(convertedObject, listType);
            
        }else if(getType().getName().contains("List")) {
            java.lang.reflect.Type listType = new com.google.common.reflect.TypeToken<ArrayList<KeyValue>>() {
            }.getType();
            List<KeyValue> convertedObject = new Gson().fromJson(defaultValue, listType);
            
            convertedObject.sort(Comparator.comparing(KeyValue::getValue));
            System.out.println(convertedObject);
            return (new Gson()).toJson(convertedObject, listType);
            
            
        }
        return defaultValue;
            
        
    }



}
