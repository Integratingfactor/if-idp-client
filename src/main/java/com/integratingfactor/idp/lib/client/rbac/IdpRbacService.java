package com.integratingfactor.idp.lib.client.rbac;

import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.integratingfactor.idp.lib.client.filter.IdpApiAuthFilter;

@Aspect
@Component
public class IdpRbacService {
    private static Logger LOG = Logger.getLogger(IdpRbacService.class.getName());

    @Autowired
    private HttpServletRequest request;

    @Pointcut("@annotation(com.integratingfactor.idp.lib.client.rbac.IdpRbacPolicy)  && @annotation(idpRbacPolicy)")
    public void accessControlledMethod(IdpRbacPolicy idpRbacPolicy) {
    }

    @Before("com.integratingfactor.idp.lib.client.rbac.IdpRbacService.accessControlledMethod(idpRbacPolicy)")
    public void doRbacValidation(IdpRbacPolicy idpRbacPolicy) {
        IdpApiRbacDetails rbacDetails = (IdpApiRbacDetails) request.getAttribute(IdpApiAuthFilter.IdpTokenRbacDetails);
        if (rbacDetails == null) {
            LOG.warning("Did not find any RBAC details in request: " + request.getRequestURI());
            throw new IdpRbacServerException("service not available");
        }
        // match RBAC policies
        validate(idpRbacPolicy.orgs(), rbacDetails.getTenantId());
        validate(idpRbacPolicy.users(), rbacDetails.getAccountId());
        validate(idpRbacPolicy.clients(), rbacDetails.getClientId());
        validate(idpRbacPolicy.roles(), rbacDetails.getRoles());
        validate(idpRbacPolicy.scopes(), rbacDetails.getScopes());
        LOG.info("Successfully passed RBAC checks for " + rbacDetails);
    }

    private void validate(String[] expected, Set<String> actuals) {
        if (expected.length > 0) {
            for (String check : expected) {
                if (actuals.contains(check))
                    return;
            }
            throw new IdpRbacAccessDeniedException("you do not have sufficient privilege to access requested resource");
        }
    }

    private void validate(String[] expected, String actual) {
        if (expected.length > 0) {
            for (String check : expected) {
                if (check.equals(actual))
                    return;
            }
            throw new IdpRbacAccessDeniedException("you do not have access to requested service");
        }
    }

}
