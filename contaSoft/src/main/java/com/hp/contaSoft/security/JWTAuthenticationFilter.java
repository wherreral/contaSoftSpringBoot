package com.hp.contaSoft.security;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.hp.contaSoft.security.SecurityConstants.EXPIRATION_TIME;
import static com.hp.contaSoft.security.SecurityConstants.HEADER_STRING;
import static com.hp.contaSoft.security.SecurityConstants.SECRET;
import static com.hp.contaSoft.security.SecurityConstants.TOKEN_PREFIX;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.contaSoft.custom.CurrentUser;
import com.hp.contaSoft.hibernate.entities.AppUser;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter{

	private AuthenticationManager authenticationManager;
	
	public Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
    
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
        	
        	System.out.println("pase por aca");
        	logger.info("info message");
            AppUser creds = new ObjectMapper().readValue(req.getInputStream(), AppUser.class);
            System.out.println("creds:"+creds);
            
            //Experimento
            //CurrentUser current = new CurrentUser(creds.getUsername(), creds.getPassword(), "asd", new ArrayList<>());
            CurrentUser current = new CurrentUser(new AppUser(creds.getUsername(), creds.getPassword()),"");
            
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                    		current,
                    		//creds.getUsername(),
                            creds.getPassword(),
                            new ArrayList<>())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {

    	/*
    	 * 0. The client has autenticated successfully so, i shoul load the family Id
    	 * 
    	 */
    	
    	
        String token = JWT.create()
                .withSubject(((CurrentUser) auth.getPrincipal()).getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .withIssuer(((CurrentUser) auth.getPrincipal()).getUsername())
                .withClaim("family", ((CurrentUser) auth.getPrincipal()).getFamilId())
                .withClaim("name", ((CurrentUser) auth.getPrincipal()).getUsername())
                .sign(HMAC512(SECRET.getBytes()));
        System.out.println(((CurrentUser) auth.getPrincipal()).getUsername());
        System.out.println("TOKEN:"+token);
        res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
        res.addHeader("Access-Control-Expose-Headers", "Authorization");
    }
	
}
