package com.integratingfactor.idp.lib.client.service;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.integratingfactor.idp.lib.client.model.IdToken;
import com.integratingfactor.idp.lib.client.model.IdpTokenValidation;
import com.integratingfactor.idp.lib.client.model.IdpOpenIdConnectUser;
import com.integratingfactor.idp.lib.client.util.IdpOauthClient;

public class IdpOpenIdConnectClient {
    private static Logger LOG = Logger.getLogger(IdpOpenIdConnectClient.class.getName());

    private IdpOauthClient oauthClient;

    static final String ClientIdKey = "idp.client.id";
    static final String ClientSecretKey = "idp.client.secret";
    static final String EncryptionKeyKey = "idp.client.encryption.key";
    String encryptionKey;
    static final String IdpHostKey = "idp.client.idp.host";
    static final String RedirectUrlKey = "idp.client.redirect.url";
    private final ObjectMapper objectMapper = new ObjectMapper();

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

    public static final String IdpRequestOriginatorKey = "IDP_OPENID_CONNECT_REQUEST_ORIGINATOR";
    public static final String IdpUserProfileKey = "IDP_OPENID_CONNECT_USER_PROFILE";

    public void clearSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session != null) {
            session.setAttribute(IdpRequestOriginatorKey, null);
            session.setAttribute(IdpUserProfileKey, null);
            // TODO: need to clear _csrf protection token here as part of
            // authorization url param
        }
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

    public IdpOpenIdConnectUser getUser(Map<String, String> params) {
        LOG.info("Getting user details from authorization response");
        // TODO: need to check _csrf protection token from params here for
        // validation
        OAuth2AccessToken token = oauthClient.getAccessToken(params);
        if (token == null) {
            LOG.warning ("Could not obtain access token from user approval");
            return null;
        }
        // run a token validation, to get user details in validation
        // response
        LOG.info("Running validation to get user details");
        IdpTokenValidation validation = oauthClient.validateToken(token);
        if (validation == null) {
            LOG.warning("Failed to validate access token");
            return null;
        }
        IdpOpenIdConnectUser user = null;
        user = new IdpOpenIdConnectUser();
        user.setUserId(validation.getUserId());
        user.setAuthenticated(true);
        user.setFirstName(validation.getFirstName());
        user.setLastName(validation.getLastName());
        user.setSubject(user.getFirstName() + " " + user.getLastName());
        if (validation.getIdToken() != null) {
            // use the id_token, if present, to get user details
            LOG.info("Openid connect ID token has user details");
            String claims = JwtHelper.decode(validation.getIdToken()).getClaims();
            LOG.info("claims: " + claims);
            try {
                IdToken idToken = objectMapper.readValue(claims, IdToken.class);
                user.setUserId(idToken.getSub());
            } catch (IOException e) {
                LOG.warning("Could not get Openid connect ID Token object from response");
            }
        }

        return user;
    }

}