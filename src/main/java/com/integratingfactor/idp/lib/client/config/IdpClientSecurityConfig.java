package com.integratingfactor.idp.lib.client.config;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

import com.integratingfactor.idp.lib.client.filter.IdpAuthenticationFailureHandler;
import com.integratingfactor.idp.lib.client.filter.IdpOpenIdConnectAuthenticationEntryPoint;
import com.integratingfactor.idp.lib.client.filter.IdpOpenIdConnectAuthenticationFilter;
import com.integratingfactor.idp.lib.client.service.IdpOpenIdConnectClient;

@Configuration
@EnableWebSecurity
@PropertySource("classpath:idp_client.properties")
public class IdpClientSecurityConfig extends WebSecurityConfigurerAdapter {
    private static Logger LOG = Logger.getLogger(IdpClientSecurityConfig.class.getName());

    @Autowired
    IdpOpenIdConnectAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    IdpOpenIdConnectAuthenticationFilter authenticationFilter;

    @Autowired
    IdpAuthenticationFailureHandler failureHandler;

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public IdpAuthenticationFailureHandler idpAuthenticationFailureHandler() {
        LOG.info("Creating instance of IdpAuthenticationFailureHandler");
        return new IdpAuthenticationFailureHandler();
    }

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

    @Bean
    public IdpOpenIdConnectAuthenticationEntryPoint idpOpenIdConnectAuthenticationEntryPoint() {
        LOG.info("Creating instance of IdpOpenIdConnectAuthenticationEntryPoint");
        return new IdpOpenIdConnectAuthenticationEntryPoint();
    }

    @Bean
    public IdpOpenIdConnectAuthenticationFilter idpOpenIdConnectAuthenticationFilter() {
        LOG.info("Creating instance of IdpOpenIdConnectAuthenticationFilter");
        return new IdpOpenIdConnectAuthenticationFilter();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // LOG.info("Registering login url: " +
        // IdpOpenIdConnectClient.pathSuffixLogin);
        // http.formLogin().loginPage(IdpOpenIdConnectClient.pathSuffixLogin);

        LOG.info("Registering authentication on all urls, except: " + "/, /resources/**, /about/**");
        http.authorizeRequests().antMatchers("/", "/resources/**", "/about").permitAll().anyRequest().authenticated();

        LOG.info("Registering custom AuthenticationEntryPoint: " + authenticationEntryPoint);
        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);

        LOG.info("Registering logout url: " + IdpOpenIdConnectClient.pathSuffixLogout);
        http.logout().logoutUrl(IdpOpenIdConnectClient.pathSuffixLogout);

        LOG.info("Registering logout success url: " + "/");
        http.logout().logoutSuccessUrl("/");

        LOG.info("Registering custom AuthenticationFilter: " + authenticationFilter);
        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // LOG.info("configuring open id connect authentication filter ...");
        // http.formLogin().loginPage(IdpOpenIdConnectClient.pathSuffixLogin).and().logout()
        // .logoutUrl(IdpOpenIdConnectClient.pathSuffixLogout).and().authorizeRequests()
        // /*
        // * .antMatchers(IdpOpenIdConnectClient.pathSuffixLogin,
        // * IdpOpenIdConnectClient.pathSuffixLogout) .permitAll()
        // */.anyRequest().authenticated()
        // /*http.authorizeRequests().antMatchers("/**").hasAuthority("USER")*/.and().addFilterBefore(
        // new IdpOpenIdConnectAuthenticationFilter(),
        // UsernamePasswordAuthenticationFilter.class)
        // // disable CSRF protection for openid connect based
        // // authentication
        // .csrf().disable();
    }

    /**
     * check this if this is really needed? if yes, then will need to add client
     * side user details service for openid connect based authenticated users
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // LOG.info("Using in-memory user details service with hard coded users:
        // \"user\" and \"admin\"");
        // auth.inMemoryAuthentication().withUser("user").password("password").roles("USER");
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
