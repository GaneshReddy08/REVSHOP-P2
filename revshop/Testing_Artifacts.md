# Testing Artifacts & Logging Summary

## 1. Unit Testing Context (JUnit 5 + Mockito)
Core classes testing business logic and API routing were implemented with Mockito abstractions over dependencies. Tested properties include:
- `UserServiceImplTest.java`: Complete mock validation on user registration constraints and login credentials checking.
- `UserControllerTest.java`: Endpoint verification for returning correct mapped HTTP statuses and user DTO bodies.
- `ProductControllerTest.java`: Mocks service-level product fetching. Validates inventory read functionality.
- `CartControllerTest.java`: Mocks cart assignment for testing the user's localized session cart endpoints.

*Note: You can run these tests using your preferred Java IDE (VSCode, IntelliJ, Eclipse) or via CLI `mvn test`.*

## 2. Logger Trimming Details (Log4j2)
Log4j2 was injected, overriding Spring Boot's default Logback.
- **`log4j2-spring.xml`**: Defined appenders to standard output (system stdout) and to file logic scaling at `logs/revshop.log`.
- Log statements are active in `UserServiceImpl` capturing both `DEBUG` traces before executing complex validations, and `ERROR` alerts if validation yields a failure. `ProductController` actively notes API requests hitting endpoint mutations, helping diagnose runtime issues.
