package com.hp.contaSoft.security;

import static com.hp.contaSoft.security.SecurityConstants.HEADER_STRING;
import static com.hp.contaSoft.security.SecurityConstants.SECRET;
import static com.hp.contaSoft.security.SecurityConstants.TOKEN_PREFIX;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
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
	        
	        // If no Authorization header, try to read JWT from cookie
	        if (header == null || !header.startsWith(TOKEN_PREFIX) || header.equals(TOKEN_PREFIX + "null") || header.trim().equals(TOKEN_PREFIX.trim())) {
	            String cookieToken = getTokenFromCookie(req);
	            if (cookieToken != null) {
	                header = TOKEN_PREFIX + cookieToken;
	            }
	        }
	        
	        // Validate that the header exists and has the correct prefix
	        if (header == null || !header.startsWith(TOKEN_PREFIX) || header.equals(TOKEN_PREFIX + "null") || header.trim().equals(TOKEN_PREFIX.trim())) {
	            chain.doFilter(req, res);
	            return;
	        }
	        
	        // Validate token format (3 parts separated by .)
	        String tokenValue = header.replace(TOKEN_PREFIX, "").trim();
	        if (tokenValue.isEmpty() || tokenValue.equals("null") || tokenValue.split("\\.").length != 3) {
	            System.out.println("Token inválido o malformado, continuando sin autenticación");
	            chain.doFilter(req, res);
	            return;
	        }

	        try {
	            UsernamePasswordAuthenticationToken authentication = getAuthentication(header);
	            SecurityContextHolder.getContext().setAuthentication(authentication);
	        } catch (Exception e) {
	            System.out.println("Error al validar token JWT: " + e.getMessage());
	            SecurityContextHolder.clearContext();
	            // Limpiar cookie JWT inválida para evitar loops de redirección
	            Cookie invalidCookie = new Cookie("JWT_TOKEN", "");
	            invalidCookie.setHttpOnly(true);
	            invalidCookie.setPath("/");
	            invalidCookie.setMaxAge(0);
	            res.addCookie(invalidCookie);
	        }
	        chain.doFilter(req, res);
	    }
	    
	    private String getTokenFromCookie(HttpServletRequest request) {
	        Cookie[] cookies = request.getCookies();
	        if (cookies != null) {
	            for (Cookie cookie : cookies) {
	                if ("JWT_TOKEN".equals(cookie.getName())) {
	                    String value = cookie.getValue();
	                    if (value != null && !value.isEmpty() && !value.equals("null")) {
	                        return value;
	                    }
	                }
	            }
	        }
	        return null;
	    }

	    private UsernamePasswordAuthenticationToken getAuthentication(String token) {
	        if (token != null) {
	            // parse the token.
	            String user = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
	                    .build()
	                    .verify(token.replace(TOKEN_PREFIX, ""))
	                    .getSubject();

	            UserDetails userDetails = this.userDetailsServiceImpl.loadUserByUsername(user);
	            
	            if (userDetails != null) {
	            	return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	            }
	            return null;
	        }
	        return null;
	    }
	
}
