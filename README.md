# OSIAM NG Server

This is the combined authorization and resource server of the OSIAM NG project.

Right now only PostgreSQL is supported as a database.

# Build and Deployment

To build the authorization-server run
```sh
mvn clean install
```

in the authorization-server folder.

If you want to run the authorization-server in a embedded tomcat instance run
```sh
 mvn jetty:run
```

To deploy the authorization-server into a running Tomcat copy the "authorization-server.war" into the webapp folder in your Tomcat installation.


## Authorization Server

The osiam-server is based on:

* Srping-Core 3.2
* Spring Security OAuth2 1.0.0.RC3 
* Hibernate

and provides

* [OAuth2 Authorization Code Flow](http://tools.ietf.org/html/rfc6749#section-4.1)
* [SCIM 2.0 API](http://tools.ietf.org/html/draft-ietf-scim-api-01)


### Configuration

To create the database scheme you have to execute src/mein/ressources/sql/init.sql. 

This SQL-Script will create you all the needed tables as well as create a demo user called Marissa and a password 'koala'.

The database configuration is done via

The client credentials are as well hardcoded:
 * client_id=tonr
 * client_secret=secret
 * redirect_uri=http://localhost:8080/oauth2-client/accessToken

This will change very soon.

The database configuration is done via properties file named

 db-config.properties

which looks like

```
db.driver=org.postgresql.Driver
db.url=jdbc:postgresql://$your_url:$port/$your_database
db.username=$your_username
db.password=$your_password
```

this config file must lie within the classpath.

For tomcat you have to edit the

shared.loader

property under

$your_tomcat_dir/conf/catalina.properties

to point to a correct folder.


### Usage

To get an authorization code call:

<http://localhost:8080/authorization-server/oauth/authorize?client_id=tonr&response_type=code&redirect_uri=http://localhost:8080/oauth2-client/accessToken>

To get an access_token call:

```sh
curl --user tonr:secret -X POST -d "code=$CODE&grant_type=authorization_code&redirect_uri=http://localhost:8080/oauth2-client/accessToken" \
 http://localhost:8080/authorization-server/oauth/token
```

The client authentication is done via [Basic Access Authentication](http://tools.ietf.org/html/rfc2617).


## Resource Server

All scim calls are secured by oauth2, so have to send an access_token in order to get access, e.q.:

```sh
curl -H "Authorization: Bearer {YOUR_ACCESS_TOKEN}" http://localhost:8080/authorization-server/User/{id}
```

### Search

### User

### Group

