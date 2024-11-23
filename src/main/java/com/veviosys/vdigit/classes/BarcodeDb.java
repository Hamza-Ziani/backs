package com.veviosys.vdigit.classes;
 

import lombok.Data;

@Data
public class BarcodeDb {

	private String app;

	private String host;

	private String port;

	private String user;

	private String pass;

	private String dbName;

	private String table;

	private String column;

	/*
	 * public String ToString(){ return
	 * this.app+"|"+this.host+"|"+this.port+"|"+this.pass+"|"+this.dbName+"|"+this.
	 * table+"|"+this.column;
	 * 
	 * }
	 */
	public void ToObject(String configval) {
		String[] fields = configval.split("[|]");
		//.println(configval.split("[|]")[0]);
		this.app = fields[0];
		this.host = fields[1];
		this.port = fields[2];
		this.user = fields[3];
		this.pass = fields[4];
		this.dbName = fields[5];
		this.table = fields[6];
		this.column = fields[7];

	}

}
