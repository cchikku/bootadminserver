package com.tis.bootadminserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import de.codecentric.boot.admin.server.config.EnableAdminServer;

@Configuration
@EnableAutoConfiguration
@EnableAdminServer
public class BootadminserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(BootadminserverApplication.class, args);
	}
	 @Configuration
	    public static class SecuritySecureConfig extends WebSecurityConfigurerAdapter {
	        private final String adminContextPath;

	        public SecuritySecureConfig(AdminServerProperties adminServerProperties) {
	            this.adminContextPath = adminServerProperties.getContextPath();
	        }

	        @Override
	        protected void configure(HttpSecurity http) throws Exception {
	            // @formatter:off
	            SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
	            successHandler.setTargetUrlParameter("redirectTo");
	            successHandler.setDefaultTargetUrl(adminContextPath + "/");

	            http.authorizeRequests()
	                .antMatchers(adminContextPath + "/assets/**").permitAll() // <1>
	                .antMatchers(adminContextPath + "/login").permitAll()
	                .anyRequest().authenticated() // <2>
	                .and()
	            .formLogin().loginPage(adminContextPath + "/login").successHandler(successHandler).and() // <3>
	            .logout().logoutUrl(adminContextPath + "/logout").and()
	            .httpBasic().and() // <4>
	            .csrf()
	                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())  // <5>
	                .ignoringAntMatchers(
	                    "/instances",   // <6>
	                    "/actuator/**"  // <7>
	                );
	            // @formatter:on
	        }
	}
}
