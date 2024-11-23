package com.veviosys.vdigit.services;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.veviosys.vdigit.classes.LdapUser;
import com.veviosys.vdigit.classes.mapSearch;
import com.veviosys.vdigit.models.MasterConfig;
import com.veviosys.vdigit.models.User;

public interface IldapService {
	
	 User getMaster();
	 void addNewConfig(String configName, String configValue);
	 String ctrypteInfos(String phrase);
	 String decrypteInfos(String cryptedPphrase);
	 MasterConfig getMasterConfig();
	 ResponseEntity setupLDAP(String host, String port, String username, String password);
	 MasterConfig checkConfig();
	 List<List<mapSearch>> getUsersFromLdap(String f);
	 List<LdapUser> addUsers(List<LdapUser> list);
	 boolean logToLdap(String username, String password, MasterConfig mConfig);
	 String hash(String password) throws Exception;
	 String active();

}
