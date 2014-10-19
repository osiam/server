#OSIAM Server

This repository contains the OSIAM Server. 

For more information and documentation visit the [repo's Wiki](https://github.com/osiam/server/wiki).

For additional information visit OSIAM's homepage at [www.osiam.org](https://www.osiam.org).

The actual version can be found at http://maven-repo.evolvis.org/releases/org/osiam/osiam-server-parent/.

#project structure

This project gets build with maven, the module structure is

* resource-server -- contains the source for the OSIAM Resource Server -> [Wiki](https://github.com/osiam/server/wiki)
* auth-server -- contains the source for the OSIAM Auth Server -> [Wiki](https://github.com/osiam/server/wiki)
* interaction-model -- contains the source for the OSIAM Interaction Model
* registration-module -- contains the source for the OSIAM Registration Module -> [Wiki](https://github.com/osiam/server/wiki/OSIAM-Registration-Module)


# Requirements

* Java 1.7 (tested with java version "1.7.0_25" OpenJDK Runtime Environment)
* PostgreSQL (tested with PostgreSQL 9.2.4)
* for performance reasons it recommend to have at least 2 GB RAM
* Tomcat (tested with 7.0.37)

# Issue tracker for the Server

Issues, bugs and feature requests can be brought to us using [OSIAM's issue tracker](https://github.com/osiam/server/issues).
