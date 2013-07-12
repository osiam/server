# OSIAM Server

This is the combined authorization and resource server of the OSIAM project.

Right now only PostgreSQL is supported as a database.

# Build and Deployment

To build the osiam-server run
```sh
mvn clean install
```

in the osiam-server folder.

If you want to run the osiam-server in a embedded jetty instance run
```sh
 mvn jetty:run
```

To deploy the osiam-server into a running Tomcat copy the "osiam-server.war" into the webapp folder in your Tomcat installation.


## Authorization Server

The osiam-server is based on:

* Spring-Core 3.2
* Spring Security OAuth2 1.0.0.RC3 
* Hibernate

and provides

* [OAuth2 Authorization Code Flow](http://tools.ietf.org/html/rfc6749#section-4.1)
* [SCIM 2.0 API](http://tools.ietf.org/html/draft-ietf-scim-api-01)


### Configuration

To create the database scheme you have to execute src/main/ressources/sql/init.sql. 

This SQL-Script will create all necessary tables as well as a demo user called Marissa with password 'koala'.

It also creates the first client entry with redirect URI set to localhost.

The client configuration is done via the database and the client management component.

The following values can be submited:
* token_scope
* redirect_uri
* accesstoken_validity_in_seconds
* refreshtoken_validity_in_seconds

The client_id and the client_secret are generated values.

URI to create a client:


To create a new client you need to get an access_token, with an existing
client, and call:


http://localhost:8080/osiam-server/Client

with a valid client e.q.:

```
{"accessTokenValiditySeconds": "1337", "refreshTokenValiditySeconds": "1337",
"redirect_uri": "http://localhost:5000/stuff", "scope": ["POST", "PUT", "GET", "DELETE", "PATCH"]}
```
and an access_token in the header.

```sh
curl -H "Accept: application/json" -H "Content-type: application/json" -H "Authorization: Bearer {YOUR_ACCESS_TOKEN}" -X POST localhost:8080/osiam-server/Client -d '{YOUR_CLIENT_AS_JSON}'
```

URI for getting and deleting a client:

DELETE http://localhost:8080/osiam-server/Client/{client_id}

The database configuration is done via properties file named

 db-config.properties

which looks like

```
db.driver=org.postgresql.Driver
db.url=jdbc:postgresql://$your_url:$port/$your_database
db.username=$your_username
db.password=$your_password
```

it also contains a parameter for profiling:
```
osiam.profiling=true
```

this config file must lie within the classpath.

For tomcat you have to edit the

shared.loader

property under

$your_tomcat_dir/conf/catalina.properties

to point to a correct folder.
