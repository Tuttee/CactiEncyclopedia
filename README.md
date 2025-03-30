#Getting started with Cacti Encyclopedia

1. Start apache/kafka:3.9.0 as a docker container.
2. In application-dev.properties configure spring.kafka.bootstrap-servers with your kafka address.
3. Enter credentials for database use one of the following options:
    a. edit the application-dev.properties file with your credentials
    b. use MYSQL_USER and MYSQL_PASS environment variables to enter credentials
4. Make sure the active profile you use is dev.
5. Initially start the app without importing data.sql, so that the DB can be created
6. After first start, import the data.sql. To import, add "spring.sql.init.mode=always" to the application.properties file.
7. After import, change "always" to "never".
8. The first registered user has an ADMIN role, every following user has USER role.
9. Additionally, set up the Facts service URL in the "facts-svc.base-url" property in the application.properties file.