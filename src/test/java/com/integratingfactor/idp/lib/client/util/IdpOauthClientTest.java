package com.integratingfactor.idp.lib.client.util;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.Assertion;

import com.integratingfactor.idp.lib.client.model.IdpTokenValidation;

public class IdpOauthClientTest extends Assertion {

    IdpOauthClient client;

    static final String testClientId = "test.oauth2.code.client";

    String testClientSecret = "";

    static final String idpHost = "https://if-idp.appspot.com";

    static final String testRedirectUri = "http://localhost";

    @BeforeTest
    public void setup() {
        client = new IdpOauthClient(testClientId, testClientSecret, idpHost, testRedirectUri);
    }

    @Test
    public void testThatProvidesAuthorizationUrlCorrect() {
        String uri = client.getAuthorizationUri();

        System.out.println("Authorization uri: " + uri);

        assertNotNull(uri);
        assertTrue(uri.contains("response_type=code"));
    }

    @Test
    public void testThatFailsWhenClientIdNull() {
        try {
            new IdpOauthClient(null, testClientSecret, idpHost, testRedirectUri);
            fail("Did not detect null client ID");
        } catch (RuntimeException e) {
            System.out.println("Validated correctly: " + e.getMessage());
        }
    }

    @Test
    public void testThatFailsWhenIdpHostNull() {
        try {
            new IdpOauthClient(testClientId, testClientSecret, null, testRedirectUri);
            fail("Did not detect null IDP host url");
        } catch (RuntimeException e) {
            System.out.println("Validated correctly: " + e.getMessage());
        }
    }

    @Test
    public void testThatFailsWhenRedirectUriNull() {
        try {
            new IdpOauthClient(testClientId, testClientSecret, idpHost, null);
            fail("Did not detect null redirect url");
        } catch (RuntimeException e) {
            System.out.println("Validated correctly: " + e.getMessage());
        }
    }

    static final String userDeniedError = "error=access_denied&error_description=User denied access";
    @Test
    public void testThatReturnsNullTokenForError() {
        OAuth2AccessToken token = client.getAccessToken(OAuth2Utils.extractMap(userDeniedError));
        assertNull(token);
    }

    static final String implicitTokenGrant = "access_token=8e45f953-6f7f-4774-a30e-57196d20251b&token_type=bearer&expires_in=59&scope=endpoint";
    @Test
    public void testThatReturnsTokenForImplicitGrant() {
        OAuth2AccessToken token = client.getAccessToken(OAuth2Utils.extractMap(implicitTokenGrant));
        assertNotNull(token);
        assertEquals(token.getTokenType(), "bearer");
        assertEquals(token.getValue(), "8e45f953-6f7f-4774-a30e-57196d20251b");
    }

    // uncomment the following to test against real IDP with real approval
    // response from IDP for the following approval URL:
    // https://if-idp.appspot.com/oauth/authorize?client_id=test.oauth2.code.client.secret&response_type=code&redirect_uri=http://localhost
    static final String approvalResponse = "code=51897aea-8fb7-4699-b56f-3f2c5285c937";

    // @Test
    public void testThatGetTokenFromIdpForAuthorizationCode() throws Exception {
        client = new IdpOauthClient(testClientId, testClientSecret, idpHost, testRedirectUri);
        OAuth2AccessToken token = client.getAccessToken(OAuth2Utils.extractMap(approvalResponse));
        assertNotNull(token);
        System.out.println("Token: " + new ObjectMapper().writeValueAsString(token));
    }

    @Test
    public void testThatChecksForNullAccessTokenUponRefresh() {
        OAuth2AccessToken token = client.getRefreshToken(null);
        assertNull(token);
    }

    @Test
    public void testThatChecksForNullRefreshTokenUponRefresh() {
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("some mock token value");
        token.setRefreshToken(null);
        OAuth2AccessToken refresh = client.getRefreshToken(token);
        assertNull(refresh);
    }

    @Test
    public void testThatGetsRefreshTokenFromIdp() throws Exception {
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("some mock token value");
        token.setRefreshToken(new DefaultOAuth2RefreshToken("89ce0b64-6366-4358-a9c4-41c49c709062"));
        OAuth2AccessToken refresh = client.getRefreshToken(token);
        assertNotNull(refresh);
        System.out.println("Token: " + new ObjectMapper().writeValueAsString(refresh));
        assertNotNull(refresh.getRefreshToken());
        assertTrue(refresh.getExpiresIn() > 0);
    }

    @Test
    public void testThatChecksForNullAccessTokenUponValidation() {
        IdpTokenValidation validation = client.validateToken(null);
        assertNull(validation);
    }

    @Test
    public void testThatValidatesTokenWithIdp() throws Exception {
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("some mock token value");
        token.setRefreshToken(new DefaultOAuth2RefreshToken("89ce0b64-6366-4358-a9c4-41c49c709062"));
        OAuth2AccessToken refresh = client.getRefreshToken(token);
        assertNotNull(refresh);
        IdpTokenValidation validation = client.validateToken(refresh);
        assertNotNull(validation);
        System.out.println("Token validation: " + new ObjectMapper().writeValueAsString(validation));
        assertNotNull(validation.getUserId());
        assertFalse(validation.getAuthorities().isEmpty());
    }
}
