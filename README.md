# if-idp-client
This is Java client library for accessing IDP services from [Integratingfactor.com](http://www.integratingfactor.com) platform.

These libraries will provide functionality for following:
* openid connect based user authentication with Integratingfactor.com's IDP service
* User approval for profile access and event subscription/notification
* Spring security framework support for authentication with Integratingfactor.com's IDP service
* RBAC on resource paths based on oAuth2 access token

## How to build this project
* Clone or download project
* Build and install project library: `mvn install`

Above steps should install the library into your local maven repository, and you should be able to use it in your application as described below.

## How to use Integratingfactor.com's IDP client library?
* download and build project as described above
* Add following dependencies into your maven project:
```XML
  <!-- Integratingfactor.com openid connect client library -->
  <dependency>
    <groupId>com.integratingfactor.idp</groupId>
    <artifactId>lib-idp-client</artifactId>
    <version>0.0.7-SNAPSHOT</version>
  </dependency>
```
* **Make sure to enable HTTP Sessions (required for CSRF and authorization workflow)** (e.g. if using google appengine, need to explicitly enable sessions)
* Library uses Javaconfig to configure Spring Security Framework. However, following minimal xml configuration is needed:
  * Security filter configuration in `web.xml` as described in Spring Security Framework Reference [Section 4.2.1 web.xml Configuration](http://docs.spring.io/spring-security/site/docs/4.0.3.RELEASE/reference/htmlsingle/#ns-web-xml), e.g.:
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
  * Configuration bean declaration in application's context file:  
  ```XML
  <!-- Configure IDP client library -->  
  <bean id="idpClientSecurityConfig"
  class="com.integratingfactor.idp.lib.client.config.IdpClientSecurityConfig" />  
  ```  
  **Note: IDP Client configuration leaves following url paths available for unrestricted/public access: "/", "/resources/\*\*", "/about/\*\*"**
  * provide following configurations in resource file `idp_client.properties` in your class path:  
  ```
  idp.client.id=test.openid.code.client
  idp.client.secret=
  idp.client.encryption.key=
  idp.client.idp.host=https://if-idp.appspot.com
  idp.client.redirect.url=https://integrating-factor.appspot.com
  ```
