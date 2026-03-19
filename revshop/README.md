# RevShop E-Commerce Application

RevShop is a full-stack monolithic e-commerce web application catering to both Buyers and Sellers. It is designed to provide comprehensive management for products, shopping carts, checkout, orders, and authentication.

## Technologies Used
- **Backend**: Java 17, Spring Boot 3, Spring Data JPA, Spring Security
- **Frontend**: HTML5, Bootstrap 5, Thymeleaf, JavaScript
- **Database**: Oracle SQL
- **Testing & Quality**: JUnit 5, Mockito
- **Logging**: Log4j2 (custom configured `log4j2-spring.xml` with console and file rolling appenders)
- **Build Tool**: Maven

## Architecture Highlights
- **Layered Architecture**: Controllers, Services, Repositories, Entities, and DTOs.
- **Log4j2 Injection**: Important lifecycle actions in the controllers and core business logic in the services are comprehensively tracked via Log4j2.
- **Robust Unit Testing**: Core functionalities are tested thoroughly using Mockito test injections.

## Running the Application
Ensure Oracle DB provides your data sources listed in your `application.properties`. Run:
```bash
mvn clean install
mvn spring-boot:run
```
