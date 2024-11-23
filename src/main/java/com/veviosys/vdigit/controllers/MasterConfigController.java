package com.veviosys.vdigit.controllers;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.veviosys.vdigit.classes.BarcodeDb;
import com.veviosys.vdigit.classes.FluxElectro;
import com.veviosys.vdigit.classes.StorageDetail;
import com.veviosys.vdigit.models.MasterConfig;
import com.veviosys.vdigit.services.MasterConfigService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("api/v1/configurations")
public class MasterConfigController {

	@Autowired private MasterConfigService configService;
	
	
	//@Secured("ROLE_MASTER")
	@GetMapping()
	public ResponseEntity< List<MasterConfig>> getAllConfigs(){
		
		try {
			return new ResponseEntity<List<MasterConfig>>(configService.getMasterConfigs(),HttpStatus.OK);
		} catch (Exception e) {
	        return new ResponseEntity<List<MasterConfig>>(HttpStatus.NO_CONTENT);
		}
		
		
	}
	@PostMapping("/dbtest")
	public ResponseEntity<?> testDbConfigs(@RequestBody BarcodeDb barcodeDb){
		Connection con;
		
		String jdbc="";
		//.println(barcodeDb.getApp());
		if(barcodeDb.getApp().equals("mysql"))
		{
			jdbc="jdbc:mysql://";
		}
		else if(barcodeDb.getApp().equals("db2"))
	    {
	    	try {
				Class.forName("com.ibm.db2.jcc.DB2Driver");
			} catch (ClassNotFoundException e) {
				 	
				e.printStackTrace();
			}
	    	jdbc="jdbc:db2://";
	    	
	    }
		
		
		try {
			
			////jdbc:mysql://localhost:3306/karthicraj
				//con = DriverManager.getConnection("jdbc:mysql://localhost:3306/docurium","root","");
			    
			String datasource = jdbc+barcodeDb.getHost()+":"+barcodeDb.getPort()+"/"+barcodeDb.getDbName();
			//.println(datasource);
			con = DriverManager.getConnection(datasource,barcodeDb.getUser(),barcodeDb.getPass());
			   //stm.close();
			con.close();
			return new ResponseEntity(HttpStatus.OK);
			
		} catch (Exception e) {
			e.printStackTrace();
	        return new ResponseEntity(HttpStatus.BAD_REQUEST);
		} 
		
		
	}
	
	
	@PostMapping()
	@Secured("ROLE_MASTER")
	public ResponseEntity addNewConfig(@RequestBody MasterConfig cfg) {
		

		try {
		    

			
		    configService.addNewConfig(cfg.getConfigName(), cfg.getConfigValue()); 
		  
			
			
			return new ResponseEntity(HttpStatus.OK);
			
		} catch (Exception e) {
			e.printStackTrace();
	        return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
		
	}
	
	@Secured("ROLE_MASTER")
	@PostMapping("/storage")
	public ResponseEntity<?> storagePathConfig(@RequestBody MasterConfig cfg) {
		

		try {
			
			
			configService.configStoragePath(cfg);
			
			return new ResponseEntity(HttpStatus.OK);
			
		} catch (Exception e) {
			e.printStackTrace();
	        return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
		
	}
	
	@Secured("ROLE_MASTER")
	@GetMapping("/{name}")

	public List<StorageDetail> getConfigByName(@PathVariable String name)
	{
	return	configService.findConfigByName(name);
	}
	
	@Secured("ROLE_MASTER")
	@GetMapping("/flux/{name}")
    public FluxElectro getConfigByNameFlux(@PathVariable String name)
    {
       return  configService.findConfigByNameFlux(name);
    }
	
	@Secured("ROLE_MASTER")
	@GetMapping("/cm/{name}")
    public MasterConfig getCMIntegration(@PathVariable String name)
    {
       return  configService.getCmIntegration(name);
    }
	
	@Secured("ROLE_MASTER")
	@GetMapping("/activepath")
	public Map<String,String> getActivePath()
	{
		Map<String, String> map = new HashMap<>();
		map.put("path",configService.findActivePath());
		return map;
	}
	@GetMapping("/nextbarcodeval")
	public ResponseEntity<String> nextBarcode()
	{
	
try {
	String jdbc ="";
	String sql="";
	String fieldName="";
	   Connection con;
	   MasterConfig cfg = configService.getStragey2Value();	
	   BarcodeDb barcodeDb = new BarcodeDb();
	   //.println(cfg.getConfigValue());
	   //.println(barcodeDb.toString());
	   barcodeDb.ToObject(cfg.getConfigValue());
	   
	    if(barcodeDb.getApp().equals("mysql"))
	    {
	    	
	    	jdbc="jdbc:mysql://";
	    	sql="SELECT NEXTVAL("+barcodeDb.getTable()+")";
	    	
	    }
	    else if(barcodeDb.getApp().equals("db2"))
	    {
	    	try {
				Class.forName("com.ibm.db2.jcc.DB2Driver");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	jdbc="jdbc:db2://";
	    	//jdbc="jdbc:mysql://";
	    	sql="NEXT VALUE FOR seq";
	    	
	    	
	    }
	   
	   String datasource = jdbc+barcodeDb.getHost()+":"+barcodeDb.getPort()+"/"+barcodeDb.getDbName()+"?reconnect=true";
	   //.println(datasource+"  "+barcodeDb.getTable());
	   
	   
	   con = DriverManager.getConnection(datasource,barcodeDb.getUser(),barcodeDb.getPass());
	   Statement stm = con.createStatement();
	   ResultSet resultset = stm.executeQuery(sql);
	   String nextVal="";
	   while (resultset.next()) {
           nextVal = resultset.getString(1);
           
       }
	   stm.close();
	   resultset.close();
	   con.close();
			return new ResponseEntity<String>(String.valueOf(nextVal),HttpStatus.OK);
			
		} catch (Exception e) {
			e.printStackTrace();
	        return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
		
		
	}
	
	// Selected DATABASE

	@Value("${documania.datasource.supportedlist}")
	private String supportdbList;

	@Value("${documania.datasource.select}")
	private String selecteddb;
	
	
	@GetMapping("/selecteddb")
	public Map<String, Boolean> getSelectedDB()
	{
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		
		
        String[] sl = supportdbList.split(",");
        
        
        for (String string : sl) {
			
        	map.put(string, string.equalsIgnoreCase(selecteddb));
        	
        	
		}
        
        

		
		
		
	
		
	return	map;
	}

	@Secured("ROLE_MASTER")
	@PostMapping("/multi")
	public ResponseEntity<?> addNewConfigs(@RequestBody List<MasterConfig> cfg) {
		

		try {
			for (MasterConfig masterConfig : cfg) {
				configService.addNewConfig(masterConfig.getConfigName(), masterConfig.getConfigValue());
			}
			 
			
			
			return new ResponseEntity<String>(HttpStatus.OK);
			
		} catch (Exception e) {
			e.printStackTrace();
	        return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
		
	}
}
