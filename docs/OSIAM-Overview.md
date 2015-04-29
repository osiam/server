# Overview of OSIAM
This page contains some high level view on the OSIAM project. Visit https://osiam.org for additional information on OSIAM.

Chapters:
- [Functional overview](#functional-overview)
  - [Architecture model](#architecture-model)
  - [SCIMv2](#scimv2)
  - [OAuth 2.0](#oauth-20)
  - [Facebook Connect](#facebook-connect)
  - [OSIAM specific interfaces](#osiam-specific-interfaces)
  - [Interface / endpoint overview and status](#interface--endpoint-overview-and-status)
  - [Homepage](#homepage)
  - [The Forum](#the-forum)
  - [The Backlog](#the-backlog)
  - [Public CI](#public-ci)
  - [Software Quality Analytics](#software-quality-analytics)
  - [Issue Tracker](#issue-tracker)
  - [Repositories](#repositories)
- [Technical overview](#technical-overview)
  - [Java](#java)
  - [Operating System](#operating-system)
  - [Databases](#databases)

## Functional overview
OSIAM stands for Open Source Identity & Access Management. It is a secure and lightweight identity store that handles also processes like authentication and authorization. OSIAM is based on open standards. These standards in combination provide a secure way letting trusted or untrusted clients access.
OSIAM is developed with the following use cases in mind:
* secure storage of identity data for a web, cloud, mobile or legacy application
* controlled access to identity data from multiple applications
* central indentity management for cloud services
* secure login (e.g. 2-factor authentication / step-up authentication)
* single sign-on for applications
* free SCIMv2 provider

### Architecture model

The OSIAM server is splitted into the authentication server and the resource server. These two server can run at the same server machine and the same database or on different server machines and also with different databases.

#### Authentication Server

The purpose of the Authentication Server is to authenticate the client and the user which are trying to get access to the resource server by providing a valid access token.

It also validate the given access token for the resource server.

#### Resource Server

The purpose of the Resource server is to handle all SCIM endpoints, the OAuth2 client endpoint and facebook's /me endpoint.

#### Gateways between these systems

There are 2 gateways between the auth-server and the resource-server.

**auth-server >> resource-server**
- GET: used to get the needed information about the user who wants to log in

**resource-server >> auth-server**
- GET: validates a given access token

![Picture ArchitectureModel](https://dev.tarent.de/plugins/mediawiki/wiki/osiamorgleg/images/7/79/ArchitectureModel.jpg)

### SCIMv2
SCIM ([System for Cross-domain Identity Management](http://www.simplecloud.info)) is a standard with it's second incarnation being in draft phase at the IETF.
SCIMv2 defines a [REST](http://en.wikipedia.org/wiki/Representational_state_transfer) based interface for managing users and groups with a focus on web based scenarios, though it is also a good choice for internal use cases to ensure interoperability and simplicity of the solution.
SCIM supports similar use cases LDAP does, it just does not care about the storage (e.g. directory, RDBMS, NoSQL-DB) and is made for todays decentral internet technology. The specification therefore also covers issues of user account provisioning and interoperability topics with existing internet standards.

### OAuth 2.0
[OAuth 2.0](http://oauth.net/2/) is utilized to secure the OSIAM API. OAuth is a widley accepted standard supported by all large service providers in the internet like Amazon, Google, LinkedIn and Salesforce [to name a few](http://en.wikipedia.org/wiki/OAuth#List_of_OAuth_service_providers).
With OAuth 2.0 single sign-on can be implemented as well as data access authorization for data consumer. Most of the OSIAM interfaces can only be used if the user is authenticated against OSIAM and the user is actually authorized to make use of the interfaces.
A so called "scope" in OAuth defines a certain action or data segment a client is allowed to access. A user gets scopes assigned so that the client can only take actions the logged in user is authorized for.
OSIAM supports three different OAuth use cases - for detailed information please see the [OAuth 2.0 specification](http://tools.ietf.org/html/rfc6749):

#### Authorization Code Grant
This is a so called "3-legged" flow and the recommended and most secure option as the client does not get access to the user's login credentials, while it is still being verified that the user is allowed to access data that resides in OSIAM.
In this scenario OSIAM provides the login page.
 
#### Resource Owner Password Credentials Grant
When a client can be trusted the user can provide the login credentials to the client and the client checks OSIAM if the authentication data is correct using a REST call. This is the less secure option for user login and authorization, especially as the authorization is done implicit.
In this scenario the client provides the login page.

#### Client Credentials Grant
This authorization flow uses a basic authentication method to authenticate and authorize the client. The user is not involved in this process so basically the client is fully flexible in make API calls against OSIAM no matter what user is logged in. This flow is not recommended and only acceptable for certain setups.

### Facebook Connect
Facebook Connect is based on OAuth 2.0 but it has some specialities. OSIAM can act as a Service Provider for the Facebook Connect login and logout process. That means you can easily run a Facebook Connect Service Consumer against OSIAM.
We have verified the compatiblity of our Facebook Connect endpoint using [LifeRays Facebook Connect Service Consumer](http://www.liferay.com/en/documentation/liferay-portal/6.1/user-guide/-/ai/facebook-connect-sso) implementation.

### LDAP Login
While using the [Authorization Code Grant](#authorization-code-grant) the User can login against an LDAP server. Fore more detail information how to configure OSIAM to use the LDAP login please see [here](detailed_reference_installation.md#configuring-ldap-login).

### OSIAM specific interfaces
While developing OSIAM we have the need to define interfaces that are not currently covered by an open standard. Like the Client interface we have developed for OSIAM before the discussion on a client interface was started in the SCIMv2 context. We try to keep the number of OSIAM specific interfaces low and will move to a standard based interface as soon as there is one in place. 

### Interface / endpoint overview and status
To get a basic overview of OSIAMs API the following section shows lists of the HTTP end points OSIAM has implemented or that require implementation based on the related standard. Additional OSIAM specific end points may be added to the list upon implementation.

#### Technical endpoints (authentication / authorization)
<table>
<tr><td> Endpoint          </td><td> Type      </td><td> Public</td><td>Comment              </td></tr>
<tr><td> /oauth2/authorize </td><td> OAuth 2.0 </td><td> yes   </td><td>                     </td></tr>
<tr><td> /oauth2/token     </td><td> OAuth 2.0 </td><td> no    </td><td>                     </td></tr>
<tr><td> /oauth2/revoke    </td><td> OAuth 2.0 </td><td> no    </td><td> not yet implemented </td></tr>
<tr><td> /authentication/user/{userId} </td><td> OAuth 2.0 </td><td> yes   </td><td> GET </td></tr>
<tr><td> /authentication/client/{clientId} </td><td> OAuth 2.0 </td><td> yes   </td><td> GET </td></tr>
<tr><td> /authentication/client/{clientId} </td><td> OAuth 2.0 </td><td> yes   </td><td> PUT </td></tr>
<tr><td> /token/validate/{accessToken} </td><td> OAuth 2.0 </td><td> yes   </td><td> GET </td></tr>
<tr><td> /token/{accessToken} </td><td> OAuth 2.0 </td><td> yes   </td><td> GET </td></tr>
</table>

#### Functional endpoints (resources)
<table>
<tr><td> /Users                  </td><td> SCIMv2    </td><td> GET PUT POST DELETE UPDATE </td><td>                     </td></tr>
<tr><td> /Groups                 </td><td> SCIMv2    </td><td> GET PUT POST DELETE UPDATE </td><td>                     </td></tr>
<tr><td> /Bulk                   </td><td> SCIMv2    </td><td> POST                       </td><td> not yet implemented </td></tr>
<tr><td> /.search                </td><td> SCIMv2    </td><td> GET POST                   </td><td> not yet implemented </td></tr>
<tr><td> /ServiceProviderConfigs </td><td> SCIMv2    </td><td> public                     </td><td>                     </td></tr>
<tr><td> /Schemas                </td><td> SCIMv2    </td><td> public                     </td><td> not yet implemented </td></tr>
<tr><td> /Clients                </td><td> OSIAM     </td><td> GET PUT POST DELETE UPDATE </td><td> SCIM based implementation as soon as the standard is defined</td></tr>
<tr><td> /me                     </td><td> Facebook  </td><td> EMAIL                      </td><td>                     </td></tr>
</table>

## Community resources overview
Beside this overview and the README you can retrieve additional information from various places:

### Website
Visit the website for general information about OSIAM
* https://www.osiam.org

### The Forum
If you have questions or inquiries or just get in contact with us for any other reason, please go to forum and drop us a posting at
* https://groups.google.com/forum/?fromgroups#!forum/osiam

### The Backlog
If you want to know more about the next steps OSIAM is taking you can take a look into our product backlog at
* https://jira.osiam.org

If you need a general idea about the roadmap [osiam.org](https://www.osiam.org/display/ZLIintranet/Get+Started#GetStarted-What'snext?) will be the better place.

### Public CI
We have an internal continuos integration server but also the OSIAM main module is built by an external CI in parallel, visit:
* https://travis-ci.org/osiam/server

### Software Quality Analytics
Our Sonar is checking static parameters of our software quality and we have quite challanging targets for that in our Definition of Done, e.g. 0 violations policy, >90% test coverage. Have a look yourself if we meet our targets by visiting:
* https://sonar.osiam.org

### Issue Tracker 
If you find a bug or you are missing a feature in OSIAM you can help us to improving OSIAM by creating a ticket in our issue tracker. Also if you want to run OSIAM on a currently not certified platform or  database, these are important requirements for us, so please add a ticket at the tracker:
* https://github.com/osiam/server/issues

### Repositories
The document you are reading is most likely part of OSIAM's main repository. See what other repositories are there by pointing your browser at:
* https://github.com/osiam

In each repository you should be able to find a README.md that gives you an idea what the contents of the repository are for.

## Technical overview

Below you can find the technical prerequisites and limitations. Whenever these are causing a problem for you either for testing and development or in production, please let us known by creating a [ticket](https://jira.osiam.org/secure/RapidBoard.jspa?rapidView=8&view=planning).

### Java
OSIAM is based on Java technology utilizing parts of the Spring framework. It is in general independent of the platform it runs on as long as the proper Java environment is in place. OSIAM requires one of the following JDKs / JREs:
* OpenJDK / OpenJRE 7
* OracleJDK / OracleJRE 7

### Operating System
OSIAM has been up and running under various operating system (Fedora, CentOS, Windows) utilizing their available Java7 stack. For production you should only use certified platforms:
* Debian Wheezy

### Databases
OSIAM uses database abstraction technologies (Hibernate) that enables us to support a wide variety of databases. For production you should only use certified platforms:
* min: PostgreSQL 9.1
