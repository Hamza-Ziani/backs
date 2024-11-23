package com.veviosys.vdigit.controllers;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.veviosys.vdigit.classes.mapSearch;
import com.veviosys.vdigit.models.DbAttrsConfig;
import com.veviosys.vdigit.services.DbAttrsConfigService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
 
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/v1/dbAttrsConfig")
public class DbAttrsConfigController {
    @Autowired
    DbAttrsConfigService dbAttrsConfigService;

    @PostMapping("")
    public ResponseEntity<DbAttrsConfig> create(@RequestBody DbAttrsConfig attrsConfig) {
        try {
            DbAttrsConfig savedItem = dbAttrsConfigService.addDbAttrsConfig(attrsConfig);
            if (Objects.nonNull(savedItem)) {
                return new ResponseEntity<>(savedItem, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(null, HttpStatus.EXPECTATION_FAILED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
  
    

    @PostMapping("edit")
    public ResponseEntity<DbAttrsConfig> edit(@RequestBody DbAttrsConfig attrsConfig) {
        try {
            DbAttrsConfig savedItem = dbAttrsConfigService.editDbAttrsConfig(attrsConfig);
            if (Objects.nonNull(savedItem)) {
                return new ResponseEntity<>(savedItem, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(null, HttpStatus.EXPECTATION_FAILED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/check")
    public ResponseEntity<Boolean> check(@RequestBody DbAttrsConfig attrsConfig) {
        try {
            Boolean savedItem = dbAttrsConfigService.testDbConfig(attrsConfig);
            if (savedItem) {
                return new ResponseEntity<>(savedItem, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(savedItem, HttpStatus.EXPECTATION_FAILED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("")
    public Page<DbAttrsConfig>getConfigs( Pageable pageable){
        return dbAttrsConfigService.getConfigs(pageable);
    }
    
    @DeleteMapping("/{id}")
    public Map<String, String> deleteConfigs(@PathVariable Long id){
        return dbAttrsConfigService.deleteConfigs(id);
    }

    // @GetMapping("/all")
    // public List<DbAttrsConfig>findAll( Pageable pageable){
    //     return dbAttrsConfigService.getConfigs(pageable);
    // }
    @GetMapping("tables/{id}")
    public List<String>getTablesByConfig(@PathVariable Long id) throws SQLException{
        return dbAttrsConfigService.getTablesByConfig(id);
    }
    @GetMapping("tables/{id}/{name}")
    public List<mapSearch>getColumnsByTableConfig(@PathVariable Long id,@PathVariable String name) throws SQLException{
        return dbAttrsConfigService.getColumnsByTableConfig(id, name);
    }
    
    // @GetMapping("data/{id}")
    // public List<mapSearch>getDataSourceByAttr(@PathVariable Long id) throws SQLException{
    //     return dbAttrsConfigService.getDataSourceByAttr(id);
    // }

}
