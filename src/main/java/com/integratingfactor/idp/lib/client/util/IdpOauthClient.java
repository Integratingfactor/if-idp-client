package com.integratingfactor.idp.lib.client.util;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.integratingfactor.idp.lib.client.config.IdpClientAuthProperties;
import com.integratingfactor.idp.lib.client.model.IdpTokenRequest;
import com.integratingfactor.idp.lib.client.model.IdpTokenValidation;

/**
 * utility class to implement oAuth2 client communication with IDP service
 * 
 * @author gnulib
 *
 */
public class IdpOauthClient {
    private static Logger LOG = Logger.getLogger(IdpOauthClient.class.getName());

    private RestTemplate restTemplate;

    private String idpHost;

    private String redirectUri;

    private String authorizationUrl;

    private String authToken;

    @Autowired
    IdpClientAuthProperties clientProperties;

    @PostConstruct
    public void setup() {
        initialize(clientProperties.getAppClientId(), clientProperties.getAppClientSecret(),
                clientProperties.getIdpHostUrl(), clientProperties.getAppRedirectUrl());
    }

    public IdpOauthClient() {
    }

    public IdpOauthClient(String clientId, String clientSecret, String idpHost, String redirectUri) {
        initialize(clientId, clientSecret, idpHost, redirectUri);
    }

    public void initialize(String clientId, String clientSecret, String idpHost, String redirectUri) {
        if (StringUtils.isEmpty(clientId)) {
            LOG.warning("Client ID cannot be empty");
            throw new RuntimeException("Client ID cannot be empty");
        }
        if (StringUtils.isEmpty(idpHost)) {
            LOG.warning("IDP host cannot be empty");
            throw new RuntimeException("IDP host cannot be empty");
        }
        this.idpHost = idpHost;
        if (StringUtils.isEmpty(redirectUri)) {
            LOG.warning("Redirect uri cannot be empty");
            throw new RuntimeException("Redirect uri cannot be empty");
        }
        this.redirectUri = redirectUri + IdpOpenIdConnectClient.pathSuffixLogin;
        authorizationUrl = idpHost + "/oauth/authorize?client_id=" + clientId + "&response_type=code&redirect_uri="
                + this.redirectUri;
        if (clientSecret == null) {
            clientSecret = "";
        }
        String auth = clientId + ":" + clientSecret;
        authToken = "Basic " + new String(Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII"))));
        restTemplate = new RestTemplate();
    }

    public String getAuthorizationUri() {
        return authorizationUrl;
    }

    private static final String AuthCode = "code";
    private static final String AccessToken = "access_token";
    private static final String ResourceOwner = "password";
    private static final String Error = "error";

    private OAuth2AccessToken requestAccessToken(IdpTokenRequest req) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        try {
            return restTemplate.postForEntity(idpHost + "/oauth/token",
                    new HttpEntity<MultiValueMap<String, String>>(req.toMap(), headers), OAuth2AccessToken.class)
                    .getBody();

        } catch (HttpClientErrorException e) {
            LOG.warning("Error in IDP request: " + e.getMessage() + " : " + e.getResponseBodyAsString());
        } catch (RestClientException e) {
            LOG.warning("Error in IDP request: " + e.getMessage());
        }
        return null;
    }

    /**
     * method to get access token based on authorization response from IDP
     * 
     * @param params
     *            authorization response from IDP redirected via user's browser
     * @return an access token (or null if failed)
     */

    public OAuth2AccessToken getAccessToken(Map<String, String> params) {
        OAuth2AccessToken token = null;
        if (params == null || params.isEmpty()) {
            LOG.warning("empty authorization response");
            return null;
        }
        // check the response type
        if (params.containsKey(AuthCode)) {
            // this is auth code response
            LOG.info("Authorization code grant");
            IdpTokenRequest req = new IdpTokenRequest();
            req.setCode(params.get(AuthCode));
            req.setGrantType("authorization_code");
            req.setRedirectUri(redirectUri);
            token = requestAccessToken(req);
        } else if (params.containsKey(AccessToken)) {
            // this is implicit grant for access token
            LOG.info("implicit token grant");
            // token = DefaultOAuth2AccessToken.valueOf(params);
            // copy all additional parameters from implicit token
            DefaultOAuth2AccessToken newToken = new DefaultOAuth2AccessToken(DefaultOAuth2AccessToken.valueOf(params));
            for (Map.Entry<String, String> kv : params.entrySet()) {
                newToken.getAdditionalInformation().put(kv.getKey(), kv.getValue());
            }
            token = newToken;
        } else if (params.containsKey(Error)) {
            // this is error response
            LOG.info("OAuth2 Authorization error: " + params.get(Error) + "\nReason: "
                    + params.get("error_description"));
        }
        return token;
    }

    /**
     * request access token with resource owner password grant
     *
     * @return always returns null
     */
    public OAuth2AccessToken getAccessToken() {
        IdpTokenRequest req = new IdpTokenRequest();
        req.setGrantType(ResourceOwner);
        req.setUsername(clientProperties.getAppServiceAccount());
        req.setPassword(clientProperties.getAppServicePassword());
        return requestAccessToken(req);
    }

    /**
     * this method will take a previously granted token and if there is a
     * refresh token provided in the original token then it will use that to
     * refresh the token grant
     * 
     * @param accessToken
     *            original access token
     * @return refreshed access token (or null if failed)
     */
    public OAuth2AccessToken getRefreshToken(OAuth2AccessToken accessToken) {
        LOG.info("Authorization code grant");
        if (accessToken == null) {
            LOG.warning("Wrong operation refresh on null access token");
            return null;
        }

        if (accessToken.getRefreshToken() == null) {
            LOG.warning("Wrong operation refresh on null refresh token");
            return null;
        }
        OAuth2AccessToken refreshedToken = null;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        IdpTokenRequest req = new IdpTokenRequest();
        req.setGrantType("refresh_token");
        req.setRefreshToken(accessToken.getRefreshToken().getValue());
        req.setRedirectUri(redirectUri);
        try {
            refreshedToken = restTemplate.postForEntity(idpHost + "/oauth/token",
                    new HttpEntity<MultiValueMap<String, String>>(req.toMap(), headers), OAuth2AccessToken.class)
                    .getBody();

        } catch (HttpClientErrorException e) {
            LOG.warning("Error in IDP request: " + e.getMessage() + " : " + e.getResponseBodyAsString());
        } catch (RestClientException e) {
            LOG.warning("Error in IDP request: " + e.getMessage());
        }
        return refreshedToken;
    }

    /**
     * this method will validate an access token with IDP service and get more
     * details about the user, tenants and the scopes this token is valid for
     * 
     * @param token
     *            access token to be validated
     * @return validation details from IDP (or null if failed)
     */
    public IdpTokenValidation validateToken(String token) {
        LOG.info("validating access token");
        if (token == null) {
            LOG.warning("Cannot validate null access token");
            return null;
        }
        IdpTokenValidation validation = null;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        IdpTokenRequest req = new IdpTokenRequest();
        req.setToken(token);
        try {
            validation = restTemplate.postForEntity(idpHost + "/oauth/check_token",
                    new HttpEntity<MultiValueMap<String, String>>(req.toMap(), headers), IdpTokenValidation.class)
                    .getBody();

        } catch (HttpClientErrorException e) {
            LOG.warning("Error in IDP request: " + e.getMessage() + " : " + e.getResponseBodyAsString());
        } catch (RestClientException e) {
            LOG.warning("Error in IDP request: " + e.getMessage());
        }
        return validation;
    }
}