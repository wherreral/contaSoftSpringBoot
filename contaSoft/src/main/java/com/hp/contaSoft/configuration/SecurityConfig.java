package com.hp.contaSoft.configuration;

import static com.hp.contaSoft.security.SecurityConstants.SIGN_IN_URL;
import static com.hp.contaSoft.security.SecurityConstants.SIGN_UP_URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.hp.contaSoft.hibernate.dao.service.UserDetailsServiceImpl;
import com.hp.contaSoft.security.JWTAuthenticationFilter;
import com.hp.contaSoft.security.JWTAuthorizationFilter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired private UserDetailsServiceImpl userDetailsService;
	@Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;

    /*public SecurityConfig(UserDetailsServiceImpl userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }*/

    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	 http
    	 		.cors().and()
    	 		.csrf().disable().authorizeRequests()
                // Public authentication endpoints
                .antMatchers(HttpMethod.POST, SIGN_UP_URL).permitAll()
                .antMatchers(HttpMethod.POST, SIGN_IN_URL).permitAll()
                .antMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .antMatchers(HttpMethod.POST, "/api/auth/logout").permitAll()
                .antMatchers("/login", "/register").permitAll()
                // Pages - JSP views (auth is handled via JS/JWT on API calls)
                .antMatchers(HttpMethod.GET, "/").permitAll()
                .antMatchers(HttpMethod.GET, "/home").permitAll()
                // Static resources
                .antMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()
                // API de clientes - requiere autenticación para multi-tenancy
                .antMatchers("/api/ui/clientes/**").authenticated()
                // API de templates - requiere autenticación para multi-tenancy
                .antMatchers("/api/templates/**").authenticated()
                // API de AFP - datos globales permitidos, nicknames requieren auth
                .antMatchers("/api/ui/afp/*/nickname").authenticated()
                .antMatchers("/api/ui/afp/nicknames").authenticated()
                .antMatchers("/api/ui/afp/**").permitAll()
                // API de Health/Isapres - CRUD de configuraciones
                .antMatchers("/api/ui/health/**").permitAll()
                // Configuración página
                .antMatchers("/configuracion/**").permitAll()
                // Pages served by controllers (JSP views)
                .antMatchers(HttpMethod.GET, "/clientes").permitAll()
                .antMatchers(HttpMethod.GET, "/sucursales/**").permitAll()
                .antMatchers(HttpMethod.POST, "/charges").permitAll()
                .antMatchers(HttpMethod.POST, "/importBook2").permitAll()
                .antMatchers(HttpMethod.GET, "/importBook2").permitAll()
                .antMatchers(HttpMethod.POST, "/importBookAjax").permitAll()
                .antMatchers(HttpMethod.GET, "/importBookAjax").permitAll()
                .antMatchers(HttpMethod.GET, "/api/paybookdetails").permitAll()
                .antMatchers(HttpMethod.POST, "/redirectImportBook").permitAll()
                .antMatchers(HttpMethod.GET, "/redirectImportBook").permitAll()
                // Protect all other endpoints
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new CorsFilter(), ChannelProcessingFilter.class)
                .addFilter(jwtAuthenticationFilter())
                .addFilter(new JWTAuthorizationFilter(authenticationManager(), userDetailsService))
                // Stateless session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // For API endpoints, return 401 instead of redirecting to login
                .exceptionHandling()
                    .authenticationEntryPoint((request, response, authException) -> {
                        String requestURI = request.getRequestURI();
                        if (requestURI.startsWith("/api/")) {
                            response.setStatus(401);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"success\":false,\"message\":\"No autenticado. Por favor inicie sesión.\"}");
                        } else {
                            response.sendRedirect("/login");
                        }
                    });
    }

    private JWTAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JWTAuthenticationFilter filter = new JWTAuthenticationFilter(authenticationManager());
        // Use /api/auth/login instead of /login to avoid conflict with the login page
        filter.setFilterProcessesUrl("/api/auth/login");
        return filter;
    }

    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
        
    }

    
  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
    return source;
  }
	
 
    
    /***@Bean
  public CorsFilter corsFilter() {
      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      CorsConfiguration config = new CorsConfiguration();
      config.setAllowCredentials(true);
      config.addAllowedOrigin("*");  
      config.addAllowedHeader("*");
      config.addExposedHeader(HttpHeaders.AUTHORIZATION);
      config.addAllowedMethod("*");
      source.registerCorsConfiguration("/**", config);
      return new CorsFilter(source);
  }*/
    
    
}
