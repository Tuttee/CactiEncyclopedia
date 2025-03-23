#Getting started with Cacti Encyclopedia

1. Enter credentials for database use one of the following options:
    a. edit the application.properties file with your credentials
    b. use MYSQL_USER and MYSQL_PASS environment variables to enter credentials
2. Initially start the app without importing data.sql, so that the DB can be created
3. After first start, import the data.sql. To import, add "spring.sql.init.mode=always" to the application.properties file.
4. After import, change "always" to "never".
5. The first registered user has an ADMIN role, every following user has USER role.