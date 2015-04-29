# OSIAM Server

## 2.0 - 2015-04-29

**Breaking changes!**

This release introduces breaking changes, due to the introduction of automatic
database schema updates powered by Flyway. See the
[migration notes](docs/Migration.md#from-13x-to-20) for further details.

- [feature] Support automatic database migrations
- [feature] create JAR containing the classes of app
- [fix] lower constraint index lengths for MySQL
- [fix] replace Windows line endings with Unix ones in SQL scripts
- [change] decrease default verbosity
- [change] bump dependency versions
- [docs] move documentation from Wiki to repo
- [docs] rename file RELEASE.NOTES to CHANGELOG.md

## 1.3.2 - 2014-11-24
- release because of fixes in addon-administration

## 1.3.1 - 2014-10-27
- release because of fixes in addon-self-administration

## 1.3 - 2014-10-17
- [fix] Umlauts encoding problems
- [fix] Infinite recursion when filtering or sorting by x509certivicates.value
- [fix] Sorting by name sub-attribute breaks the result list
- [fix] Wrong directory name for translations

  For a detailed description and migration see:
  https://github.com/osiam/server/wiki/Migration#from-12-to-13

## 1.2 - 2014-09-30
- release because of fixes in addon-self-administration
- [feature] Introduced an interface to get the extension definitions (/osiam/extension-definition) 

## 1.1 - 2014-09-19
- [feature] support for mysql as database
- [feature] prevent users from login after N failed attempts
- [feature] revocation of access tokens
  It is now possible to revoke access tokens by using the following service
  endpoints:
  * auth-server/token/revocation
    For revocation of the access token sent as bearer token in the
    Authorization header
  * auth-server/token/revocation/<uuid of user>
    For revocation of all access tokens that were issued to or in behalf of a
    given user. This endpoint is protected.
- [feature] revoke all access tokens of a deactivated/deleted user
- [enhancement] Force UTF-8 encoding of requests and responses
- [enhancement] better error message on search
  When searching for resources and forgetting the surrounding double quotes for
  values, a non-understandable error message was responded. the error message
  was changed to explicitly tell that the error occurred due to missing
  double quotes.
- [enhancement] updated dependencies: Spring 4.1.0, Spring Security 3.2.5,
  Spring Metrics 3.0.2, Jackson 2.4.2, Hibernate 4.3.6, AspectJ 1.8.2,
  Joda Time 2.4, Joda Convert 1.7, Apache Commons Logging 1.2, Guava 18.0,
  Postgres JDBC Driver 9.3-1102-jdbc41

## 1.0 - 2014-05-15
- [refactore] The token validation REST endpoint changed. If you want to validate an token,
  you need to send the token directly in the header instead of putting in the URL.
- [fix] The client secret and the redirect uri is not unique anymore
  For a detailed description and migration see:
  https://github.com/osiam/server/wiki/migration#from-021-to-10

## 0.20 - 2014-04-02
- [refactoring] The auth-server has a new interface with bootstrap, which could be customize.
  For a detailed description and migration see:
  https://github.com/osiam/server/wiki/Migration#from-019-to-020
- [fix] BT-45 - https://jira.osiam.org/browse/BT-45

## 0.19 - 2014-03-17
- [refactoring] The registration module has been moved to his own github repository:
  https://github.com/osiam/addon-self-administration

## 0.18 - 2014-03-04
- [fix] BT-41 - Attribute filtering removes "schemas" attribute
- [feature] OSNG-322 - Placeholder for registration emails
  Added new template engine for emails, see how you could add your own email template files:
  https://github.com/osiam/server/wiki/OSIAM-Registration-Module
  For migration see: https://github.com/osiam/server/wiki/Migration#wiki-from-016-to-017

## 0.17 - 2014-02-17
- [fix] BT-36 - Query with NOT clause on extension fields returns wrong results
- [feature] OSNG-215 - Develop PATCH "Delete" support on extensions
  For migration see: https://github.com/osiam/server/wiki/Migration#wiki-from-016-to-017
- [enhancement] OSNG-323 - Optimize Performance
  For migration see: https://github.com/osiam/server/wiki/Migration#wiki-from-016-to-017
