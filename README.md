# E-Commerce REST API

Full-featured e-commerce backend built with Spring Boot, JWT authentication, and MySQL database. Implements complete CRUD operations for products, orders, and user management.

## Technologies
- Spring Boot
- Spring Security
- Spring Data JPA
- MySQL
- JWT
- Maven

## Key Features
- JWT Authentication
- Role-based Authorization
- Payment Integration
- Order Management

## Getting Started
1. Clone the repository
2. Configure database settings in `src/main/resources/application.properties` or `application.yml`
3. Build and run the application using Maven:
   ```bash
   ./mvnw spring-boot:run
   ```
4. Access the API documentation via Swagger UI (if enabled)

## Quick Start

### Prerequisites
- Node.js 18+
- Docker and Docker Compose
- npm

### Setup and Run

1. **Automated Setup**:
   ```bash
   chmod +x scripts/setup.sh
   ./scripts/setup.sh
   ```

2. **Manual Setup**:
   ```bash
   # Install dependencies
   npm install
   
   # Start services
   npm run docker:up
   
   # Start development server
   npm run dev
   ```

### Available Scripts

- `npm run setup` - Install dependencies and start services
- `npm run dev` - Start development server with hot reload
- `npm run docker:up` - Start database and services
- `npm run docker:down` - Stop all services
- `npm run docker:logs` - View service logs

### Service URLs

- **API**: http://localhost:3000
- **Database**: localhost:5432 (postgres/password)

## Folder Structure
- `src/main/java/com/JDMGod/accounts_service/` - Main source code
- `src/main/resources/` - Configuration files and migrations
- `docker/compose.yml` - Docker Compose setup

## License
This project is licensed under the MIT License.
