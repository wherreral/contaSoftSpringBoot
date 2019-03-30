package com.hp.contaSoft.custom;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.hp.contaSoft.hibernate.entities.AppUser;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CurrentUser extends AppUser implements UserDetails{

	private static final long serialVersionUID = 1L;
	private  AppUser user;
	private String familId;
	
	
	public CurrentUser(AppUser user) {
		super();
		this.user = user;
	}
	
	public CurrentUser(AppUser user, String familId) {
		super();
		this.user = user;
		this.familId = familId;
	}
	

	

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_ADMIN");
	}




	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return this.user.getPassword();
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return this.user.getUsername();
	}


	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override

	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	


	/*
	public CurrentUser(String username, String password, boolean enabled, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		// TODO Auto-generated constructor stub
	}

	public CurrentUser(String username, String password, String familyId, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
		this.familId = familyId;

		// TODO Auto-generated constructor stub
	}
*/
	
	
	
}
