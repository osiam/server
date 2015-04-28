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

## Dropping tables
If tables already exists, you can drop them using the drop scripts:

```
\. /path/to/auth-server/src/main/sql/mysql/drop_all.sql
\. /path/to/resource-server/src/main/sql/mysql/drop_all.sql
```

## Create the tables and add example data for the auth-server
To create the tables execute following SQL scripts:

```
\. /path/to/auth-server/src/main/sql/mysql/init_ddl.sql
\. /path/to/auth-server/src/main/sql/mysql/example_data.sql
```

## Create the tables and add example data for the resource-server
To create the tables execute following SQL scripts:

```
\. /path/to/auth-server/src/main/sql/mysql/init_ddl.sql
\. /path/to/auth-server/src/main/sql/mysql/init_data.sql
\. /path/to/auth-server/src/main/sql/mysql/example_data.sql
```
After that is the DB ready for use. Here an example, how the connection properties should looks like:
```
# Database properties
org.osiam.auth-server.db.driver=com.mysql.jdbc.Driver
org.osiam.auth-server.db.dialect=org.hibernate.dialect.MySQLInnoDBDialect
org.osiam.auth-server.db.url=jdbc:mysql://localhost:3306/osiam
org.osiam.auth-server.db.username=root
org.osiam.auth-server.db.password=<PASSWORD>
```
