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
    <version>0.1.1-SNAPSHOT</version>
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
  **Note: IDP Client configuration leaves following url paths available for unrestricted/public access: "/", "/resources/\*\*", "/about/\*\*"**
  * provide following configurations in resource file `idp_client.properties` in your class path or export in environment:
  ```
	### app's client id from app registration with IDP service
	idp.client.id=test.backend.client
	### app's client secret as provided during app registration (required for backend apps)
	idp.client.secret=This.is.a.secret
	### app's encryption key for openid connect ID token as provided during app registration (optional)
	idp.client.encryption.key=This.is.an.encrypted.key
	### IDP service url (why is this needed, wouldn't it always be a well known public url)?
	idp.client.idp.host=https://if-idp.appspot.com
	### App's redirect url (should be one of the urls provided during app registration)
	idp.client.redirect.url=http://localhost:8080
	### white label publicly accessible url paths that should not require authentication
	idp.client.public.urls=/,/about/**,/resources/**
	##########################################################
	######### when you enable below, make sure to
	######### also configure corresponding OAuth filter
	### App's API path
	idp.client.api.path=/api/v1/**
	##########################################################
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
