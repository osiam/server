## Install MySQL via apt-get
MySQL can be installed via apt-get:  

```
apt-get install mysql-server
```  

During the installation you will be prompt to enter password for the root user of MySQL.  

## Set client encoding
Edit the file ``/etc/mysql/my.cnf`` and add 

```
default-character-set = utf8
```  

## Create user and grant privileges

To create a user you need to be connected to the mysql server:  

```
mysql --user=root --password=<ROOT_PASSWORD> mysql
```

Then create the user osiam and grant privileges for a database.

```
GRANT ALL PRIVILEGES ON <DB_NAME>.* TO 'osiam'@'%' IDENTIFIED BY '<OSIAM_USER_PASSWORD>';
```

## Create database
To create the database enter following:

```
CREATE DATABASE <DATABASE_NAME>;
```

## Switch to the database

```
use <DATABASE_NAME>;
```

After that the database is ready for use and the server applications can be deployed.
Here an example, how the connection properties should looks like:
```
org.osiam.auth-server.db.vendor=mysql
org.osiam.auth-server.db.driver=com.mysql.jdbc.Driver
org.osiam.auth-server.db.dialect=org.hibernate.dialect.MySQLInnoDBDialect
org.osiam.auth-server.db.url=jdbc:mysql://localhost:3306/osiam
org.osiam.auth-server.db.username=root
org.osiam.auth-server.db.password=<PASSWORD>
```
