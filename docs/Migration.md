## from 1.3.x to 2.0

Flyway was added to support schema upgrades for the next versions. To distinguish between the currently supported database systems you have to change the files `/etc/osiam/resource-server.properties` and `/etc/osiam/auth-server.properties` like this:

**NOTE:** these steps must be taken **BEFORE** the deployment of the new web apps!

In `/etc/osiam/resource-server.properties` add a line containing

    org.osiam.resource-server.db.vendor=<database vendor>

`<database vendor>` can be one of:

* postgresql
* mysql

Repeat this process for the file `/etc/osiam/auth-server.properties`, but this time add a line containing

    org.osiam.auth-server.db.vendor=<database vendor>

The possible values for `<database vendor>` are the same as above. You have to restart Tomcat (or any other Servlet container you're using) after this.

If you have an existing database and cannot "throw your data away" (or backup and restore it) use the following instructions to migrate your database to be ready for Flyway. Otherwise, purge all data in your database and deploy the new web apps using your favorite way of deployment.

### Migrate existing database

Get the Flyway command line client from http://flywaydb.org/getstarted/download.html. Download the `flyway-commandline-X.X.X.tar.gz (without JRE)` package. Unpack the archive on a server that has access to your database and change dir into the unpacked folder. Now, edit the file `conf/flyway.conf` and change the property `flyway.url` to suit your needs. You can copy the JDBC connection URL from `/etc/osiam/resource-server.properties`. Next, set `flyway.user` and `flyway.password` using the settings from `/etc/osiam/resource-server.properties` like before. Uncomment and set the property `flyway.table` to `resource_server_schema_version`. Now it's time to run the baseline process:

    $ ./flyway baseline

This process must now be repeated for the `auth-server`. Edit the file `conf/flyway.conf` and change the property `flyway.url`. This time, copy the JDBC connection URL from `/etc/osiam/auth-server.properties`. Next, set `flyway.user` and `flyway.password` using the settings from `/etc/osiam/auth-server.properties` like before. Uncomment and set the property `flyway.table` to `auth_server_schema_version`. After that, run the baseline process:

    $ ./flyway baseline

You can now deploy the new web apps using your favorite way of deployment.

## from 1.2 to 1.3
The localization files are now named to "i18n" instead of "l10n".
Also now they have additional descriptions in the middle of a expression.

Here are a few examples:

<table>
    <tr>
        <th> Before: </th>
        <th> After: </th>
    </tr>
    <tr>
        <td> registration.title </td>
        <td> registration.html.title </td>
    </tr>
    <tr>
        <td> registration.headline </td>
        <td> registration.html.headline </td>
    <tr>
        <td> registration.userName </td>
        <td> registration.user.userName </td>
    </tr>
    <tr>
        <td> Email.registrationUser.email </td>
        <td> registration.validation.email </td>
    </tr>
</table>

## from 0.21 to 1.0
* You have to run the auth server sql script (ATTENTION!): https://github.com/osiam/server/blob/master/auth-server/src/main/sql/migration/from_0_20_to_0_21.sql
* The validation token endpoint changed. Put the token you want to validate directly in the authorization header instead of putting in the URI.

## from 0.19 to 0.20
* The auth-server has a new interface with bootstrap.
* [How-to integrate the 3-legged flow into your application](detailed_reference_installation#configuring-login-with-authorization-code-grant)
* Please consider the new interfaces:
<table>
 <tr>
     <th> Description </th>
     <th> HTTP-Method </th>
     <th> Old URI </th>
     <th> New URI </th>
 </tr>
 <tr>
     <td> Login page </td>
     <td> GET </td>
     <td> /login or /login.jsp </td>
     <td> /login or /login.html </td>
 </tr>
 <tr>
     <td> Login check </td>
     <td> POST </td>
     <td> /login.do </td>
     <td> /login/check </td>
 </tr>
 <tr>
     <td> Login error </td>
     <td> GET </td>
     <td> /login.jsp?authentication_error=true </td>
     <td> /login/error </td>
 </tr>
</table>

## from 0.18 to 0.19
* The registration module has been moved to his own [github repository](https://github.com/osiam/addon-self-administration)

## from 0.17 to 0.18
Only updates in the registration module!
* You need to update your osiam-registration.properties like explained here: OSIAM-Registration-Module#wiki-migration
* New template engine for the emails! You don't have to change anything, but the email content and subject changed for the four different emails. You could now add your own email template files! Have look at the new registration module manual: OSIAM-Registration-Module
 
## from 0.16 to 0.17
* All rows and their relations in `scim_extension_field_value` with empty `value` column will be deleted
* Add indices to all multivalueattributes (`scim_email`, ...) and `scim_extension_field_value`<br/>
Run [migration script](https://github.com/osiam/server/blob/master/resource-server/src/main/resources/sql/migration/from_0_16_to_0_17.sql) -> **be aware of running against productive system without testing**

## from 0.15.6 to 0.16
* `RoleEntity` and `X509CertificateEntity` - now both have a Type field.<br />
PostgreSQL Database Migration:<br />
ALTER TABLE scim_certificate ADD COLUMN type character varying(255);<br />
ALTER TABLE scim_roles ADD COLUMN type character varying(255);
