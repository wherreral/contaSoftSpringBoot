package com.hp.contaSoft.hibernate.dao.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hp.contaSoft.custom.CurrentUser;
import com.hp.contaSoft.hibernate.dao.repositories.UserRepository;
import com.hp.contaSoft.hibernate.entities.AppUser;


@Service
public class UserDetailsServiceImpl implements UserDetailsService{
	
	@Autowired private UserRepository applicationUserRepository;
	public Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    /*public UserDetailsServiceImpl(UserRepository applicationUserRepository) {
        this.applicationUserRepository = applicationUserRepository;
    }*/

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
    	logger.info("UserDetailsServiceImpl username:"+username);
    	
    	AppUser applicationUser = applicationUserRepository.findByUsername(username);
        if (applicationUser == null) {
        	System.out.println("NO ENCONTRO");
            throw new UsernameNotFoundException(username);
        }
        logger.info("UserDetailsServiceImpl:"+applicationUser);
        
        //return buildUserForAuthentication(applicationUser, authorities);
        
        //return new User(applicationUser.getUsername(), applicationUser.getPassword(), Collections.emptyList());
        return new CurrentUser(
        		applicationUser, applicationUser.getGroupCredentials().getGcId());
    }
	
    public Collection<? extends GrantedAuthority> getAuthorities(Integer role) {
		List<GrantedAuthority> authList = getGrantedAuthorities(getRoles(role));
		return authList;
	}
    
    /**
	 * Converts a numerical role to an equivalent list of roles
	 * @param role the numerical role
	 * @return list of roles as as a list of {@link String}
	 */
	public List<String> getRoles(Integer role) {
		List<String> roles = new ArrayList<String>();
		
		if (role.intValue() == 1) {
			roles.add("ROLE_USER");
			roles.add("ROLE_ADMIN");
			
		} else if (role.intValue() == 2) {
			roles.add("ROLE_USER");
		}
		
		return roles;
	}
    
	/**
	 * Wraps {@link String} roles to {@link SimpleGrantedAuthority} objects
	 * @param roles {@link String} of roles
	 * @return list of granted authorities
	 */
	public static List<GrantedAuthority> getGrantedAuthorities(List<String> roles) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		for (String role : roles) {
			authorities.add(new SimpleGrantedAuthority(role));
		}
		return authorities;
	}
}
