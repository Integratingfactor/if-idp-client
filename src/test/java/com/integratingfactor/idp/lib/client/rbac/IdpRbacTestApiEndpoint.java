package com.integratingfactor.idp.lib.client.rbac;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.integratingfactor.idp.lib.client.rbac.IdpRbacPolicy;

@RestController
public class IdpRbacTestApiEndpoint {
    public static final String API_ENDPOINT_AUTHORIZED = "/api/v1/authorized";
    public static final String API_ENDPOINT_UNAUTHORIZED = "/api/v1/unauthorized";

    @RequestMapping(API_ENDPOINT_AUTHORIZED)
    @IdpRbacPolicy(orgs = { "users-alpha.integratingfactor.com", "users.integratingfactor.com" }, roles = "GUEST")
    public Pong pingAuthorized(HttpServletRequest request) {
        return new Pong("Hello " + request.getMethod());
    }

    @RequestMapping(API_ENDPOINT_UNAUTHORIZED)
    @IdpRbacPolicy(orgs = { "users-alpha.integratingfactor.com", "users.integratingfactor.com" }, roles = "ADMIN")
    public Pong pingUnauthorized(HttpServletRequest request) {
        return new Pong("Hello " + request.getMethod());
    }

    public static class Pong {
        private String message;

        public Pong(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}
