package com.integratingfactor.idp.lib.client.mvc;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.integratingfactor.idp.lib.client.util.IdpOpenIdConnectClient;

@Controller
public class IdpTestMvcHandler {
    private static Logger LOG = Logger.getLogger(IdpTestMvcHandler.class.getName());

    @Autowired
    private IdpOpenIdConnectClient openidConnectClient;

    @RequestMapping(value = IdpOpenIdConnectClient.pathSuffixLogin, method = RequestMethod.GET)
    public String openIdConnectListener(HttpServletRequest request) {
        String redirect = "redirect:" + IdpOpenIdConnectClient.pathSuffixLogin;
        LOG.info("Redirecting after openid connect authentication to " + redirect);
        return redirect;
    }

    public static final String originatingParam = "openidConnectRequestOriginator";

    @RequestMapping(value = IdpOpenIdConnectClient.pathSuffixLogin, method = RequestMethod.POST, params = {
            originatingParam })
    public String authenticateUser(@RequestParam(originatingParam) String originator, HttpServletRequest request) {
        return "redirect:" + openidConnectClient.getAuthorizationUri();
    }

    @RequestMapping(value = IdpOpenIdConnectClient.pathSuffixLogout, method = RequestMethod.POST)
    public String logoutUser(HttpServletRequest request) {
        return "redirect:/";
    }
}
