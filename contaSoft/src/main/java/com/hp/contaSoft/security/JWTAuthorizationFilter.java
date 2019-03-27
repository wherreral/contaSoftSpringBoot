package com.hp.contaSoft.security;

import static com.hp.contaSoft.security.SecurityConstants.HEADER_STRING;
import static com.hp.contaSoft.security.SecurityConstants.SECRET;
import static com.hp.contaSoft.security.SecurityConstants.TOKEN_PREFIX;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.hp.contaSoft.hibernate.dao.service.UserDetailsServiceImpl;


public class JWTAuthorizationFilter extends BasicAuthenticationFilter{

		private UserDetailsServiceImpl userDetailsServiceImpl;
		public JWTAuthorizationFilter(AuthenticationManager authManager, UserDetailsServiceImpl userDetailsServiceImpl) {
	        super(authManager);
	        this.userDetailsServiceImpl = userDetailsServiceImpl;
		}

	    @Override
	    protected void doFilterInternal(HttpServletRequest req,
	                                    HttpServletResponse res,
	                                    FilterChain chain) throws IOException, ServletException {
	        String header = req.getHeader(HEADER_STRING);
	        System.out.println("header:"+header);
	        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
	            chain.doFilter(req, res);
	            return;
	        }

	        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

	        SecurityContextHolder.getContext().setAuthentication(authentication);
	        chain.doFilter(req, res);
	    }

	    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
	        String token = request.getHeader(HEADER_STRING);
	        if (token != null) {
	            // parse the token.
	            String user = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
	                    .build()
	                    .verify(token.replace(TOKEN_PREFIX, ""))
	                    .getSubject();

	            UserDetails userDetails = this.userDetailsServiceImpl.loadUserByUsername(user);
	            
	            if (userDetails != null) {
	                //return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
	            	return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	            }
	            return null;
	        }
	        return null;
	    }
	
}
