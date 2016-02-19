package com.integratingfactor.idp.lib.client.filter;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IdpTestApiEndpoint {

    public static final String API_ENDPOINT = "/api/v1/ping";

    @RequestMapping(API_ENDPOINT)
    public Pong ping(HttpServletRequest request) {
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
