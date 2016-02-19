package com.integratingfactor.idp.lib.client.rbac;

import javax.servlet.http.HttpServletRequest;

import com.integratingfactor.idp.lib.client.rbac.IdpRbacTestApiEndpoint.Pong;

public interface IdpRbacTestApi {

    String API_ENDPOINT = "/api/v1/ping";

    Pong ping(HttpServletRequest request);

}