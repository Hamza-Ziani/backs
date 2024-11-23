package com.veviosys.vdigit.services;

import java.util.List;
import java.util.Objects;

import com.google.common.hash.Hashing;
import com.veviosys.vdigit.classes.LdapUser;
import com.veviosys.vdigit.classes.mapClass;
import com.veviosys.vdigit.classes.mapSearch;
import com.veviosys.vdigit.models.Contact;
import com.veviosys.vdigit.models.MasterConfig;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.repositories.MasterConfigRepository;
import com.veviosys.vdigit.repositories.RoleRepo;
import com.veviosys.vdigit.repositories.UserRepository;

import org.apache.catalina.connector.Response;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.DefaultDirObjectFactory;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Service;

import io.netty.handler.codec.base64.Base64Encoder;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Properties;
import javax.naming.Context;


public class ldapService {

    @Autowired
    MasterConfigRepository configRepository;
    @Autowired
    UserRepository ur;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Value("${ldap.searchContext}")
    private String searchCon;
    @Value("${interface.completion}")
    private String pw;
    @Value("${ldap.search.context}")
    private String ctxSearch;

	@Autowired
	private RoleRepo rr;
    public User getMaster() {
        CostumUserDetails user = (CostumUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return user.getUser();
    }

    @Secured("ROLE_MASTER")
    public void addNewConfig(String configName, String configValue) {

        MasterConfig config = configRepository.findByMasterUserIdAndConfigName(getMaster().getUserId(), configName);

        if (Objects.isNull(config))
            config = new MasterConfig();
        config.setMaster(getMaster());
        config.setConfigName(configName);
        config.setConfigValue(configValue);

        configRepository.save(config);
    }

    public String ctrypteInfos(String phrase) {
        String cryptedPhrase = "";
        int pLenght = phrase.length();
        for (int i = 0; i < pLenght; i++) {
            cryptedPhrase += String.valueOf((phrase.charAt(i) + 5));
            if (i < pLenght - 1)
                cryptedPhrase += ",";
        }
        return Base64.getEncoder().encodeToString(cryptedPhrase.getBytes());
    }

    public String decrypteInfos(String cryptedPphrase) {
        char c;
        int code;
        String phrase = new String(Base64.getDecoder().decode(cryptedPphrase));
        // .println(phrase);
        String[] asciis = phrase.split(",");
        int pLenght = asciis.length;
        phrase = "";
        for (int i = 0; i < pLenght; i++) {
            code = Integer.valueOf(asciis[i]) - 5;
            c = (char) code;
            phrase += String.valueOf(c);

        }
        return phrase;
    }

    private MasterConfig getMasterConfig() {
        return configRepository.findByMasterUserIdAndConfigName(getMaster().getUserId(), "LDAP_CONFIG");
    }

    @Secured("ROLE_MASTER")
    public ResponseEntity setupLDAP(String host, String port, String username, String password) {

        try {
            LdapTemplate sourceLdapTemplate = new LdapTemplate();
            LdapContextSource sourceLdapCtx = new LdapContextSource();
            String server = "ldap://" + host + ":" + port;
            // .println(server + username + password);

            sourceLdapCtx.setUrl(server);
            sourceLdapCtx.setUserDn(username);
            sourceLdapCtx.setPassword(password);
            sourceLdapCtx.setDirObjectFactory(DefaultDirObjectFactory.class);
            sourceLdapCtx.afterPropertiesSet();
            sourceLdapTemplate = new LdapTemplate(sourceLdapCtx);
            sourceLdapTemplate.getContextSource().getContext(username, password);

            addNewConfig("LDAP_CONFIG", server + "#" + username + "#" + ctrypteInfos(password));

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {

            // .println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS);

        }

    }

    public MasterConfig checkConfig() {
        MasterConfig config = configRepository.findByMasterUserIdAndConfigName(getMaster().getUserId(), "LDAP_CONFIG");
        if (Objects.isNull(config))
            return config;
        else {
        	String value = "";
        	String[] oldValue=config.getConfigValue().split("#");
        	int len = oldValue.length;
        	int i =0;
        	for (String string : oldValue) {
				
        		if(i==len-1)
        		{
        			
        			value+="*******";
        		}
        		else
        			value+=string+"#";
        		
        		i++;
			}
        	config.setConfigValue(value);
        	
        	return config;
        }
    }

    @Secured("ROLE_MASTER")
    public List<List<mapSearch>> getUsersFromLdap(String f) {

        try {
            MasterConfig mConfig = getMasterConfig();
            if (Objects.nonNull(mConfig)) {
                String[] config = mConfig.getConfigValue().split("#");

                Properties initialProperties = new Properties();
                initialProperties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
                initialProperties.put(Context.PROVIDER_URL, config[0]);
                initialProperties.put(Context.SECURITY_PRINCIPAL, config[1]);
                initialProperties.put(Context.SECURITY_CREDENTIALS, decrypteInfos(config[2]));

                DirContext context = new InitialDirContext(initialProperties);

                String searchFilter = "(&(sAMAccountName=*)(objectClass=person))";
                if (!ctxSearch.equals("")) {
                    searchFilter = ctxSearch;
                }
                SearchControls controls = new SearchControls();
                String searchCont=searchCon;
                if(Objects.nonNull(f)&&!f.equals("")){
                    searchCont="OU="+f+","+searchCont;
                }
                controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
                // controls.setReturningAttributes(requiredAttributes);
                NamingEnumeration data = context.search(searchCont, searchFilter, controls);

                List<List<mapSearch>> users = new ArrayList<List<mapSearch>>();
                SearchResult searchResult = null;
                while (data.hasMore()) {
                    System.out.println(" users :");
                    searchResult = (SearchResult) data.next();
                    Attributes attr = searchResult.getAttributes();
                    if (Objects.nonNull(attr.get("cn")) && Objects.nonNull(attr.get("sAMAccountName"))
                            && Objects.nonNull(attr.get("mail"))) {
                        List<mapSearch> user = new ArrayList<mapSearch>();
                        user.add(new mapSearch("cn", attr.get("cn").get(0).toString(),""));
                        // user.add(new mapSearch("sn", attr.get("sn").get(0).toString()));
                        user.add(new mapSearch("sAMAccountName", attr.get("sAMAccountName").get(0).toString(),""));
                        user.add(new mapSearch("mail", attr.get("mail").get(0).toString(),""));
                        user.add(new mapSearch("selected", "0",""));

                        users.add(user);
                    }
                }

                return users;
            } else {
                System.out.println(" null");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Secured("ROLE_MASTER")
    public List<LdapUser> addUsers(List<LdapUser> list) {
        List<LdapUser> ret = new ArrayList<LdapUser>();
        User master = getMaster();
        for (LdapUser lu : list) {

            if (ur.findByEmailIgnoreCase(lu.mail) == null && ur.findByMasterAndMatIgnoreCase(getMaster(), "") == null
                    && ur.findByMasterAndFullNameIgnoreCase(getMaster(), lu.fullname) == null) {

                User u = new User();
                u.setMaster(master);
                u.setContact(new Contact());
                u.setUsername(lu.username);
                u.setEmail(lu.mail);
                u.setFullName(lu.fullname);
                u.setFromLdap(1);
                u.setParent(new Long(0));
                u.setRoles(rr.findByRole("USER"));
                // u.setDateCreation()
                u.setPassword(passwordEncoder.encode(new String(Base64.getDecoder().decode(pw))));
                ur.save(u);

            } else {
                ret.add(lu);
            }

        }
        if (ret.size() > 0)
            return ret;
        return null;
    }

    public boolean logToLdap(String username, String password, MasterConfig mConfig) {

        if (Objects.nonNull(mConfig)) {
            String[] config = mConfig.getConfigValue().split("#");

            Properties authEnv = new Properties();
            authEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            authEnv.put(Context.PROVIDER_URL,  config[0]);
            username="cnops\\"+username.replace("cnops\\","").replace("cnops/","");
            authEnv.put(Context.SECURITY_PRINCIPAL,username);
            authEnv.put(Context.SECURITY_CREDENTIALS, password);
            try {
                new InitialDirContext(authEnv);
                return true;
            } catch (NamingException e) {
                e.printStackTrace();
                return false;

            }
        }else return false;

    }

    public static String hash(String password) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(password.getBytes("UTF8"));
        String sha256password = Base64.getEncoder().encodeToString(digest.digest());
        return "{SHA256}" + sha256password;

    }

}
