package com.hp.contaSoft.custom;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import com.hp.contaSoft.hibernate.entities.AppUser;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CurrentUser extends AppUser implements UserDetails{

	private static final long serialVersionUID = 1L;
	private AppUser user;
	private String familId;
	private Collection<? extends GrantedAuthority> authorities;

	public CurrentUser(AppUser user) {
		super();
		this.user = user;
		this.authorities = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");
	}

	public CurrentUser(AppUser user, String familId) {
		super();
		this.user = user;
		this.familId = familId;
		this.authorities = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");
	}

	public CurrentUser(AppUser user, String familId, Collection<? extends GrantedAuthority> authorities) {
		super();
		this.user = user;
		this.familId = familId;
		this.authorities = authorities;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public String getPassword() {
		return this.user.getPassword();
	}

	@Override
	public String getUsername() {
		return this.user.getUsername();
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

}
