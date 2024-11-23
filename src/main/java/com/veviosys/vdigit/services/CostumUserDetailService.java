package com.veviosys.vdigit.services;

import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.Getter;

@Service

public class CostumUserDetailService implements UserDetailsService {
	@Autowired
	private UserRepository us;
	@Getter
	CostumUserDetails cud;
	@Getter
	User user;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		user = us.findByUsernameIgnoreCase(username);

		if (user == null) {

			throw new UsernameNotFoundException(username + " non trouvée");
		}

		cud = new CostumUserDetails(user);

		return cud;
	}

}
