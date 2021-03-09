# Use the Client Credential Flow to access the Resource Server through Spring Security

## Key concepts

You can use the OAuth 2.0 client credentials grant specified in RFC 6749, sometimes called
two-legged OAuth, to access web-hosted resources by using the identity of an application. This type
of grant is commonly used for server-to-server interactions that must run in the background, without
immediate interaction with a user. These types of applications are often referred to as daemons or
service accounts. The OAuth 2.0 client credentials grant flow permits a web service (confidential
client) to use its own credentials, instead of impersonating a user, to authenticate when calling
another web service. In this scenario, the client is typically a middle-tier web service, a daemon
service, or a web site.

## Protocol diagram

![Protocol diagram](https://docs.microsoft.com/zh-cn/azure/active-directory/develop/media/v2-oauth2-client-creds-grant-flow/convergence-scenarios-client-creds.svg)

## Getting started

In this project, `spring-security-client-credential-flow` (referred to as **Web APP**) is used as the
client side to access `azure-spring-boot-sample-active-directory-b2c-resource-server` (referred to
as **Web API**) by accessing the access token in the way of client credential flow. Then we get
resource data.

### Configure Web APP

There are two ways to exchange tokens for client credential flow in Web APP:

- The first way:

```java
@GetMapping("/client_file_read")
@ResponseBody
public String fileRead(){
    String body=webClient
    .get()
    .uri(URI_READ_ENDPOINT)
    .attributes(clientRegistrationId("aad-example"))
    .retrieve()
    .bodyToMono(String.class)
    .block();
    return"response:"+(null!=body?body:"failed.");
    }
```

- The second way:

```java
@GetMapping("/client_file_write")
@ResponseBody
public String fileWrite(@RegisteredOAuth2AuthorizedClient("aad-example") OAuth2AuthorizedClient authorizedClient){
    return callCustomResourceServer(authorizedClient);
    }
private String callCustomResourceServer(OAuth2AuthorizedClient authorizedClient){
    if(null!=authorizedClient){
    String body=webClient
    .get()
    .uri(URI_WRITE_ENDPOINT)
    .attributes(oauth2AuthorizedClient(authorizedClient))
    .retrieve()
    .bodyToMono(String.class)
    .block();
    return"response:"+(null!=body?body:"failed.");
    }else{
    return"response failed.";
    }
}
```
In the end, these two approaches will be executed to `DefaultOAuth2AuthorizedClientManager#authorize` method, get the access token.

#### Configure application.yml

```yml
spring:
  security:
    oauth2:
      client:
        registration:
          aad-example:
            authorization-grant-type: client_credentials
            client-id: <your-client-id>
            client-secret: <your-secret>
            scope: <your-scope>
            token-uri: <your-token-uri>
```

### Configure Web API
#### Configure application.yml

```yml
# In v2.0 tokens, this is always the client ID of the API, while in v1.0 tokens it can be the resource URI used in the request.
# If we configure azure.activedirectory.app-id-uri will be to check the audience.
# If you are using v1.0 tokens, configure app-id-uri to properly complete the audience validation.

azure:
  activedirectory:
    b2c:
      tenant-id: ${your-tenant-id}
      client-id: ${your-client-id}
      app-id-uri: ${your-app-id-uri}
```

## Examples

### Run with Maven

```shell
cd spring-security-client-credential-flow-sample-parent/azure-spring-boot-sample-active-directory-b2c-resource-server
mvn spring-boot:run
```

```shell
cd spring-security-client-credential-flow-sample-parent/spring-security-client-credential-flow
mvn spring-boot:run
```

### Access the Web API

We could use Postman to simulate a Web APP to send a request to a Web API.

```http request
GET /client_file_read HTTP/1.1
```

```http request
GET /client_file_write HTTP/1.1
```

### Check the authentication and authorization

1. Access `http://localhost:<your-configured-server-port>/client_file_read` link: success.
2. Access `http://localhost:<your-configured-server-port>/client_file_write` link: success.

## Troubleshooting

## Next steps

## Contributing