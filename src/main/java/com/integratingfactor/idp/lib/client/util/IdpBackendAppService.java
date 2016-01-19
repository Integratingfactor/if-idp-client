package com.integratingfactor.idp.lib.client.util;

import java.util.Map;

import com.integratingfactor.idp.lib.client.model.UserDetails;

/**
 * this interface defines an IDP backend application library that will interact
 * with IDP service on behalf of the application to initiate authorization
 * requests and obtain the access token based on user approval
 * 
 * @author gnulib
 *
 */
public interface IdpBackendAppService {

    /**
     * initiate an openid connect authentication of the user with IDP service
     * 
     * @return IDP url along with required parameters to redirect the user
     *         browser for authentication
     */
    String getAuthorizationUri();

    /**
     * handle user authentication response from the IDP service and provide user
     * details
     * 
     * @param params
     *            IDP service response parameters from authorization request
     */
    UserDetails getUser(Map<String, String> params);
}
