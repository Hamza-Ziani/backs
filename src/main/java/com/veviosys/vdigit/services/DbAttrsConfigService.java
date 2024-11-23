package com.veviosys.vdigit.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import com.google.gson.Gson;
import com.veviosys.vdigit.classes.KeyValueFk;
import com.veviosys.vdigit.classes.mapSearch;
import com.veviosys.vdigit.models.Attribute;
import com.veviosys.vdigit.models.DbAttrsConfig;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.reposietories.AttributeRepo;
import com.veviosys.vdigit.repositories.DbAttrsConfigRepo;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class DbAttrsConfigService {
    @Autowired
    DbAttrsConfigRepo dbAttrsConfigRepo;
    // @Autowired
    // masterService ms;
    @Autowired
    AttributeRepo ar;

    public DbAttrsConfig addDbAttrsConfig(DbAttrsConfig attrsConfig) {
        if (testDbConfig(attrsConfig)) {
            attrsConfig.setMaster(connectedUser());
            return dbAttrsConfigRepo.saveAndFlush(attrsConfig);
        } else {
            return null;
        }
    }
    
    

    public DbAttrsConfig editDbAttrsConfig(DbAttrsConfig attrsConfig) {
        if (testDbConfig(attrsConfig)) {
            attrsConfig.setMaster(connectedUser());
            return dbAttrsConfigRepo.saveAndFlush(attrsConfig);
        } else {
            return null;
        }
    }

    public User connectedUser() {
        return ((CostumUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
    }

    public Boolean testDbConfig(DbAttrsConfig attrsConfig) {

        // Class.forName("com.ibm.db2.jcc.DB2Driver");

        if (attrsConfig.getApp().equals("orcl")) {
            try {
                String jdbc = "jdbc:oracle:thin:@";
                String datasource = jdbc + attrsConfig.getHost() + ":" + attrsConfig.getPort() + "/"
                        + attrsConfig.getDbName();
                System.out.println(datasource + "pw:::: " + attrsConfig.getPass() + "user :  " + attrsConfig.getUser());
                Connection con = DriverManager.getConnection(datasource, attrsConfig.getUser(), attrsConfig.getPass());

                con.close();
                return true;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else if (attrsConfig.getApp().equals("mssql")) {

            try {
                String jdbc = "jdbc:sqlserver://";
                String datasource = jdbc + attrsConfig.getHost() + ";databaseName=" + attrsConfig.getDbName();
                Connection con = DriverManager.getConnection(datasource, attrsConfig.getUser(), attrsConfig.getPass());

                con.close();
                return true;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

        } else  {

            try {
                String jdbc = "jdbc:mysql://";
                String datasource = jdbc + attrsConfig.getHost() +":"+attrsConfig.getPort()+ "/" + attrsConfig.getDbName();
                Connection con = DriverManager.getConnection(datasource, attrsConfig.getUser(), attrsConfig.getPass());

                con.close();
                return true;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

        }

    }

    public Page<DbAttrsConfig> getConfigs(Pageable pageable) {
        return dbAttrsConfigRepo.findByMaster(connectedUser(), pageable);
    }

    public List<String> getTablesByConfig(Long id) throws SQLException {
        String query = "";
        DbAttrsConfig attrsConfig = dbAttrsConfigRepo.findById(id).orElse(null);
        String datasource = "";
        if (attrsConfig.getApp().equals("orcl")) {
            query = "SELECT table_name name FROM user_tables";
            String jdbc = "jdbc:oracle:thin:@";
            datasource = jdbc + attrsConfig.getHost() + ":" + attrsConfig.getPort() + "/" + attrsConfig.getDbName();

        } else if (attrsConfig.getApp().equals("mssql")) {

            query = "SELECT table_name name FROM INFORMATION_SCHEMA.TABLES";
            String jdbc = "jdbc:sqlserver://";
            datasource = jdbc + attrsConfig.getHost() + ";databaseName=" + attrsConfig.getDbName();

        } else {
            
            query = "SELECT table_name name FROM information_schema.tables WHERE table_schema = ?";
            String jdbc = "jdbc:mysql://";
            datasource = jdbc + attrsConfig.getHost() +":"+attrsConfig.getPort()+ "/" + attrsConfig.getDbName();
            
        }

        Connection con = DriverManager.getConnection(datasource, attrsConfig.getUser(), attrsConfig.getPass());
        ResultSet rs;
        if(attrsConfig.getApp().equals("mysql")) {
            PreparedStatement pe = con.prepareStatement(query);
            pe.setString(1,attrsConfig.getDbName());
            rs = pe.executeQuery();
        } else {
            rs = con.createStatement().executeQuery(query);
        }
        List<String> tables = new ArrayList<String>();
        while (rs.next()) {
            tables.add(rs.getString("name"));
        }
        con.close();
        return tables;
    }

    public List<mapSearch> getColumnsByTableConfig(Long id, String name) throws SQLException {
        String query = "";

        DbAttrsConfig attrsConfig = dbAttrsConfigRepo.findById(id).orElse(null);
        String datasource = "";

        if (attrsConfig.getApp().equals("orcl")) {
            query = "SELECT column_name, data_type FROM USER_TAB_COLUMNS where table_name= ? ";
            String jdbc = "jdbc:oracle:thin:@";
            datasource = jdbc + attrsConfig.getHost() + ":" + attrsConfig.getPort() + "/" + attrsConfig.getDbName();

        } else if (attrsConfig.getApp().equals("mssql")) {

            query = "SELECT column_name, data_type FROM INFORMATION_SCHEMA.COLUMNS where table_name=?";
            String jdbc = "jdbc:sqlserver://";
            datasource = jdbc + attrsConfig.getHost() + ";databaseName=" + attrsConfig.getDbName();

        } else {
            
            query = "SELECT distinct(column_name), data_type FROM information_schema.columns where table_name=?";
            String jdbc = "jdbc:mysql://";
            datasource = jdbc + attrsConfig.getHost() +":"+attrsConfig.getPort()+ "/" + attrsConfig.getDbName();
            
        }

        Connection con = DriverManager.getConnection(datasource, attrsConfig.getUser(), attrsConfig.getPass());
        PreparedStatement pe = con.prepareStatement(query);
        pe.setString(1,name.toUpperCase());
        ResultSet rs = pe.executeQuery();
        List<mapSearch> tables = new ArrayList<mapSearch>();
        while (rs.next()) {
            tables.add(new mapSearch(rs.getString("column_name"), rs.getString("data_type"), ""));
        }
        return tables;
    }

    public List<mapSearch> getDataSourceByAttr(Attribute a) throws SQLException {

        String[] data = a.getTableConfig().split("[|]");
        @SuppressWarnings("unused")
        Boolean hasFk=data.length==4;
        String table = data[0];
        String key = data[1];
        String value = data[2];

        String query = "SELECT   " + key + " , " + value + " FROM " + table;

        DbAttrsConfig attrsConfig = dbAttrsConfigRepo.findById(a.getConfigId()).orElse(null);

        String datasource = "";

        if (attrsConfig.getApp().equals("orcl")) {

            String jdbc = "jdbc:oracle:thin:@";
            datasource = jdbc + attrsConfig.getHost() + ":" + attrsConfig.getPort() + "/" + attrsConfig.getDbName();

        } else if (attrsConfig.getApp().equals("mssql")) {

            String jdbc = "jdbc:sqlserver://";
            datasource = jdbc + attrsConfig.getHost() + ";databaseName=" + attrsConfig.getDbName();

        } else {
            
            String jdbc = "jdbc:mysql://";
            datasource = jdbc + attrsConfig.getHost() +":"+attrsConfig.getPort()+ "/" + attrsConfig.getDbName();
            
        }
        
        Connection con = DriverManager.getConnection(datasource, attrsConfig.getUser(), attrsConfig.getPass());
        System.err.println("" + query);
        ResultSet rs = con.createStatement().executeQuery(query);
        List<mapSearch> datares = new ArrayList<mapSearch>();
        while (rs.next()) {
            datares.add(new mapSearch(rs.getString(key), rs.getString(value), ""));
            Log.debug(rs);

        }
        return datares;
    }
    
    
    
    @Async
    public List<KeyValueFk> loadDataSourceByAttr(Attribute a) throws SQLException {

        
       System.out.println("update 10336");
        String[] data = a.getTableConfig().split("[|]");
        Boolean hasFk=data.length==4;
        String table = data[0];
        String key = data[1];
        String value = data[2];
         String fk="";

        

        String query = "SELECT   " + key + " , " + value + " FROM " + table;
           if(hasFk)
           {
              fk = data[3];
              query = "SELECT   " + key + " , " + value  + "," + fk + " FROM " + table;
           }

        DbAttrsConfig attrsConfig = dbAttrsConfigRepo.findById(a.getConfigId()).orElse(null);

        String datasource = "";

        if (attrsConfig.getApp().equals("orcl")) {

            String jdbc = "jdbc:oracle:thin:@";
            datasource = jdbc + attrsConfig.getHost() + ":" + attrsConfig.getPort() + "/" + attrsConfig.getDbName();

        } else if (attrsConfig.getApp().equals("mssql")) {

            String jdbc = "jdbc:sqlserver://";
            datasource = jdbc + attrsConfig.getHost() + ";databaseName=" + attrsConfig.getDbName();

        } else {
            
            String jdbc = "jdbc:mysql://";
            datasource = jdbc + attrsConfig.getHost() +":"+attrsConfig.getPort()+ "/" + attrsConfig.getDbName();
            
        }
        
        Connection con = DriverManager.getConnection(datasource, attrsConfig.getUser(), attrsConfig.getPass());
    
        ResultSet rs = con.createStatement().executeQuery(query);
         System.out.println("update 10336");
        List<KeyValueFk> datares = new ArrayList<KeyValueFk>();
        while (rs.next()) {
            datares.add(new KeyValueFk(rs.getString(key), rs.getString(value), hasFk ?  rs.getString(fk) : ""));
             System.out.println(rs);

        }
        Gson g= new Gson();
        a.setDefaultValue(g.toJson(datares));
    ar.save(a);
        return datares;
        
        
    }

    public Map<String, String> deleteConfigs(Long id) {
        // TODO Auto-generated method stub

        System.out.println(id + " AB");
        Map<String, String> res = new HashMap<String, String>();

        List<Attribute> ats = ar.findByConfigId(id);
        System.out.println(ats);
        if (ats.size() == 0) {
            dbAttrsConfigRepo.deleteById(id);
            res.put("status", "OK");
            return res;
        }

        res.put("status", "NOT_OK");

        return res;
    }
}