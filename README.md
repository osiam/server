# OSIAM Server

This is the combined authorization and resource server of the OSIAM project.

Right now only PostgreSQL is supported as a database.



## Authorization Server

The osiam-server is based on:

* Spring-Core 3.2
* Spring Security OAuth2 1.0.0.RC3 
* Hibernate


### Configuration

To create the database scheme you have to execute src/main/ressources/sql/init.sql. 

This SQL-Script will create all necessary tables as well as a demo user called Marissa with password 'koala'.

It also creates the first client entry with redirect URI set to localhost.

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
