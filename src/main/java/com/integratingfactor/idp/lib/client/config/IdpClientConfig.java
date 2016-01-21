package com.integratingfactor.idp.lib.client.config;

import java.util.logging.Logger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.integratingfactor.idp.lib.client.service.IdpOpenIdConnectClient;
import com.integratingfactor.idp.lib.client.service.IdpOpenIdConnectMvcHandler;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "com.integratingfactor.idp.lib.client.service" })
@PropertySource("classpath:idp_client.properties")
public class IdpClientConfig {
    private static Logger LOG = Logger.getLogger(IdpClientConfig.class.getName());

    @Bean
    public IdpOpenIdConnectMvcHandler idpOpenIdConnectMvcHandler() {
        LOG.info("Creating instance of IdpOpenIdConnectMvcHandler");
        return new IdpOpenIdConnectMvcHandler();
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
}
