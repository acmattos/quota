# User´s Quota REST API Microservice

User´s Quota is a microservice application, built on top of Java 17, Spring 
Boot, and MySQL database.

## Specification
As a BE developer, your task is to improve the efficiency of our Web API usage 
by preventing abuse caused by excessive requests from multiple users (quota).

To accomplish this, implement a robust access-limiting mechanism that ensures 
optimal performance and resource utilization.

The new API should accept up to X API requests per user (for instance, 5 
requests per user) - Beyond the quota of X requests, the user should be blocked.

Unfortunately, our users reside in two different sources: one in Elastic and 
one in MySQL.

During the day (9:00 - 17:00 UTC), we use MySQL as a source, During the night 
(17:01 - 8:59), we use Elastic.

Create a new Spring Boot application that can be run and tested. In your coding, 
consider scalability, extensibility, and design patterns.You should implement 
one database (as localhost), and the other implementation can be printing 
functions only.

Just pointing out:

Please, do not use third-party libraries for the quota mechanism (spring boot 
starter etc.).

No authentication is required for the server app service

Assume single instance of your server app service

You don’t need to take care of data in database. for your testing you can seed 
some users.

If you want to expand the User model - do it - add fields

time\recycle is not something you should care about. while the user reached his 
quota - he is locked

API functions:

CRUD functions for the user model: (should not be monitored as quota)
```Text
createUser(User user);
getUser(String userId);
updateUser(String userId, User updatedUser);
deleteUser(String userId);
```

This function is the main function used for access and counting the users’ 
quotas.
```Text
consumeQuota(@PathVariable String userId);
```

This function returns all users and their quota statuses.
```Text
getUsersQuota();
```

User model:
```Java
public class User { 
    private String id; 
    private String firstName; 
    private String lastName; 
    private LocalDateTime lastLoginTimeUtc; 
}
```

## Development Environment
### Tools
- GIT
- Java 17+
- Maven 3
- MySql 5+

### Libraries
- Srping Boot 3
- Swagger 3
- OpenAPI 3
- JUnit 5
- Mockito 5

## Getting Started
1.  Ensure that GIT, Java 17+, Maven 3, and MySql 5+ are properly installed.
2. Open a command console and change the directory to one of your choice.
3. Type: `git clone https://github.com/acmattos/quota.git`
4. After cloning the repository, change to `quota` directory
5. Build a version of the application: `mvn clean compile package`
6. You can run the application usind: `mvn springboot:run`
7. Open your browser and access: `http://localhost:8080/swagger-ui/index.html`
8. You are ready to test the application.

## Notes
1. You can change the quota modifying the value of `max.requests.per.user`
2. Don´t forget to configure the database user and password accordingly:
   `spring.datasource.username` and  `spring.datasource.password`
3. The database/elasticsearch control is defined in the 
   `UserPersistenceServiceImpl#mustUseDbRepository()` method