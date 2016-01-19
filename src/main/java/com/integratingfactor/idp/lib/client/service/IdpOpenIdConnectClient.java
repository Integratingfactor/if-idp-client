package com.integratingfactor.idp.lib.client.service;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.integratingfactor.idp.lib.client.model.UserDetails;
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

    @Autowired
    private Environment env;

    @PostConstruct
    public void setup() {
        oauthClient = new IdpOauthClient(env.getProperty(ClientIdKey), env.getProperty(ClientSecretKey),
                env.getProperty(IdpHostKey), "" + env.getProperty(RedirectUrlKey) + pathSuffix);
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
    public static final String pathSuffix = "/openid";

    public static final String requestOriginator = "IDP_OPENID_CONNECT_REQUEST_ORIGINATOR";

    @RequestMapping(value = pathSuffix, method = RequestMethod.GET)
    public String openIdConnectListener(HttpServletRequest request) {

        // return back to originating page for this authentication
        String originator = (String) request.getSession().getAttribute(requestOriginator);
        if (StringUtils.isEmpty(originator)) {
            LOG.warning("Session does not have originator cache for this authentication");
            // use root landing page as default
            originator = "redirect:/";
        }
        // clear the session before returning
        request.getSession().setAttribute(requestOriginator, null);

        // process request parameters for token handling
        Map<String, String> params = new HashMap<String, String>();
        for (Map.Entry<String, String[]> kv : request.getParameterMap().entrySet()) {
            LOG.info("Adding key: " + kv.getKey() + ", value: " + kv.getValue()[0]);
            params.put(kv.getKey(), kv.getValue()[0]);
        }
        UserDetails user = getUser(params);
        if (user == null) {
            // could not get user details, present an error on the originator
            // page
            request.setAttribute("error_message", "Could not obtain user details");
            return originator;
        }

        // setup authentication in spring security context
        // TBD
        
        return originator;
    }

    public static final String originatingParam = "openidConnectRequestOriginator";

    @RequestMapping(value = pathSuffix, method = RequestMethod.POST, params = { originatingParam })
    public String authenticateUser(@RequestParam(originatingParam) String originator, HttpServletRequest request) {
        // save the request originator to return back after authentication
        request.getSession(true).setAttribute(requestOriginator, originator);
        // redirect user to authorization request
        return "redirect:" + getAuthorizationUri();
    }

    public IdpOpenIdConnectClient(String clientId, String clientSecret, String encryptionKey, String idpHost,
            String redirectUri) {
        oauthClient = new IdpOauthClient(clientId, clientSecret, idpHost, redirectUri + pathSuffix);
        this.encryptionKey = encryptionKey;
        LOG.info("OpenId Connect Client initialized");
    }

    @Override
    public String getAuthorizationUri() {
        return oauthClient.getAuthorizationUri();
    }

    @Override
    public UserDetails getUser(Map<String, String> params) {
        LOG.info("Getting user details from authorization response");
        OAuth2AccessToken token = oauthClient.getAccessToken(params);
        if (token == null) {
            LOG.warning ("Could not obtain access token from user approval");
            return null;
        }
        UserDetails user = null;
        if (token.getAdditionalInformation().containsKey("id_token")) {
            // use the id_token, if present, to get user details

        } else {
            // run a token validation, to get user details in validation
            // response
        }

        return user;
    }

}
