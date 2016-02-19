package com.integratingfactor.idp.lib.client.rbac;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RBAC policy annotation, to define any restriction at the level of:
 * <ul>
 * <li><strong>roles</strong> : restrict access to request with access token
 * that have specified roles. allow all if none specified.</li>
 * <li><strong>users</strong> : restrict access to request with access token
 * granted for specified user accounts. allow all if none specified.</li>
 * <li><strong>orgs</strong> : restrict access to request with access token that
 * were issued to apps for specified tenants/orgs. allow all if none specified.
 * </li>
 * <li><strong>clients</strong> : restrict access to request with access token
 * granted to specified apps. allow all if none specified.</li>
 * <li><strong>scopes</strong> : restrict access to request with access token
 * that have been granted to apps with specified scope. allow all if none
 * specified.</li>
 * </ul>
 * 
 * @author gnulib
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface IdpRbacPolicy {
    String[] roles() default {};

    String[] users() default {};

    String[] orgs() default {};

    String[] clients() default {};

    String[] scopes() default {};
}
