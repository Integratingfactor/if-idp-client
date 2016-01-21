package com.integratingfactor.idp.lib.client.config;

import java.util.logging.Logger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

import com.integratingfactor.idp.lib.client.filter.IdpOpenIdConnectAuthenticationFilter;
import com.integratingfactor.idp.lib.client.service.IdpOpenIdConnectClient;

@Configuration
@EnableWebSecurity
@PropertySource("classpath:idp_client.properties")
public class IdpClientSecurityConfig extends WebSecurityConfigurerAdapter {
    private static Logger LOG = Logger.getLogger(IdpClientSecurityConfig.class.getName());

    @Bean
    public IdpOpenIdConnectClient idpOpenIdConnectClient() {
        LOG.info("Creating instance of IdpOpenIdConnectClient");
        return new IdpOpenIdConnectClient();
    }
    /**
     * register an external property place holder
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        LOG.info("configuring open id connect authentication filter ...");
        // http.authorizeRequests().anyRequest().authenticated()
        http.authorizeRequests().antMatchers("/**").hasAuthority(
                "USER")/*
                        * .and().addFilterBefore( new
                        * IdpOpenIdConnectAuthenticationFilter(),
                        * UsernamePasswordAuthenticationFilter.class)
                        */;
    }

    /**
     * check this if this is really needed? if yes, then will need to add client
     * side user details service for openid connect based authenticated users
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            LOG.info("Using in-memory user details service with hard coded users: \"user\" and \"admin\"");
            auth
                    // enable in memory based authentication with a user named
                    // "user" and "admin"
                    .inMemoryAuthentication().withUser("user").password("password").roles("USER");
    }
    
    /**
     * register Spring Security with existing application context
     * 
     * @author gnulib
     *
     */
    public static class SecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {

    }
}
