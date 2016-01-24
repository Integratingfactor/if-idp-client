package com.integratingfactor.idp.lib.client.mvc;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.integratingfactor.idp.lib.client.model.IdpOpenIdConnectUser;
import com.integratingfactor.idp.lib.client.model.IdpTokenValidation;
import com.integratingfactor.idp.lib.client.service.IdpOpenIdConnectClient;

@Controller
public class IdpOpenIdConnectMvcHandler {
    private static Logger LOG = Logger.getLogger(IdpOpenIdConnectMvcHandler.class.getName());

    @Autowired
    private IdpOpenIdConnectClient openidConnectClient;

    @RequestMapping(value = IdpOpenIdConnectClient.pathSuffixLogin, method = RequestMethod.GET)
    public String openIdConnectListener(HttpServletRequest request) {

        /*
         * // return back to originating page for this authentication String
         * originator = (String)
         * request.getSession().getAttribute(IdpOpenIdConnectClient.
         * IdpRequestOriginatorKey); if (StringUtils.isEmpty(originator)) {
         * LOG.warning(
         * "Session does not have originator cache for this authentication"); //
         * use root landing page as default originator = "redirect:/"; } else {
         * originator = "redirect:" + originator; } // clear the session before
         * proceeding openidConnectClient.clearSession(request);
         */
        // process request parameters for token handling
        Map<String, String> params = new HashMap<String, String>();
        for (Map.Entry<String, String[]> kv : request.getParameterMap().entrySet()) {
            params.put(kv.getKey(), kv.getValue()[0]);
        }
        IdpTokenValidation user = openidConnectClient.getValidatedUser(params);
        if (user != null) {
            // put the user profile in session context for who so ever needs
            // this
            // (e.g. security filters)
            request.getSession(true).setAttribute(IdpOpenIdConnectClient.IdpUserProfileKey, user);
        }
        // String redirect = "redirect:" +
        // IdpOpenIdConnectClient.pathSuffixLogin + ".do";
        String redirect = "redirect:" + IdpOpenIdConnectClient.pathSuffixLogin;
        LOG.info("Redirecting after openid connect authentication to " + redirect);
        return redirect;
    }

    public static final String originatingParam = "openidConnectRequestOriginator";

    @RequestMapping(value = IdpOpenIdConnectClient.pathSuffixLogin, method = RequestMethod.POST, params = {
            originatingParam })
    public String authenticateUser(@RequestParam(originatingParam) String originator, HttpServletRequest request) {
        // save the request originator to return back after authentication
        request.getSession(true).setAttribute(IdpOpenIdConnectClient.IdpRequestOriginatorKey, originator);
        // TODO: need to add _csrf protection token here as part of
        // authorization url param
        // redirect user to authorization request
        return "redirect:" + openidConnectClient.getAuthorizationUri();
    }

    @RequestMapping(value = IdpOpenIdConnectClient.pathSuffixLogout, method = RequestMethod.POST)
    public String logoutUser(HttpServletRequest request) {
        // clear the session
        openidConnectClient.clearSession(request);
        // redirect user to main landing page
        return "redirect:/";
    }
}
