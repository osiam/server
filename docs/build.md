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

