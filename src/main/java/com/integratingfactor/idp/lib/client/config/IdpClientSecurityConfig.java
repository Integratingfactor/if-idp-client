package com.integratingfactor.idp.lib.client.config;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.util.StringUtils;

import com.integratingfactor.idp.lib.client.filter.IdpApiAuthFilter;
import com.integratingfactor.idp.lib.client.filter.IdpApiRbacFilter;
import com.integratingfactor.idp.lib.client.filter.IdpOpenIdConnectAuthenticationEntryPoint;
import com.integratingfactor.idp.lib.client.filter.IdpOpenIdConnectAuthenticationFilter;
import com.integratingfactor.idp.lib.client.rbac.IdpRbacService;
import com.integratingfactor.idp.lib.client.util.IdpOauthClient;
import com.integratingfactor.idp.lib.client.util.IdpOpenIdConnectClient;

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
    IdpClientAuthProperties clientProperties;

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public IdpRbacService IdpRbacService() {
        LOG.info("Creating instance of IdpRbacService");
        return new IdpRbacService();
    }

    @Bean
    public IdpClientAuthProperties clientAuthProperties() {
        LOG.info("Creating instance of IdpClientAuthProperties");
        return new IdpClientAuthProperties();
    }

    @Bean
    public IdpApiAuthFilter idpApiAuthFilter() {
        LOG.info("Creating instance of IdpApiAuthFilter");
        return new IdpApiAuthFilter();
    }

    @Bean
    public IdpApiRbacFilter idpApiRbacFilter() {
        LOG.info("Creating instance of IdpApiRbacFilter");
        return new IdpApiRbacFilter();
    }

    @Bean
    public IdpOauthClient idpOauthClient() {
        LOG.info("Creating instance of IdpOauthClient");
        return new IdpOauthClient();
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
        String[] whiteListUrls;
        if (clientProperties.getMiscProp("idp.client.api.path") != null) {
            whiteListUrls = StringUtils.commaDelimitedListToStringArray(clientProperties.getAppClientPublicUrls() + ","
                    + clientProperties.getMiscProp("idp.client.api.path"));
        } else {
            whiteListUrls = StringUtils.commaDelimitedListToStringArray(clientProperties.getAppClientPublicUrls());
        }
        LOG.info("Registering authentication on all urls, except: " + whiteListUrls);
        http.authorizeRequests().antMatchers(whiteListUrls).permitAll().anyRequest().authenticated();
        http.csrf().ignoringAntMatchers(whiteListUrls);

        LOG.info("Registering custom AuthenticationEntryPoint: " + authenticationEntryPoint);
        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);

        LOG.info("Registering logout url: " + IdpOpenIdConnectClient.pathSuffixLogout);
        http.logout().logoutUrl(IdpOpenIdConnectClient.pathSuffixLogout);

        LOG.info("Registering logout success url: " + "/");
        http.logout().logoutSuccessUrl("/");

        LOG.info("Registering custom AuthenticationFilter: " + authenticationFilter);
        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
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
