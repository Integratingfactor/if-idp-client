package com.integratingfactor.idp.lib.client.filter;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IdpApiCorsFilter implements Filter {
    private static Logger LOG = Logger.getLogger(IdpApiCorsFilter.class.getName());

    @Override
    public void destroy() {
        // NOOP

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletResponse sResponse = (HttpServletResponse) response;
        sResponse.setHeader("Access-Control-Allow-Origin", "*");
        sResponse.setHeader("Access-Control-Allow-Credentials", "true");
        sResponse.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, DELETE, HEAD, OPTIONS");
        sResponse.setHeader("Access-Control-Allow-Headers",
                "Origin, Accept, Authorization, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
        if (((HttpServletRequest) request).getMethod().equals("OPTIONS")) {
            // this is an options check for CORS, so just return back 200 OK
            LOG.info("responding to access control");
            sResponse.setStatus(200);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        // NOOP

    }
}
