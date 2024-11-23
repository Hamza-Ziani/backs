package com.veviosys.vdigit.configuration;

import java.util.ArrayList;
import java.util.Base64; 
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import com.veviosys.vdigit.models.MasterConfig;
import com.veviosys.vdigit.models.TempSession;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.repositories.MasterConfigRepository;
import com.veviosys.vdigit.repositories.TempSessionRepo;
import com.veviosys.vdigit.repositories.UserRepository;
import com.veviosys.vdigit.services.CostumUserDetailService;
import com.veviosys.vdigit.services.CostumUserDetails;
import com.veviosys.vdigit.services.IldapService;
import com.veviosys.vdigit.services.ldapService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.DefaultDirObjectFactory;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority; 
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; 

public class CustomAuthenticationProvider extends DaoAuthenticationProvider {
    @Autowired

    CostumUserDetailService cuds;

    @Autowired
    UserRepository ur;

    @Value("${interface.completion}")
    private String pw;
    @Autowired
    IldapService serviceLdap;
    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    @Autowired
    MasterConfigRepository configRepository;
    @Autowired
    TempSessionRepo tempSessionRepo;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private MySessionRegistry sessionRegistry;
    @Value("${tentative.number}")
	int tentative;
    public CustomAuthenticationProvider() {
        super();
    }
    public void cleanSession(String name) {
        List<Object> users = sessionRegistry.getAllPrincipals();
      

        for (Object object : users) {
            CostumUserDetails us = (CostumUserDetails) object;

            if (us.getUsername().equals(name)) {

                List<SessionInformation> infos = sessionRegistry.getAllSessions(us, false);
              
                for (SessionInformation info : infos) {
                    TempSession tmp = new TempSession();
                    tmp.setUserId(us.getUser().getUserId());
                    tmp.setSession(info.getSessionId());
                    tmp.setUsername(us.getUser().getUsername());
                     tempSessionRepo.save(tmp);
                    info.expireNow();
                    sessionRegistry.removeSessionInformation(info.getSessionId());
                }
            }
        }
  
  
    }


    @Transactional
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    	 
    	 try {
    		    String name = authentication.getName();
    	        Object password = authentication.getCredentials();
    	        if(password.toString().length() == 0)
    	            return null;
    	        name = name.replace("cnops\\", "").replace("cnops/", "");
    	        UserDetails cud = cuds.loadUserByUsername(name);
    	        User u = cuds.getUser();
    	        List<GrantedAuthority> authorities = new ArrayList<>();
    	        u.getRoles().forEach(r -> {
    	            GrantedAuthority auth = new SimpleGrantedAuthority("ROLE_" + r.getRole());
    	            authorities.add(auth);

    	        });
    	       
    	        /// CHECK USER FROM LDAP
    	        int s = calculateMaxSessions(sessionRegistry, name);

    	        if (u.getTentativeNumber() < tentative)
    	        {
  	          
    		        if (u.getFromLdap() == 1) {
        	            // LOG TO LDAP CHECK

        	            if (serviceLdap.logToLdap(name, password.toString(), getMasterConfig(u.getMaster()))) {
        	                password = new String(Base64.getDecoder().decode(pw));
        	                
        	                u.setTentativeNumber(0);
                            userRepository.save(u);
        	                return new UsernamePasswordAuthenticationToken(cud, password, authorities);
        	            } else {
        	                
        	                u.setTentativeNumber(u.getTentativeNumber() + 1);
                            userRepository.save(u);
                            
        	                return null;
        	            }
        	        } else {// SIMPLE LOGIN

        	            if (passwordEncoder.matches(password.toString(), u.getPassword())) {

        	              

        	                cleanSession(name);
        	                u.setTentativeNumber(0);
                            userRepository.save(u);
        	                return new UsernamePasswordAuthenticationToken(cud, password, authorities);
        	            } else {
        	            	 
        	                u.setTentativeNumber(u.getTentativeNumber() + 1);
        	                userRepository.save(u);
        	                
        	                return null;
        	            }
        	        }
    	        }
    	        else {
    	        	return null; 
    	        }
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
      

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    private MasterConfig getMasterConfig(User r) {
        return configRepository.findByMasterUserIdAndConfigName(r.getUserId(), "LDAP_CONFIG");
    }

    public boolean checkLoginToLdap(String username, String password, User master) {

        try {
            String server = getMasterConfig(master).getConfigValue().split("#")[0];
            LdapTemplate sourceLdapTemplate = new LdapTemplate();
            LdapContextSource sourceLdapCtx = new LdapContextSource();
            // .println("-------------- ccccccdaap");
            // System.out.println(server + username + password);

            sourceLdapCtx.setUrl(server);
            // cn=administrateur,cn=Users,dc=master,dc=local
            // sourceLdapCtx.setUserDn("cn="+username+",cn=Users,dc=master,dc=local");
            sourceLdapCtx.setUserDn("CN=azdin darikh,CN=Users,DC=master,DC=local");
            // sourceLdapCtx.getus

            // String userNameWithDomain = String.Format("{0}@{1}",username ,doamain);

            sourceLdapCtx.setPassword(password);
            sourceLdapCtx.setDirObjectFactory(DefaultDirObjectFactory.class);
            sourceLdapCtx.afterPropertiesSet();
            sourceLdapTemplate = new LdapTemplate(sourceLdapCtx);
            sourceLdapTemplate.getContextSource().getContext(username, password);
            // .println("-------------- logged to ldaap");
            return true;
        } catch (Exception e) {
            // System.out.println("--------------exeeep");

            // .println(e.getMessage());
            return false;
        }
    }

    public int calculateMaxSessions(SessionRegistry sessionRegistry, String uname) {
        final List<Object> principals = sessionRegistry.getAllPrincipals();
        if (principals != null) {
          

            List<SessionInformation> sessions = new ArrayList<>();
            for (Object principal : principals) {
                CostumUserDetails us = (CostumUserDetails) principal;
                if (uname.equals(us.getUsername()))
                    sessions.addAll(sessionRegistry.getAllSessions(principal, false));
            }
            return sessions.size();
        }
     

        return 0;
    }
}