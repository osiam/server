# HTTP API Documentation

Chapters:
- [Basics](#basics)
- [OAuth2](#oauth2)
 - [Supported Grants](#supported-grants)
 - [Authorization Code Grant](#authorization-code-grant)
 - [Resource Owner Password Credentials Grant](#resource-owner-password-credentials-grant)
 - [Client Credentials Grant](#client-credentials-grant)
- [Scopes] (#scopes)
 - [Supported scopes](#supported-scopes)
- [SCIM](#scim)
 - [OSIAM Implementation Specifics](#osiam-implementation-specifics)
 - [OSIAM's Known Limitations](#osiams-known-limitations)
 - [Unsupported Interfaces](#unsupported-interfaces)
 - [Service provider configuration](#service-provider-configuration)
 - [Search](#search)
      - [Search filtering including logical operators](#search-filtering-including-logical-operators)
      - [Limiting the output to predefined attributes] (#limiting-the-output-to-predefined-attributes)
     - [Sorting](#sorting)
     - [Paging](#paging)
 - [User management](#user-management)
     - [Get a single User] (#get-a-single-user)
     - [Get the User of the actual accessToken](#get-the-user-of-the-actual-accesstoken)
     - [Create a new User] (#create-a-new-user)
     - [Replace an existing User] (#replace-an-existing-user)
     - [Update an User](#update-an-user)
     - [Delete an User](#delete-an-user)
 - [Group management](#group-management)
     - [Get a single Group](#get-a-single-group)
     - [Create a new Group](#create-a-new-group)
     - [Replace an existing Group](#replace-an-existing-group)
     - [Update an Group](#update-an-group)
     - [Delete an Group](#delete-an-group)
- [OSIAM] (#osiam)
 - [Client Management](#client-management)
     - [Client configuration](#client-configuration)
     - [Get a single client](#get-a-single-client)
     - [Create a client](#create-a-client)
     - [Replace an client](#replace-an-client)
     - [Delete an client] (#delete-an-client)
- [Other](#other)
 - [Facebooks me](#facebooks-me)

## Basics

OSIAM uses the [OAuth 2.0](http://tools.ietf.org/html/rfc6749) standard the provide a secure API. The standard supports different ways of getting access to a secured API. They are called "grants". OSIAM support three different grants, while the concept of OSIAM is based on the grant providing the best security. It is important to understand the concept of the so called 'Authorization Code Grant'.

There are a lot of webpages out there explaining that topic in detail, the following sections are an OSIAM related summary.

OSIAM has two types of interfaces:
* The authx interface - also called technical interface, implementing the OAuth 2.0 standard: Used for authentication (authn) and authorization (authz)
* The resource interface - also called functional interface, implementing the SCIMv2 standard: Used for management of the data (identities) within OSIAM

OSIAM basically communicates with two parties:
* The client: Is an application that wants to work with the data stored in OSIAM 
* The user: Is the one who is using the client.

OSIAM needs to know both parties to provide its services. 
* The client has to be configured in OSIAM including a secret phrase, so OSIAM can verify that it is an authentic client is sending requests.
* The user needs to be stored in the OSIAM database as well, including an authentication parameter (e.g. a password) and the so called 'scopes'. The scopes define what actions an user can perform on the OSIAM resource interface.

In order for a client to access the resource interface of OSIAM the client needs to be authorized. This is done by the user. In the standard flow the user gets to see a [login screen](https://github.com/osiam/server/blob/master/auth-server/defaultConfiguration/auth-server/templates/web/login.html) provided by OSIAM to authenticate himself (e.g. using a password). If successful the client can make use of OSIAM's resource interface based on the scopes defined on the user's record.

So in short words: OSIAM needs to know at least about a single user and one client to provide its services. That is the reason why an OSIAM installation comes with the [default user and a default client](detailed_reference_installation.md#default-setup).

## OAuth2

### Supported Grants

An authorization grant is a credential representing the authorization to access protected resources. This credential is called "access token". Once a client has the access token it has to use the token everytime it accesses a protected resource until the token gets invalid. 
More details can be found in the [OAuth 2.0 specifictaion](http://tools.ietf.org/html/rfc6749#section-1.3).

OSIAM supports three OAuth 2.0 grant types:
* [authorization code](#authorization-code-grant)
* [resource owner password credentials](#resource-owner-password-credentials-grant)
* [client credentials](#client-credentials-grant)

### Authorization Code Grant
This is the most secure grant and is recommended for every production use case. 

With this grant the user needs to authenticate himself, the user has to authorize the client to access the protected resources and last but not least the registered client needs to authenticate himself.

OSIAM implements some additional features for the client authorization that are [configurable per client](#client-management):
* Authorization behaviour: OSIAM can skip the step of the user authorization by performing an internal implicit authorization or the a authorization once given by the user can be store as valid for a configurable period of time, instead of asking the user for authorization on every login.

#### How to get the access token?

##### Authorization Request
First the "authorization code" is required. This happens with the so called [authorization request](http://tools.ietf.org/html/rfc6749#section-4.1.1). In short words this is what happens:

The client that wants to get the authorization code redirects the user's browser to the authorization server providing several parameters shown in the example request below:

```http
https://<OSIAMHOST>:8443/osiam-auth-server/oauth/authorize?client_id=<CLIENT_ID>&response_type=code&redirect_uri=<CLIENT_REDIRECT_URI>&scope=<SCOPES>
```

This request an all consequent examples are based on an encrypted connection, if you do not have SSL enabled on your application server, the protocol prefix is only 'http://' and the port is likely to be '8080'.

The additional parameters in the example shortly explained:
* OSIAMHOST - is the hostname or IP address of the machine OSIAM is running on. 
* CLIENT_ID - is the identifier of the client the client was registered with on OSIAM's client management interface.
* CLIENT_REDIRECT_URI - the redirect URI of the client. This URI must begin with the value of the redirect URI stored in OSIAM's database for that client, or it can be identical to it. So you can provide additional parameters for the redirect URI in the authorization request.
* SCOPES - the [scopes](#scopes) the client wants to be authorized for when using the access token.

##### Interactive Authorization
Now the user sees a page to authenticate himself (e.g. using a username and a password). After successful authentication and when the client is configured in the OSIAM database that way, the user will also be asked to authorize the client.

##### Authorization Response
The server now sends an [authorization response](http://tools.ietf.org/html/rfc6749#section-4.1.2) that includes the authorization code and redirects the user back to the client. If something goes wrong an [error response](http://tools.ietf.org/html/rfc6749#section-4.1.2.1) is send.

##### Access Token Request
With the authentication code the client can now talk directly to the server to request the access token. This is done via the [access token request](http://tools.ietf.org/html/rfc6749#section-4.1.3) using
[HTTP basic authentication](http://tools.ietf.org/html/rfc2617). An example access token request below:

```sh
curl -H "Authorization: Basic <BASE64_CLIENT_CREDENTIALS>" -X POST -d "code=<AUTH_CODE>&grant_type=authorization_code&redirect_uri=<CLIENT_PROVIDED_URI>" http://<OSIAMHOST>:8080/osiam-auth-server/oauth/token
```

The parameters (beside the OSIAMHOST are):
* BASE64_CLIENT_CREDENTIALS - required for the HTTP basic authentication, it consists of the CLIENT_ID and the client's SECRET
* AUTH_CODE - is the previously received authentication code.
* CLIENT_PROVIDED_URI - The URI of the client that must match the one stored for the client in OSIAM's database

##### Access Token Response
The [access token response](http://tools.ietf.org/html/rfc6749#section-4.1.4) includes the access token the client needs to use OSIAM's resource interface on the authorized scopes.

### Resource Owner Password Credentials Grant
This grant provides the possibility to get an access token without user interaction. But needs client and user credentials. 
Authorization request/response from the authorization code grant is not required. is necessary, only the [Access Token Request](http://tools.ietf.org/html/rfc6749#section-4.3.2) and [HTTP Basic Authentication](http://tools.ietf.org/html/rfc2617) is used.

```sh
curl -H "Authorization: Basic <BASE64_CLIENT_CREDENTIALS>" -X POST -d "grant_type=password&username=<USERNAME>&password=<PASSWORD>&scope=<SCOPES>" http://<OSIAMHOST>:8080/osiam-auth-server/oauth/token
```

The parameters are similar to the access token request from the authorization code grant, but the user credentials are provided using the parameter
* USERNAME and
* PASSWORD

An example based on [OSIAM's default setup](detailed_reference_installation.md#default-setup):

```sh
curl -H "Authorization: Basic ZXhhbXBsZS1jbGllbnQ6c2VjcmV0" -X POST -d "grant_type=password&username=admin&password=koala&scope=GET POST PUT PATCH DELETE" http://localhost:8080/osiam-auth-server/oauth/token
```

### Client Credentials Grant
This grant provides a possibility to get an access token without user interaction and needs only client credentials, no authorization request is required, only the [Access Token Request](http://tools.ietf.org/html/rfc6749#section-4.4.2) and [HTTP Basic Authentication](http://tools.ietf.org/html/rfc2617) is used.

```sh
curl -H "Authorization: Basic <BASE64_CLIENT_CREDENTIALS>" -X POST -d "grant_type=client_credentials&scope=<SCOPES>"
http://<OSIAMHOST>:8080/osiam-auth-server/oauth/token
```

An example based on [OSIAM's default setup](detailed_reference_installation.md#default-setup)

```sh
curl -H "Authorization: Basic ZXhhbXBsZS1jbGllbnQ6c2VjcmV0" -X POST -d "grant_type=client_credentials&scope=GET POST PUT PATCH DELETE" http://localhost:8080/osiam-auth-server/oauth/token
```

## Scopes

Scopes are used to define access rights, see [OAuth2 Spec](http://tools.ietf.org/html/draft-ietf-oauth-v2-31#section-1.4) for further details.

### Supported scopes

OSIAM implements the different http methods as single scopes plus the scope 'email':

* GET - allows you all get calls it stands for reading,
* POST - allows you all post calls it stands mostly for creating
* PUT - allows you all put calls it stands for replacing
* PATCH - allows you all patch calls which means updating
* DELETE - allows you all delete calls which means deleting
* email - allows the access to a basic dataset of the user was authenticated during the OAuth 2.0 flow

# SCIM

All scim calls are secured by OAuth 2.0, so you will at least have to send an access token in order get the expected response:

```sh
curl -H "Authorization: Bearer YOUR_ACCESS_TOKEN" http://OSIAMHOST:8080/osiam-resource-server/Users/ID
```

The parameters are:
* YOUR_ACCESS_TOKEN - the access token that was provided through the OAuth 2.0 grant
* ID - The ID of the user record you want to retrieve

## OSIAM Implementation Specifics
* The default maxResults of a filter is 100
* xmlDataFormat is not supported

## OSIAM's Known Limitations
* the [etag](http://tools.ietf.org/html/draft-ietf-scim-api-02#section-3.11) is not supported yet, but it is already in OSIAMs backlog

## Unsupported Interfaces
* SCIM bulk actions: Is not yet implemented, but it is on the roadmap
* Cross ressource types search from the server's root: We do not understand the use case for this functionality at the moment, please let us know if you have a reasonable use case.

## Service provider configuration

The following URI provides the service provider configuration of the addressed server:

```http
http://OSIAMHOST:8080/osiam-resource-server/ServiceProviderConfigs
```

## Search
OSIAM supports search on both SCIM resource types, user and group.
* Users: 
```
http://OSIAMHOST:8080/osiam-resource-server/Users
```
* Groups
```
http://OSIAMHOST:8080/osiam-resource-server/Groups
```

### Parser grammar for search

```h
grammar LogicalOperatorRules;

parse
    : expression
    ;

expression
    : expression OR expression    #orExp
    | expression AND expression   #andExp
    | NOT '(' expression ')'      #notExp
    | '(' expression ')'          #braceExp
    | FIELD PRESENT               #simplePresentExp
    | FIELD OPERATOR VALUE        #simpleExp
    ;

OR
    : 'or'
    | 'Or'
    | 'oR'
    | 'OR'
    ;

AND
    : 'and'
    | 'And'
    | 'aNd'
    | 'anD'
    | 'ANd'
    | 'aND'
    | 'AND'
    ;

NOT
    : 'not'
    | 'Not'
    | 'nOt'
    | 'noT'
    | 'NOt'
    | 'nOT'
    | 'NOT'
    ;

PRESENT
    : 'pr'
    ;

OPERATOR
    : 'sw'
    | 'co'
    | 'eq'
    | 'gt'
    | 'ge'
    | 'lt'
    | 'le'
    ;

FIELD
    : ([a-z] | [A-Z] | [0-9] | '.' | ':' | '_' | '-')+ 
    ;

ESCAPED_QUOTE
    : '\\"'
    ;

VALUE
    : '"'(ESCAPED_QUOTE | ~'"')*'"'
    ;

EXCLUDE
    : [\b | \t | \r | \n]+ -> skip
    ;
```

### Search filtering including logical operators

The following filter options are supported:
* eq = equals
* co = contains
* sw = starts with
* pr = present
* gt = greater than
* ge = greater equals
* lt = less than
* le = less equals

**The value must be provided in double quotes. To provide a quote as part of the search value, the input must contain \" for each desired quote.**
 
Here are some examples:

```http
http://OSIAMHOST:8080/osiam-resource-server/Users?access_token=YOUR_ACCESSTOKEN&filter=userName%20eq%20"TheUserName"

http://OSIAMHOST:8080/osiam-resource-server/Users?access_token=YOUR_ACCESSTOKEN&filter=userName%20co%20"someValue"

http://OSIAMHOST:8080/osiam-resource-server/Users?access_token=YOUR_ACCESSTOKEN&filter=userName%20sw%20"someValue"

http://OSIAMHOST:8080/osiam-resource-server/Users?access_token=YOUR_ACCESSTOKEN&filter=displayName%20pr

http://OSIAMHOST:8080/osiam-resource-server/Users?access_token=YOUR_ACCESSTOKEN&filter=meta.created%20gt%20"2013-05-23T13:12:45.672#;4302:00"

http://OSIAMHOST:8080/osiam-resource-server/Users?access_token=YOUR_ACCESSTOKEN&filter=meta.created%20ge%20<an existing time>

http://OSIAMHOST:8080/osiam-resource-server/Users?access_token=YOUR_ACCESSTOKEN&filter=meta.created%20lt%20"2013-05-23T13:12:45.672#;4302:00"

http://OSIAMHOST:8080/osiam-resource-server/Users?access_token=YOUR_ACCESSTOKEN&filter=meta.created%20le%20<an existing time>
```

Additionally "AND" and "OR" as logical operators are supported, including grouping with parentheses.

```http
http://OSIAMHOST:8080/osiam-resource-server/Users?access_token=YOUR_ACCESSTOKEN&filter=userName%20eq%20"TheUserName"%20and%20meta.created%20lt%20"2013-05-23T13:12:45.672#;4302:00"

http://OSIAMHOST:8080/osiam-resource-server/Users?access_token=YOUR_ACCESSTOKEN&filter=userName%20eq%20"TheUserName"%20or%20userName%20eq%20"TheUserName1"

http://OSIAMHOST:8080/osiam-resource-server/Users?access_token=YOUR_ACCESSTOKEN&filter=(userName%20eq%20"TheUserName"%20or%20userName%20eq%20"TheUserName1")%20and%20meta.created%20gt%20"2013-05-23T13:12:45.672#;4302:00"
```

Also the "NOT" operator is supported. The parentheses are required and not optional. The "NOT" can also include filters already combined with "AND" and "OR".

```http
http://OSIAMHOST:8080/osiam-resource-server/Users?access_token=YOUR_ACCESSTOKEN&filter=active%20eq%20"true"%20and%20not%20(groups.display%20eq%20"TheGroupName")
```

### Limiting the output to predefined attributes

It is possible to search and limit the output to a the given list of attributes. To define more than one separate them with comma using the ``attributes`` parameter.

```http
http://OSIAMHOST:8080/osiam-resource-server/Users?access_token=YOUR_ACCESSTOKEN&attributes=userName,displayName,meta.created
```

### Sorting

To sort the results ascending or descending by a given attribute use the following parameters:
* sortOrder - ascending and descending. Default is ascending
* sortBy - the attribute so sort by. For example "userName". The default is "id"

```http
http://OSIAMHOST:8080/osiam-resource-server/Users?access_token=YOUR_ACCESSTOKEN&sortBy=meta.created&sortOrder=ascending

http://OSIAMHOST:8080/osiam-resource-server/Users?access_token=YOUR_ACCESSTOKEN&sortBy=meta.created&sortOrder=descending
```

### Paging

The paging is done via two parameters that limit the output shown per page and define the starting point using the following parameters:
* count - will limit the items per page to the given value. Default is 100
* startIndex - will define the start index of the search. Default is 0

To paginate through the results increase the startIndex to the next desired position.

```http
http://OSIAMHOST:8080/osiam-resource-server/Users?access_token=$YOUR_ACCESSTOKEN&count=5&startIndex=0

http://OSIAMHOST:8080/osiam-resource-server/Users?access_token=$YOUR_ACCESSTOKEN&count=5&startIndex=5
```

## User management

This section will describe the handling of user with OSIAM.

### Get a single User

To get a single user you need to send a GET request to the URL providing the user's ID

```http
http://OSIAMHOST:8080/osiam-resource-server/Users/ID
```

The response contains the SCIM compliant record of the user from the OSIAM database.

e.g.:

```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X GET OSIAMHOST:8080/osiam-resource-server/Users/$ID
```
See [SCIMv2 specification](http://tools.ietf.org/html/draft-ietf-scim-api-02#section-3.2.1) for further details.

### Get the User of the actual accessToken

To know the user of the actual accessToken OSIAM implemented an /me interface. For more detail information please look [here](#facebooks-me).

### Create a new User

To create a new user you need to send the user input as JSON via POST to the URL

```http
http://OSIAMHOST:8080/osiam-resource-server/Users
```

the response will be the created user.

e.g.:

```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X POST LOCALHOST:8080/osiam-resource-server/Users -d '{"schemas":["urn:scim:schemas:core:1.0"],"externalId":"admin","userName":"Arthur Dent","password":""}'
```

See [scim 2 rest spec](http://tools.ietf.org/html/draft-ietf-scim-api-02#section-3.1) for further details.

### Replace an existing User
To replace an existing user you need to send the user input as json via put to the url

```http
http://OSIAMHOST:8080/osiam-resource-server/Users/$ID
```

the response will be the replaced user in json format.

e.g.:
```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X PUT OSIAMHOST:8080/osiam-resource-server/Users/$ID -d '{"schemas":["urn:scim:schemas:core:1.0"], "externalId":"admin","userName":"Arthur Dent","password":""}'
```

See [scim 2 rest spec](http://tools.ietf.org/html/draft-ietf-scim-api-02#section-3.3.1) for further details.

### Update an User
To update an existing user you need to send the fields you which to update oder delete as json via patch to the url

```http
http://OSIAMHOST:8080/osiam-resource-server/Users/$ID
```

the response will be the updated user in json format.

e.g.:
```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X PATCH OSIAMHOST:8080/osiam-resource-server/Users/$ID -d '{"schemas":["urn:scim:schemas:core:1.0"], "externalId":"admin","userName":"Arthur Dent","password":""}'
```
See [scim 2 rest spec](http://tools.ietf.org/html/draft-ietf-scim-api-02#section-3.3.2) for further details.

### Delete an User
To delete an existing user you need to call the url via delete

```http
http://OSIAMHOST:8080/osiam-resource-server/Users/$ID
```

the response will be the http status.

e.g.:

```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X DELETE OSIAMHOST:8080/osiam-resource-server/Users/$ID
```

## Group management

This section will descripe the handling of user in the osiam context.

### Get a single Group

To get a single group you need to send a GET request to the url 

```http
http://OSIAMHOST:8080/osiam-resource-server/Groups/$ID
```

the response will be a osiam group in json format.

e.g.:

```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X GET OSIAMHOST:8080/osiam-resource-server/Groups/$ID
```

See [scim 2 rest spec](http://tools.ietf.org/html/draft-ietf-scim-api-02#section-3.2.1) for further details.

### Create a new Group
To create a new group you need to send the group input as json via post to the url

```http
http://OSIAMHOST:8080/osiam-resource-server/Groups
```

the response will be the created group in json format.

e.g.:

```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X POST OSIAMHOST:8080/osiam-resource-server/Groups -d '{"schemas":["urn:scim:schemas:core:1.0"],"displayName":"adminsGroup"}'
```

See [scim 2 rest spec](http://tools.ietf.org/html/draft-ietf-scim-api-02#section-3.1) for further details.

### Replace an existing Group
To replace a group you need to send the group input as json via put to the url

```http
http://OSIAMHOST:8080/osiam-resource-server/Groups/$ID
```

the response will be the replaced group in json format.

e.g.:

```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X PUT OSIAMHOST:8080/osiam-resource-server/Groups/$ID -d '{"schemas":["urn:scim:schemas:core:1.0"], "displayName":"Group1"}'
```
See [scim 2 rest spec](http://tools.ietf.org/html/draft-ietf-scim-api-02#section-3.3.1) for further details.

### Update an Group
To update a group you need to send the fields you which to update oder delete as json via patch to the url

```http
http://OSIAMHOST:8080/osiam-resource-server/Groups/$ID
```

the response will be the updated group in json format.

e.g.:

```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X PATCH OSIAMHOST:8080/osiam-resource-server/Groups/$ID -d '{"schemas":["urn:scim:schemas:core:1.0"], "displayName":"adminsGroup"}'
```
See [scim 2 rest spec](http://tools.ietf.org/html/draft-ietf-scim-api-02#section-3.3.2) for further details.

### Delete an Group
To delete a group you need to call the url via delete

```http
http://OSIAMHOST:8080/osiam-resource-server/Groups/$ID
```

the response will be status.

e.g.:

```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X DELETE OSIAMHOST:8080/osiam-resource-server/Groups/$ID
```

# OSIAM

## Client Management
The client management is a osiam defined endpoint to manage the clients needed for the oauth flow.

URI:
```http
http://OSIAMHOST:8080/osiam-resource-server/Client
```

The client role in oauth is described as follows:

An application making protected resource requests on behalf of the resource owner and with its authorization.  The term "client" does
not imply any particular implementation characteristics (e.g., whether the application executes on a server, a desktop, or other
devices).

[OAuth2 Roles](http://tools.ietf.org/html/rfc6749#section-1.1)

### Client configuration
A client in Osiam consists of the following configurable values:

* accessTokenValiditySeconds = is the validity in seconds of an access token
* refreshTokenValiditySeconds = is the validity in seconds of an refresh token
* redirectUri = is the uri for user agent redirection as described in [OAuth2 RFC](http://tools.ietf.org/html/rfc6749#section-3.1.2)
* clientSecret = the clients secret is part of the client credentials and will be generated
* scope = the access token scopes which are allowed for the client
* grants = the allowed grants for the client. Default is authorization_grant and refresh_token
* implicit = the value indicates whether the client authorization to access protected resources is done with or without asking the user
* validityInSeconds = is the validity in seconds for the client authorization to access protected resources if implicit is not wanted
* expiry = the day of expiry of the client authorization to access protected resources. Is generated in dependence on validityInSeconds

### Get a single client
To get a single client you need to send a GET request to the url 

```http
http://OSIAMHOST:8080/osiam-auth-server/Client/$ID
```
the response will be a osiam client in json format.

e.g.:

```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X GET OSIAMHOST:8080/osiam-resource-server/Client/$ID
```

### Create a client
To create a new client you need to send the client input as json via post to the url

```http
http://OSIAMHOST:8080/osiam-auth-server/Client
```

the response will be the created client in json format.

e.g.:

```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X POST OSIAMHOST:8080/osiam-resource-server/Client -d '{"id": "client_id", "accessTokenValiditySeconds": "1337", "refreshTokenValiditySeconds": "1337", "redirectUri": "http://OSIAMHOST:5000/stuff", "scope": ["POST", "PUT", "GET", "DELETE", "PATCH"], "validityInSeconds": "1337", "implicit": "false", "grants": ["authorization_code", "client_credentials", "password", "refresh-token"]}'
```

### Replace an client
To replace a client you need to send the client input as json via put to the url

```http
http://OSIAMHOST:8080/osiam-resource-server/Client/$ID
```

the response will be the replaced client in json format.

e.g.:

```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X PUT OSIAMHOST:8080/osiam-resource-server/Client/$ID 
-d '{"id": "client_id", "accessTokenValiditySeconds": "1337", "refreshTokenValiditySeconds": "1337", "redirectUri": "http://OSIAMHOST:5000/stuff", "scope": ["POST", "PUT", "GET", "DELETE", "PATCH"], "validityInSeconds": "1337", "implicit": "false", "grants": ["authorization_code", "client_credentials", "password", "refresh-token"]}'
```

### Delete an client
To delete a client you need to call the url via delete

```http
http://OSIAMHOST:8080/osiam-resource-server/Client/$ID
```

the response will be status.

e.g.:

```sh
curl -i -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" -X DELETE OSIAMHOST:8080/osiam-resource-server/Client/$ID
```

#Other

## Facebooks me

To get information about who granted the access_token you can call:

```http
http://OSIAMHOST:8080/osiam-resource-server/me
```

e.g.:
```sh
curl -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer $YOUR_ACCESS_TOKEN" OSIAMHOST:8080/osiam-resource-server/me
```

The response is like the result of facebooks /me:

```json
{
    "id":"cef9452e-00a9-4cec-a086-d171374ffbef",
    "name":"Mar Issa",
    "first_name":"Issa",
    "last_name":"Mar",
    "link":"not supported.",
    "userName":"admin",
    "gender":"female",
    "email":"mari@ssa.ma",
    "timezone":2,
    "locale":null,
    "verified":true,
    "updated_time":"2011-10-10T00:00:00.000+02:00"
}
```
