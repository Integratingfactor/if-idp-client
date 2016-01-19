package com.integratingfactor.idp.lib.client.util;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;
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

import com.integratingfactor.idp.lib.client.model.IdpTokenRequest;

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

    public IdpOauthClient(String clientId, String clientSecret, String idpHost, String redirectUri) {
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
        this.redirectUri = redirectUri;
        authorizationUrl = idpHost + "/oauth/authorize?client_id=" + clientId + "&response_type=code&redirect_uri="
                + redirectUri;
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
    private static final String Error = "error";

    public OAuth2AccessToken getToken(Map<String, String> params) {
        OAuth2AccessToken token = null;
        if (params == null || params.isEmpty()) {
            LOG.warning("empty authorization response");
            return null;
        }
        // check the response type
        if (params.containsKey(AuthCode)) {
            // this is auth code response
            LOG.info("Authorization code grant");
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authToken);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            IdpTokenRequest req = new IdpTokenRequest();
            req.setCode(params.get(AuthCode));
            req.setGrantType("authorization_code");
            req.setRedirectUri(redirectUri);
            try {
                token = restTemplate.postForEntity(idpHost + "/oauth/token",
                        new HttpEntity<MultiValueMap<String, String>>(req.toMap(), headers),
                                OAuth2AccessToken.class)
                        .getBody();

            } catch (HttpClientErrorException e) {
                LOG.warning("Error in IDP request: " + e.getMessage() + " : " + e.getResponseBodyAsString());
            } catch (RestClientException e) {
                LOG.warning("Error in IDP request: " + e.getMessage());
            }
        } else if (params.containsKey(AccessToken)) {
            // this is implicit grant for access token
            LOG.info("implicit token grant");
            token = DefaultOAuth2AccessToken.valueOf(params);
        } else if (params.containsKey(Error)) {
            // this is error response
            LOG.info("OAuth2 Authorization error: " + params.get(Error) + "\nReason: "
                    + params.get("error_description"));
        }
        return token;
    }
}