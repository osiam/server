#OSIAM Registration Module

This module contains the OSIAM registration functionality.

The functionality consists of three parts which will be:
 * user registration
 * change password mechanism
 * change E-Mail mechanism

# Deployment and configuration

## The operating system
We recommend to choose the latest OSIAM release version.
 * Release Repository: http://maven-repo.evolvis.org/releases/org/osiam/
 * GitHub Release Tags: https://github.com/osiam/server/releases

Download the OSIAM .war file for the registration-module

	$ wget http://maven-repo.evolvis.org/releases/org/osiam/osiam-registration-module/<VERSION>/osiam-registration-module-<VERSION>.war

and the OSIAM sources

	$ wget http://maven-repo.evolvis.org/releases/org/osiam/osiam-registration-module/<VERSION>/osiam-registration-module-<VERSION>-sources.jar

and unpack the sources

	$ unzip osiam-registration-module-<VERSION>-sources.jar

## Database setup

For the registration you need to add some extension fields into the database otherwise it will not work.

The extension is configured with it's own namespace and will not conflict user defined extensions.

Start the database commandline:

    $ sudo -u postgres psql

Now insert it as user osiam by calling

	$ psql -f ./sql/registration_extension.sql -U osiam

while being in the directory where you unpacked the sources.


## Configuring OSIAM

For the registration module OSIAM needs some configuration values. Create the file

	/etc/osiam/osiam-registration.properties

with content based on this example:

    #The registration properties
    osiam.web.registermail.from=
    osiam.web.registermail.subject=
    osiam.web.registermail.linkprefix=
    osiam.web.registermail.content.path=
    osiam.web.register.url=

    #The change password properties
    osiam.web.passwordlostmail.from=
    osiam.web.passwordlostmail.subject=
    osiam.web.passwordlostmail.linkprefix=
    osiam.web.passwordlostmail.content.path=
    osiam.web.password.url=

    #The change e-mail properties
    osiam.web.emailchange.from=
    osiam.web.emailchange.subject=
    osiam.web.emailchange.linkprefix=
    osiam.web.emailchange.content.path=
    osiam.web.email.url=
    osiam.web.emailchange-info.subject=
    osiam.web.emailchange-info.content.path=

    #The required extension configuration including the needed field definition
    #This are also the defaults and there is no need do change this values.
    osiam.internal.scim.extension.urn=urn:scim:schemas:osiam:1.0:Registration
    osiam.activation.token.field=activationToken
    osiam.one.time.password.field=oneTimePassword
    osiam.confirm.email.token.field=emailConfirmToken
    osiam.temp.email.field=tempMail

### Configuration values explanation

#### The registration values are:

**osiam.web.registermail.from**

The sender address from where the mail was send to the user.

**osiam.web.registermail.subject**

The subject of the e-mail.

**osiam.web.registermail.linkprefix**

The controller URL on client side where the registration link will point to.
This must be on client side and must not point directly to the osiam registration module due to security issues.
The URL must end with either a '?' or a '&' character. This depends on whether you already have some parameter's or not.
If you don't have url parameter add the '?' character otherwise add the '&' character.

Here some examples:
 * http://localhost:1234/client/activationMail?
 * http://localhost:1234/client/activationMail?someParameter=value&

**osiam.web.registermail.content.path**

This is the path to the content text file for the e-mail. If no path is specified a default content will be used.

**osiam.web.register.url**

The controller URL on client side where the registration call arrives, submitted by the HTML from.
This must be a URL on client side and must not point directly to the osiam registration module due to security issues.

#### The change password values are:

**osiam.web.passwordlostmail.from**

The sender address from where the mail was send to the user.

**osiam.web.passwordlostmail.subject**

The subject of the e-mail.

**osiam.web.passwordlostmail.linkprefix**

The controller URL on client side where the change password link will point to.
This must be on client side and must not point directly to the osiam registration module due to security issues.
The URL must end with either a '?' or a '&' character. This depends on whether you already have some parameter's or not.
If you don't have url parameter add the '?' character otherwise add the '&' character.

Here some examples:
 * http://localhost:1234/client/activationMail?
 * http://localhost:1234/client/activationMail?someParameter=value&

**osiam.web.passwordlostmail.content.path**

This is the path to the content text file for the e-mail. If no path is specified a default content will be used.

**osiam.web.password.url**

The controller URL on client side where the change password call arrives, submitted by the HTML from.
This must be a URL on client side and must not point directly to the osiam registration module due to security issues.

#### The change E-Mail values are:

**osiam.web.emailchange.from**

The sender address from where the mail was send to the user.

**osiam.web.emailchange.subject**

The subject of the confirmation e-mail.

**osiam.web.emailchange.linkprefix**

The controller URL on client side where the change e-mail link will point to.
This must be on client side and must not point directly to the osiam registration module due to security issues.
The URL must end with either a '?' or a '&' character. This depends on whether you already have some parameter's or not.
If you don't have url parameter add the '?' character otherwise add the '&' character.

Here some examples:
 * http://localhost:1234/client/activationMail?
 * http://localhost:1234/client/activationMail?someParameter=value&

**osiam.web.emailchange.content.path**

This is the path to the content text file for the confirmation e-mail. If no path is specified a default content will be used.

**osiam.web.email.url**

The controller URL on client side where the change e-mail call arrives, submitted by the HTML from.
This must be a URL on client side and must not point directly to the osiam registration module due to security issues.

**osiam.web.emailchange-info.subject**

The subject of the information e-mail. It will be send to the old address indicating the new address.

**osiam.web.emailchange-info.content.path**

The subject of the information e-mail. It will be send to the old address indicating the new address.

## Deployment into the application server
To deploy the registration module into Tomcat server the downloaded .war files need to be renamed and moved into Tomcat's webapp directory:

	$ sudo mv osiam-auth-server-<VERSION>.war /var/lib/tomcat7/webapps/osiam-auth-server.war

For further information on Tomcat's configuration [read this section:](https://github.com/osiam/server/wiki/detailed_reference_installation#deployment-into-the-application-server)

# Integration
For the integration in your app you need to define a client side mechanism for calling the registration module's HTTP API and
to receive the user requests. The user agent will not directly speak with the registration module.

You need also to enhance the request with the authorization header and a valid access token before sending to the
registration modules HTTP endpoint.

The base URI for the module is: **http://HOST:PORT/osiam-registration-module**

# User registration
The registration will be done via a double opt in mechanism.

In the first step the user will register at osiam via the client application.
Then a email will be send to the address provided in the first step. The content of that email will
include a registration link with an activation token.
The second step for the user will be to confirm his email address by going to that url.
The client application will then send the activation request to osiam and the user will be activated if the
activation token validation will be correct and the user is able to login.

There are three HTTP endpoints:

<table>
    <tr>
        <td> /register </td>
        <td> GET </td>
        <td> no access token needed </td>
        <td> Will provide a HTML form with the required fields for registration including validation.
         The request will be submitted to the URL configured in the 'osiam.web.register.url' parameter. </td>
    </tr>
    <tr>
        <td> /register/create </td>
        <td> POST </td>
        <td> access token in the Authorization header as HTTP Bearer authorization </td>
        <td> Will create the user with activation token and will send you the HTTP status code as response.
         The user will be disabled until email confirmation and he is not able to login yet.
         Also a email will be send to the user with the link pointing to the 'osiam.web.registermail.linkprefix' config parameter</td>
    </tr>
    <tr>
        <td> /register/activate </td>
        <td> POST </td>
        <td> access token in the Authorization header as HTTP Bearer authorization.
         Parameters: 'userId' and 'activationToken' from the email's confirmation link. </td>
        <td> The activation token will be validated and the user will be enabled for login if the validation was successful.
         You get the HTTP status code as response. </td>
    </tr>
</table>

# Change password mechanism
The change password mechanism has also multiple steps.

First of all the user has to indicate on client side that he lost his password. Then the client will send a request to
the registration module. Osiam will now generate a one time password and will send the user an email
with a link in the content, including the one time password. This url will also be hosted on client side an will point
to the 'osiam.web.passwordlostmail.linkprefix' config parameter.
The user need to go to the url from the email's content and has to enter his new password.
Then the request must be submitted to the registration module where the one time password verification will be triggered
and if this was successful the new password will be saved and the user is able to login with the new password.

There are three HTTP endpoint:

<table>
    <tr>
        <td> /password/lostForm </td>
        <td> GET </td>
        <td> no access token needed </td>
        <td> Will provide a HTML form with the required fields for change password including validation.
         The request will be submitted to the URL configured in the 'osiam.web.password.url' parameter. </td>
    </tr>
    <tr>
        <td> /password/lost </td>
        <td> POST </td>
        <td> access token in the Authorization header as HTTP Bearer authorization
         Parameters: 'userId' for whom the password should be changed. </td>
        <td> This will generate a one time password an sending the user an email with a confirmation link
         pointing to the 'osiam.web.passwordlostmail.linkprefix' config parameter including his one time password.
         The response will be the HTTP status code. </td>
    </tr>
    <tr>
        <td> /password/change </td>
        <td> POST </td>
        <td> access token in the Authorization header as HTTP Bearer authorization
         Parameters: 'userId', 'oneTimePassword' and 'newPassword' </td>
        <td> This will validate the one time password and save the new password if the validation will be successful.
          The response will be the HTTP status code and the previously updated user if successful. </td>
    </tr>
</table>

# Change E-Mail mechanism
The change e-mail mechanism has also multiple steps.

First of all the user has to indicate on client side that he want to change his e-mail address.
Then the client will send a request including the new e-mail to the registration module.
Osiam will now generate a confirmation token and will send the user an email with a link in the content,
including the confirmation token. This url will also be hosted on client side an will point
to the 'osiam.web.emailchange.linkprefix' config parameter.
The user need confirm his e-mail address by going to the url from the email's content.
Then the request must be submitted to the registration module where the confirmation token verification will be triggered
and if this was successful the new e-mail address will be stored. Finally an information e-mail will be send to the user's
old e-mail address.

There are three HTTP endpoint:

<table>
 <tr>
     <td> /email </td>
     <td> GET </td>
     <td> no access token needed </td>
     <td> Will provide a HTML form with the required fields for change e-mail including validation.
      The request will be submitted to the URL configured in the 'osiam.web.email.url' parameter. </td>
 </tr>
 <tr>
     <td> /email/change </td>
     <td> POST </td>
     <td> access token in the Authorization header as HTTP Bearer authorization
      Parameters: 'newEmailValue' the new user's e-mail address. </td>
     <td> This will generate a confirmation token an sending the user an email with a confirmation link
      pointing to the 'osiam.web.emailchange.linkprefix' config parameter including the confirmation token.
      The response will be the HTTP status code. </td>
 </tr>
 <tr>
     <td> /email/confirm </td>
     <td> POST </td>
     <td> access token in the Authorization header as HTTP Bearer authorization
      Parameters: 'userId', 'confirmToken' </td>
     <td> This will validate the confirmToken and save the new e-mail if the validation will be successful.
       The response will be the HTTP status code and the previously updated user if successful. </td>
 </tr>
</table>