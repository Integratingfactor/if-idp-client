# if-idp-client
This is Java client library for accessing IDP services from [Integratingfactor.com](http://www.integratingfactor.com) platform.

These libraries will provide functionality for following:
* openid connect based user authentication with Integratingfactor.com's IDP service
* User approval for profile access and event subscription/notification
* Spring security framework support for authentication with Integratingfactor.com's IDP service
* RBAC on resource paths based on oAuth2 access token

# Usage

## How to build this project
* Clone or download project
* Build and install project library: `mvn install`

Above steps should install the library into your local maven repository, and you should be able to use it in your application as described below.

## How to configure Integratingfactor.com's IDP client library
* download and build project as described above
* Add following dependencies into your maven project:
```XML
  <!-- Integratingfactor.com openid connect client library -->
  <dependency>
    <groupId>com.integratingfactor.idp</groupId>
    <artifactId>lib-idp-client</artifactId>
    <version>0.1.3-SNAPSHOT</version>
  </dependency>
```
* **Make sure to enable HTTP Sessions (required for CSRF and authorization workflow)** (e.g. if using google appengine, need to explicitly enable sessions)
* Library uses Javaconfig to configure Spring Security Framework. However, following minimal xml configuration is needed:
  * add API OAuth filter configuration in `web.xml`:
  ```XML
  <filter>
      <filter-name>idpApiAuthFilter</filter-name>
      <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
  </filter>
  <filter-mapping>
      <filter-name>idpApiAuthFilter</filter-name>
      <!-- make sure that below api url pattern is also listed
      in the idp_client.properties file -->
      <url-pattern>/api/v1/*</url-pattern>
  </filter-mapping>
  ```  
  * add Spring security filter configuration in `web.xml`:
  ```XML
  <filter>
      <filter-name>springSecurityFilterChain</filter-name>
      <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
  </filter>
  <filter-mapping>
      <filter-name>springSecurityFilterChain</filter-name>
      <url-pattern>/*</url-pattern>
  </filter-mapping>
  ```  
  * add IDP client configuration bean declaration in application's context file:  
  ```XML
  <!-- Configure IDP client library -->  
  <bean id="idpClientSecurityConfig"
  class="com.integratingfactor.idp.lib.client.config.IdpClientSecurityConfig" />  
  ```  
  * enable AspectJ auto proxy wherever the API endpoint beans are being created
    * if API endpoint beans are being created with XML configuration, then need to use `<aop:aspectj-autoproxy/>` as following
    ```XML
    <beans:beans xmlns="http://www.springframework.org/schema/mvc"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:beans="http://www.springframework.org/schema/beans"
	xmlns:aop="http://www.springframework.org/schema/aop"
	xmlns:context="http://www.springframework.org/schema/context"
	xsi:schemaLocation="http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc.xsd
		http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
		http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd
	http://www.springframework.org/schema/aop
	http://www.springframework.org/schema/aop/spring-aop.xsd">

    <aop:aspectj-autoproxy/>
    ```
    * if using Javaconfig `@Configuration` and `@EnableWebMvc` class to create endpoint beans, then need use `@EnableAspectJAutoProxy` as following:
    ```JAVA
    @Configuration
@EnableWebMvc
@EnableAspectJAutoProxy
public class IdpRbacTestApiEndpointConfig {
    private static Logger LOG = Logger.getLogger(IdpRbacTestApiEndpointConfig.class.getName());

    @Bean
    public IdpRbacTestApiEndpoint idpRbacTestApi() {
        LOG.info("Creating instance of IdpRbacTestApiEndpoint");
        return new IdpRbacTestApiEndpoint();
    }
}
    ```
  * provide following configurations in resource file `idp_client.properties` in your class path or export in environment:
  ```
### app's client id from app registration with IDP service
idp.client.id=test.service.client
### app's client secret as provided during app registration (required for backend apps)
idp.client.secret=secret
### app's encryption key for openid connect ID token as provided during app registration (optional)
idp.client.encryption.key=This.is.an.encrypted.key
### app's service account username
idp.client.service.account=user
### app's service account password
idp.client.service.password=secret
### IDP service url (why is this needed, wouldn't it always be a well known public url)?
idp.client.idp.host=https://if-idp.appspot.com
### App's redirect url (should be one of the urls provided during app registration)
idp.client.redirect.url=http://localhost:8080
### white label publicly accessible url paths that should not require authentication
idp.client.public.urls=/,/about/**,/resources/**
### App's API path
idp.client.api.path=/api/v1/**
  ```
 > You can use above test client app parameters, or register a new app.
 
## How to access authenticated user information
Once user is authenticated, their profile can be access using following example:  
```JAVA
    IdpTokenValidation auth;
    try {
        auth = (IdpTokenValidation) SecurityContextHolder.getContext().getAuthentication();
        request.setAttribute("user", auth);
    } catch (ClassCastException e) {
        LOG.info("User is unauthenticated");
    }
```
## How to implement RBAC
RBAC can be implemented on per API endpoint by using the `@IdpRbacPolicy` annotations as following:
```JAVA
@RestController
public class PingApiEndpoint {
    private static Logger LOG = Logger.getLogger(PingApiEndpoint.class.getName());

    @RequestMapping(value = "/api/v1/ping/admin")
    @IdpRbacPolicy(orgs = { "users-alpha.integratingfactor.com", "users.integratingfactor.com" }, roles = "ADMIN")
    public Pong pingAdmin(HttpServletRequest request) {
        LOG.info("Ping request from " + request.getAttribute(IdpApiAuthFilter.IdpTokenRbacDetails));
        return new Pong("Hello Admin!");
    }

    @RequestMapping(value = "/api/v1/ping/user")
    @IdpRbacPolicy(orgs = { "users-alpha.integratingfactor.com", "users.integratingfactor.com" }, roles = "USER")
    public Pong pingUser(HttpServletRequest request) {
        LOG.info("Ping request from " + request.getAttribute(IdpApiAuthFilter.IdpTokenRbacDetails));
        return new Pong("Hello User!");
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
```