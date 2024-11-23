package com.veviosys.vdigit.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.veviosys.vdigit.repositories.MasterConfigRepository;
import com.veviosys.vdigit.repositories.UserRepository;
import com.veviosys.vdigit.services.CostumUserDetailService;
import com.veviosys.vdigit.services.IldapService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;



@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private CostumUserDetailService cuds;
	
	@Value("${csrf.active}")
    Boolean csrfEnabled;
	  
	  
	@Value("${urls}")
	private String urls;
	// @Autowired
	// CustomAuthenticationProvider customAuthProvider;
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		auth.authenticationProvider(authenticationProvider()); 
	}



	


	@Override
	protected void configure(HttpSecurity http) throws Exception {

		// http.headers().
	    http.cors().configurationSource(new CorsConfigurationSource() {

            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration config = new CorsConfiguration();
                List<String> origins = new ArrayList<>();
                Collections.addAll(origins, urls.split(","));
                
                config.setAllowedHeaders(Arrays.asList(HttpHeaders.AUTHORIZATION,
                        HttpHeaders.CONTENT_TYPE,
                        HttpHeaders.ACCEPT,
                        "Authno",
                        "X-XSRF-TOKEN",
                        "X-Requested-With",
                        "secondary"));
                config.setAllowedMethods(Arrays.asList(
                        HttpMethod.GET.toString(),
                        HttpMethod.POST.toString(),
                        HttpMethod.DELETE.toString(),
                        HttpMethod.PUT.toString(),
                        HttpMethod.PATCH.toString(),
                        HttpMethod.OPTIONS.toString()));
                //config.setAllowedOriginPatterns(Collections.singletonList("*"));
                config.setAllowedOrigins(origins);
                //config.setAllowCredentials(true);
            
                return config;
            }
        });
	 	http.headers().frameOptions().sameOrigin().httpStrictTransportSecurity().disable();
	 	http.cors();
	 	if(csrfEnabled)
	        http .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()); 
	        else
	            http .csrf().disable();
	        

	            http.addFilterBefore(new AuthFilter(),BasicAuthenticationFilter.class);
	            http.authorizeRequests().antMatchers("/api/v2/records/**").permitAll();
	            http.authorizeRequests().antMatchers("/api/v1/api/csrf").permitAll();
				http.authorizeRequests().antMatchers("/api/v1/**").authenticated()
				.and().httpBasic()  .and()          .formLogin().and()
				.logout().clearAuthentication(true).logoutRequestMatcher(new AntPathRequestMatcher("/logout")) 
				 
				.deleteCookies("JSESSIONID")
				.invalidateHttpSession(true);
			 
			 
				 
				// http.sessionManagement( ).maximumSessions( -1 ).sessionRegistry( sessionRegistry( ) );
				// http.sessionManagement( ).sessionFixation( ).migrateSession( )
				// 		.sessionAuthenticationStrategy( registerSessionAuthStr( ) );
			http	 
 
				.sessionManagement()
                .maximumSessions(1)
                .sessionRegistry(sessionRegistry)
                .maxSessionsPreventsLogin(true);
	}

	
	




	@Autowired
    MySessionRegistry sessionRegistry; 
	@Bean
	public CustomAuthenticationProvider authenticationProvider() throws Exception {
	 
		CustomAuthenticationProvider provider = new CustomAuthenticationProvider();
		 provider.setPasswordEncoder(encodePWD());
		provider.setUserDetailsService(cuds);
	 
		return provider;
	}

	@Bean
	public BCryptPasswordEncoder encodePWD() {
		return new BCryptPasswordEncoder();
	}

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
	@Bean
	public RegisterSessionAuthenticationStrategy registerSessionAuthStr( ) {
		return new RegisterSessionAuthenticationStrategy( sessionRegistry( ) );
	}
    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<HttpSessionEventPublisher>(new HttpSessionEventPublisher());
    }
	@Autowired
	SecurityFilter securityFilter;
	@Bean
    public FilterRegistrationBean dawsonApiFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(securityFilter);
        return registration;
    }
}