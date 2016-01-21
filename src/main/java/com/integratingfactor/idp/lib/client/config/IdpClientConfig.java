package com.integratingfactor.idp.lib.client.config;

import java.util.logging.Logger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.integratingfactor.idp.lib.client.mvc.IdpOpenIdConnectMvcHandler;

@Configuration
@EnableWebMvc
public class IdpClientConfig {
    private static Logger LOG = Logger.getLogger(IdpClientConfig.class.getName());

    @Bean
    public IdpOpenIdConnectMvcHandler idpOpenIdConnectMvcHandler() {
        LOG.info("Creating instance of IdpOpenIdConnectMvcHandler");
        return new IdpOpenIdConnectMvcHandler();
    }

    // @Bean
    // public IdpOpenIdConnectClient idpOpenIdConnectClient() {
    // LOG.info("Creating instance of IdpOpenIdConnectClient");
    // return new IdpOpenIdConnectClient();
    // }
    // /**
    // * register an external property place holder
    // */
    // @Bean
    // public static PropertySourcesPlaceholderConfigurer
    // propertyPlaceholderConfigurer() {
    // return new PropertySourcesPlaceholderConfigurer();
    // }
    //
    // @Override
    // protected void configure(HttpSecurity http) throws Exception {
    // LOG.info("configuring open id connect authentication filter ...");
    // http.authorizeRequests().anyRequest().authenticated().and().addFilterBefore(
    // new IdpOpenIdConnectAuthenticationFilter(),
    // UsernamePasswordAuthenticationFilter.class);
    // }
    //
    // /**
    // * register Spring Security with existing application context
    // *
    // * @author gnulib
    // *
    // */
    // public static class SecurityWebApplicationInitializer extends
    // AbstractSecurityWebApplicationInitializer {
    //
    // }
}
