package com.integratingfactor.idp.lib.client.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.integratingfactor.idp.lib.client.model.IdToken;
import com.integratingfactor.idp.lib.client.model.IdpTokenValidation;
import com.integratingfactor.idp.lib.client.model.UserProfile;
import com.integratingfactor.idp.lib.client.util.IdpOauthClient;

@Controller
public class IdpOpenIdConnectClient implements IdpBackendAppService {
    private static Logger LOG = Logger.getLogger(IdpOpenIdConnectClient.class.getName());

    private IdpOauthClient oauthClient;

    static final String ClientIdKey = "idp.client.id";
    static final String ClientSecretKey = "idp.client.secret";
    static final String EncryptionKeyKey = "idp.client.encryption.key";
    String encryptionKey;
    static final String IdpHostKey = "idp.client.idp.host";
    static final String RedirectUrlKey = "idp.client.redirect.url";
    private final ObjectMapper objectMapper = new ObjectMapper();

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

    // this is the suffix where we are listening for IDP redirects for
    // authorization requests
    public static final String pathSuffixLogin = "/openid/login";
    public static final String pathSuffixLogout = "/openid/logout";

    public static final String IdpRequestOriginatorKey = "IDP_OPENID_CONNECT_REQUEST_ORIGINATOR";
    public static final String IdpUserProfileKey = "IDP_OPENID_CONNECT_USER_PROFILE";

    @RequestMapping(value = pathSuffixLogin, method = RequestMethod.GET)
    public String openIdConnectListener(HttpServletRequest request) {

        // return back to originating page for this authentication
        String originator = (String) request.getSession().getAttribute(IdpRequestOriginatorKey);
        if (StringUtils.isEmpty(originator)) {
            LOG.warning("Session does not have originator cache for this authentication");
            // use root landing page as default
            originator = "redirect:/";
        } else {
            originator = "redirect:" + originator;
        }
        // clear the session before proceeding
        clearSession(request);

        // process request parameters for token handling
        Map<String, String> params = new HashMap<String, String>();
        for (Map.Entry<String, String[]> kv : request.getParameterMap().entrySet()) {
            params.put(kv.getKey(), kv.getValue()[0]);
        }
        UserProfile user = getUser(params);
        if (user == null) {
            // could not get user details, present an error on the originator
            // page
            request.setAttribute("error_message", "Could not obtain user details");
            return originator;
        }

        // put the user profile in session context for who so ever needs this
        // (e.g. security filters)
        request.getSession(true).setAttribute(IdpUserProfileKey, user);
        LOG.info("Redirecting after openid connect authentication to " + originator);
        return originator;
    }

    public static final String originatingParam = "openidConnectRequestOriginator";

    @RequestMapping(value = pathSuffixLogin, method = RequestMethod.POST, params = { originatingParam })
    public String authenticateUser(@RequestParam(originatingParam) String originator, HttpServletRequest request) {
        // save the request originator to return back after authentication
        request.getSession(true).setAttribute(IdpRequestOriginatorKey, originator);
        // TODO: need to add _csrf protection token here as part of
        // authorization url param
        // redirect user to authorization request
        return "redirect:" + getAuthorizationUri();
    }

    @RequestMapping(value = pathSuffixLogout, method = RequestMethod.POST)
    public String logoutUser(HttpServletRequest request) {
        // clear the session
        clearSession(request);
        // redirect user to main landing page
        return "redirect:/";
    }

    private void clearSession(HttpServletRequest request) {
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

    @Override
    public String getAuthorizationUri() {
        return oauthClient.getAuthorizationUri();
    }

    @Override
    public UserProfile getUser(Map<String, String> params) {
        LOG.info("Getting user details from authorization response");
        // TODO: need to check _csrf protection token from params here for
        // validation
        OAuth2AccessToken token = oauthClient.getAccessToken(params);
        if (token == null) {
            LOG.warning ("Could not obtain access token from user approval");
            return null;
        }
        UserProfile user = null;
        if (token.getAdditionalInformation().containsKey("id_token")) {
            // use the id_token, if present, to get user details
            LOG.info("Openid connect ID token has user details");
            String claims = JwtHelper.decode((String) token.getAdditionalInformation().get("id_token")).getClaims();
            LOG.info("claims: " + claims);
            try {
                IdToken idToken = objectMapper.readValue(claims, IdToken.class);
                user = new UserProfile();
                user.setUserId(idToken.getSub());
                user.setFirstName((String) token.getAdditionalInformation().get("first_name"));
                user.setLastName((String) token.getAdditionalInformation().get("last_name"));
                user.setSubject(user.getFirstName() + " " + user.getLastName());
            } catch (IOException e) {
                LOG.warning("Could not get Openid connect ID Token object from response");
            }
        } else {
            // run a token validation, to get user details in validation
            // response
            LOG.info("Token is not of type Openid connect, running validation to get user details");
            IdpTokenValidation validation = oauthClient.validateToken(token);
            if (validation != null) {
                user = new UserProfile();
                user.setUserId(validation.getUserId());
            } else {
                LOG.warning("Failed to validate access token");
            }
        }

        return user;
    }

}
