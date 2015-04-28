Welcome to OSIAM!

Please have a look at the website of OSIAM: https://www.osiam.org
GitHub: https://github.com/osiam/server
Jira: https://jira.osiam.org

If you want to run OSIAM:
https://github.com/osiam/server/blob/master/docs/detailed_reference_installation.md

If you find this file in the distribution of the OSIAM server, please jump
to the installation instructions among,

if not, just download the .zip or .tar.gz distribution file here:
https://maven-repo.evolvis.org/releases/org/osiam/osiam-server-distribution
-> GitHub: https://github.com/osiam/server/tree/master/distribution

or run the following commands on your console:
$ git clone https://github.com/osiam/server.git
$ cd server
$ mvn clean install
$ cd distribution/target
$ gunzip xfv osiam-server-${VERSION}-dist-distribution.tar.gz
OR
$ unzip osiam-server-${VERSION}-dist-distribution.zip
$ cd osiam-server-${VERSION}-dist-distribution
Now you could follow the instructions:

Just copy the .war files to the application server of your choice and
before you copy the configuration files, please check the config values
of both property files, which are in the /configuration folder of both projects:
https://github.com/osiam/server/blob/master/docs/detailed_reference_installation.md#configuring-osiam

After that, copy all files of the /configuration folder of both projects (resource and auth server)
to your shared classpath of the application server of your choice, like here described:
https://github.com/osiam/server/blob/master/docs/detailed_reference_installation.md#deployment-into-the-application-server

Then import the sql files to your already configured database (and referenced in the config files)
which are in the /sql folder also of both projects:
https://github.com/osiam/server/blob/master/docs/detailed_reference_installation.md#database-setup

Now have fun with OSIAM.
