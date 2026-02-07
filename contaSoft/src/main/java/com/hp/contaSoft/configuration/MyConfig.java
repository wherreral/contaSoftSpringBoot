package com.hp.contaSoft.configuration;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//import com.hp.contaSoft.interceptors.TestInterceptor;


@Configuration
//@EnableGlobalMethodSecurity(securedEnabled = false)
public class MyConfig implements WebMvcConfigurer{

	
	@Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

	@Bean
	public FilterRegistrationBean<HiddenHttpMethodFilter> hiddenHttpMethodFilter() {
		FilterRegistrationBean<HiddenHttpMethodFilter> filterRegistrationBean = new FilterRegistrationBean<>(new HiddenHttpMethodFilter());
		filterRegistrationBean.setEnabled(true);
		return filterRegistrationBean;
	}

	@Override
    public void addInterceptors(InterceptorRegistry registry) {
        //registry.addInterceptor(new TestInterceptor());
    }
	
	/*@Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
        .allowedMethods("GET","POST")
        .allowedOrigins("http://localhost:3000")
        .allowCredentials(false);
    }
	*/
	
	  
	
	
}
