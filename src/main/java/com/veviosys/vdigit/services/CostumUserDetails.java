package com.veviosys.vdigit.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.veviosys.vdigit.configuration.AskToExpireSessionEvent;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.repositories.PasswordRepo;

import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
//import org.springframework.security.core.authority.mapping.SimpleAttributes2GrantedAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CostumUserDetails implements UserDetails {

	private static final long serialVersionUID = 1256711395932122675L;
	private User user;
	@Autowired
	private PasswordRepo passwordRepo;

	CostumUserDetails() {
	}

	public CostumUserDetails(User user) {

		this.user = user;

	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		List<GrantedAuthority> authorities = new ArrayList<>();
		this.user.getRoles().forEach(r -> {
			GrantedAuthority auth = new SimpleGrantedAuthority("ROLE_" + r.getRole());
			authorities.add(auth);

		});
		return authorities;

	}

	@Override
	public String getPassword() {		 
		return user.getPassword();
	}
  
	@Override
	public String getUsername() {

		return user.getUsername();
	} 

	@Override
	public boolean isAccountNonExpired() {

		return true;
	}

	@Override
	public boolean isAccountNonLocked() {

		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {

		return true;
	}

	@Override
	public boolean isEnabled() {

		return true;
	}
	@Autowired(required = false)
      private Set<SessionRegistry> sessionRegistries;
	  

//       @Autowired
//       private ApplicationEventPublisher publisher;

// 	public boolean invalidateUserByUserName(final String userName){
	 
// 		if(StringUtils.isEmpty(userName)||Objects.isNull(userName) ) {
// 			throw new IllegalArgumentException("userName must not be null or empty");
// 	}
// 	boolean expieredAtLeastOneSession = false;
// 	for (final SessionRegistry sessionRegistry :sessionRegistries) {
// 			findPrincipal: for (final Object principal : sessionRegistry.getAllPrincipals()) {
// 					if(principal instanceof CostumUserDetails) {
// 							final CostumUserDetails user = (CostumUserDetails) principal;
// 							if(userName.equals(user.getUsername())) {
// 									for (final SessionInformation session : sessionRegistry.getAllSessions(user, true)) {
// 											session.expireNow();
// 											sessionRegistry.removeSessionInformation(session.getSessionId());
// 											publisher.publishEvent(AskToExpireSessionEvent.of(session.getSessionId()));
// 											expieredAtLeastOneSession = true;
// 									}
// 									break findPrincipal;
// 							}
// 					} else {
// 							// logger.warn("encountered a session for a none user object {} while invalidating '{}' " , principal, userName);
// 					}
// 			}
// 	}
// 	return expieredAtLeastOneSession;
// }




}


