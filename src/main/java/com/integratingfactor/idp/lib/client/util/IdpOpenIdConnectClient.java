package com.integratingfactor.idp.lib.client.util;

import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import com.integratingfactor.idp.lib.client.config.IdpClientAuthProperties;
import com.integratingfactor.idp.lib.client.model.IdpTokenValidation;

public class IdpOpenIdConnectClient {
    private static Logger LOG = Logger.getLogger(IdpOpenIdConnectClient.class.getName());

    @Autowired
    private IdpOauthClient oauthClient;

    String encryptionKey;

    // this is the suffix where we are listening for IDP redirects for
    // authorization requests
    public static final String pathSuffixLogin = "/openid/login";
    public static final String pathSuffixLogout = "/openid/logout";

    @Autowired
    private IdpClientAuthProperties clientProperties;

    @PostConstruct
    public void setup() {
        this.encryptionKey = clientProperties.getAppClientEncryptionKey();
        LOG.info("OpenId Connect Client initialized");
    }

    public String getAuthorizationUri() {
        return oauthClient.getAuthorizationUri();
    }

    public IdpTokenValidation getValidatedUser(Map<String, String> params) {
        LOG.info("Getting user details from authorization response");
        // TODO: need to check _csrf protection token from params here for
        // validation
        OAuth2AccessToken token = oauthClient.getAccessToken(params);
        if (token == null) {
            LOG.warning("Could not obtain access token from user approval");
            return null;
        }
        // run a token validation, to get user details in validation
        // response
        LOG.info("Running validation to get user details");
        return oauthClient.validateToken(token.getValue());
    }
}