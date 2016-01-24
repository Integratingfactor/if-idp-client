package com.integratingfactor.idp.lib.client.util;

import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import com.integratingfactor.idp.lib.client.model.IdpTokenValidation;

public class IdpOpenIdConnectClient {
    private static Logger LOG = Logger.getLogger(IdpOpenIdConnectClient.class.getName());

    private IdpOauthClient oauthClient;

    static final String ClientIdKey = "idp.client.id";
    static final String ClientSecretKey = "idp.client.secret";
    static final String EncryptionKeyKey = "idp.client.encryption.key";
    String encryptionKey;
    static final String IdpHostKey = "idp.client.idp.host";
    static final String RedirectUrlKey = "idp.client.redirect.url";

    // this is the suffix where we are listening for IDP redirects for
    // authorization requests
    public static final String pathSuffixLogin = "/openid/login";
    public static final String pathSuffixLogout = "/openid/logout";

    @Autowired
    private Environment env;

    @PostConstruct
    public void setup() {
        oauthClient = new IdpOauthClient(env.getProperty(ClientIdKey), env.getProperty(ClientSecretKey),
                env.getProperty(IdpHostKey), "" + env.getProperty(RedirectUrlKey) + pathSuffixLogin);
        this.encryptionKey = env.getProperty(EncryptionKeyKey);
        LOG.info("OpenId Connect Client initialized");
    }

    /**
     * default constructor
     */
    public IdpOpenIdConnectClient() {
    }

    public IdpOpenIdConnectClient(String clientId, String clientSecret, String encryptionKey, String idpHost,
            String redirectUri) {
        oauthClient = new IdpOauthClient(clientId, clientSecret, idpHost, redirectUri + pathSuffixLogin);
        this.encryptionKey = encryptionKey;
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
        return oauthClient.validateToken(token);
    }
}