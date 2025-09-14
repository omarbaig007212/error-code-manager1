# Error Code Manager

A Spring Boot application for managing product error codes, versions, and products with a modern Bootstrap frontend and REST API.  
Supports OAuth2/SSO integration, scalable deployment, and advanced search/filtering.

---

## Features

- Manage Products, Versions, and Error Codes
- RESTful API with Swagger UI documentation
- Responsive Bootstrap frontend
- Advanced search and filtering
- Pagination and sticky table headers
- OAuth2/SSO support (Azure AD, Okta, Google, GitHub, etc.)
- Ready for Docker and AWS deployment

---

## Prerequisites

- Java 17+
- Maven 3.6+
- Node.js (optional, for frontend development)
- Docker (optional, for containerization)
- PostgreSQL or AWS RDS (for production)
- Organization SSO/OAuth2 credentials (for SSO)

---

## Quick Start: Commands & Steps to Run

1. **Clone the repository**
    ```bash
    git clone <repo-url>
    cd error-code-manager
    ```

2. **Configure the database (optional for production)**
    - Edit `src/main/resources/application.properties` for your DB settings.

3. **(Optional) Configure SSO/OAuth2**
    - Add your SSO/OAuth2 credentials to `application.properties` or as environment variables.

4. **Build the application**
    ```bash
    ./mvnw clean package
    ```

5. **Run the application**
    ```bash
    java -jar target/error-code-manager-0.0.1-SNAPSHOT.jar
    ```

6. **Access the application**
    - Main UI: [http://localhost:8080](http://localhost:8080)
    - Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
    - H2 Console (dev): [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

---

**Docker Run (alternative):**
```bash
docker build -t error-code-manager .
docker run -d -p 8080:8080 --name error-code-app error-code-manager
```

---

## Running Locally

### 1. Clone the Repository

```bash
git clone <repo-url>
cd error-code-manager
```

### 2. Configure the Database

By default, the app uses H2 in-memory DB for development.  
For production, configure PostgreSQL or AWS RDS in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://<host>:<port>/<db>
spring.datasource.username=<username>
spring.datasource.password=<password>
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update
```

### 3. (Optional) Configure OAuth2/SSO

Add your SSO/OAuth2 credentials to `application.properties` or as environment variables:

```properties
# Example for Azure AD
spring.security.oauth2.client.registration.azure.client-id=YOUR_CLIENT_ID
spring.security.oauth2.client.registration.azure.client-secret=YOUR_CLIENT_SECRET
spring.security.oauth2.client.registration.azure.scope=openid,profile,email
spring.security.oauth2.client.registration.azure.redirect-uri={baseUrl}/login/oauth2/code/azure
spring.security.oauth2.client.provider.azure.issuer-uri=https://login.microsoftonline.com/{tenant-id}/v2.0
```

### 4. Build the Application

```bash
./mvnw clean package
```

### 5. Run the Application

```bash
java -jar target/error-code-manager-0.0.1-SNAPSHOT.jar
```

The app will be available at [http://localhost:8080](http://localhost:8080)

---

## Running with Docker

1. Build the Docker image:

    ```bash
    docker build -t error-code-manager .
    ```

2. Run the container:

    ```bash
    docker run -d -p 8080:8080 --name error-code-app error-code-manager
    ```

---

## API Documentation

- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- H2 Console (dev): [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

---

## SSO Integration

- Register your app with your organization's SSO provider (Azure AD, Okta, Google, etc.)
- Set redirect URI to: `http://localhost:8080/login/oauth2/code/<provider>`
- Add client ID/secret to `application.properties` or as environment variables
- On login, users will be redirected to your SSO provider

---

## AWS & Production Deployment

- Use PostgreSQL or AWS RDS for database
- Use AWS ECS/EKS or EC2 with Docker for scalable deployment
- Configure environment variables for DB and SSO credentials
- Use AWS ALB/NLB for load balancing and auto-scaling

---

## Development

- Frontend: Edit files in `src/main/resources/static/`
- Backend: Standard Spring Boot structure in `src/main/java/com/example/error_code_manager/`
- Run tests: `./mvnw test`

---

## License

[Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)

---

## Support

For issues, open a GitHub issue or contact your organization admin.