# FutureTrPr ESG Reporting Platform — Backend REST API

[![Java Version](https://img.shields.io/badge/Java-17-orange.svg?logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.2-brightgreen.svg?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-HTTP%20Basic-green.svg?logo=springsecurity&logoColor=white)](https://spring.io/projects/spring-security)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-Proprietary-blue.svg)](#)

A enterprise-grade Spring Boot REST API for managing corporate **Environmental, Social, and Governance (ESG)** reporting disclosures, verifiable document evidence, carbon emissions calculations, and workflow status auditing.

---

## Repository Links

- **Backend Repository:** [`https://github.com/Deepith-03/esg-reporting-bacend`](https://github.com/Deepith-03/esg-reporting-bacend)
- **Frontend Repository:** [`https://github.com/Deepith-03/esg-reporting-frontend`](https://github.com/Deepith-03/esg-reporting-frontend)
- **Live Production API URL:** `https://your-backend-api-url.com` *(Placeholder)*

---

## Table of Contents

- [Project Overview & Architecture](#project-overview--architecture)
- [Technologies & Dependencies](#technologies--dependencies)
- [Prerequisites](#prerequisites)
- [Database Setup & Configuration](#database-setup--configuration)
- [Database Migrations & Seed Data](#database-migrations--seed-data)
- [Environment Configuration](#environment-configuration)
- [Build, Run & Test Commands](#build-run--test-commands)
- [Authentication, Authorization & Roles](#authentication-authorization--roles)
- [Complete API Endpoint Documentation](#complete-api-endpoint-documentation)
- [Swagger / OpenAPI Instructions](#swagger--openapi-instructions)
- [Carbon Calculation Architecture](#carbon-calculation-architecture)
- [Deployment Instructions](#deployment-instructions)
- [Incomplete Features & Technical Debt (TODOs)](#incomplete-features--technical-debt-todos)

---

## Project Overview & Architecture

The **FutureTrPr ESG Reporting Platform Backend** serves as the system of record for ESG indicators, supporting evidence files, and report governance. 

### Layered Architecture

```
com.esg.esg_reporting_backend/
├── config/                  # Security, CORS, and Jackson configuration
│   ├── CarbonCalculatorConfig.java
│   ├── CorsConfig.java
│   └── SecurityConfig.java
├── controller/              # RESTful API endpoints (@RestController)
│   ├── CarbonCalculatorController.java
│   ├── DataCleanupController.java
│   ├── EvidenceController.java
│   ├── MetricDefinitionController.java
│   ├── MetricEntryController.java
│   ├── OrganizationController.java
│   ├── ReportingPeriodController.java
│   ├── ReportingWorkflowController.java
│   └── WorkflowAuditController.java
├── dto/                     # Request and Response transfer records
│   ├── CleanupImpactDto.java
│   ├── CleanupResultDto.java
│   ├── MetricEntryRequest.java
│   ├── ReportingPeriodRequest.java
│   └── carbon/              # Carbon calculation input and output DTOs
├── entity/                  # JPA / Hibernate database models
│   ├── CarbonCalculationRecord.java
│   ├── Evidence.java
│   ├── MetricDefinition.java
│   ├── MetricEntry.java
│   ├── Organization.java
│   ├── ReportingPeriod.java
│   └── WorkflowAudit.java
├── repo/                    # Spring Data JPA repositories
│   ├── CarbonCalculationRepository.java
│   ├── EvidenceRepository.java
│   ├── MetricDefinitionRepository.java
│   ├── MetricEntryRepository.java
│   ├── OrganizationRepository.java
│   ├── ReportingPeriodRepository.java
│   └── WorkflowAuditRepository.java
└── service/                 # Core business logic and calculations
    ├── DataCleanupService.java
    ├── EsgCalculationService.java
    ├── EvidenceService.java
    ├── MetricEntryService.java
    ├── ReportingPeriodService.java
    ├── ReportingWorkflowService.java
    └── carbon/              # Multi-tier carbon calculation engine
        ├── CarbonCalculationProvider.java
        ├── CarbonCalculatorService.java
        ├── ExternalCarbonCalculatorClient.java
        └── SampleLocalCarbonCalculatorProvider.java
```

---

## Technologies & Dependencies

| Dependency | Group / Artifact | Purpose |
| :--- | :--- | :--- |
| **Java Platform** | OpenJDK 17 | Core runtime language |
| **Spring Framework** | `org.springframework.boot:spring-boot-starter-webmvc` | REST API routing and HTTP controller dispatching |
| **Data Persistence** | `org.springframework.boot:spring-boot-starter-data-jpa` | Spring Data JPA with Hibernate ORM |
| **Security** | `org.springframework.boot:spring-boot-starter-security` | Role-based authorization & HTTP Basic Authentication |
| **Validation** | `org.springframework.boot:spring-boot-starter-validation` | Jakarta Bean Validation (`@Valid`, `@NotBlank`, etc.) |
| **Database Driver** | `com.mysql:mysql-connector-j` | Production MySQL JDBC runtime driver |
| **JSON Serialization** | `com.fasterxml.jackson.core:jackson-databind` | JSON marshaling and unmarshaling |
| **Java 8 Date/Time** | `com.fasterxml.jackson.datatype:jackson-datatype-jsr310` | ISO-8601 `LocalDate` and `LocalDateTime` support |
| **Boilerplate Reducer**| `org.projectlombok:lombok` | Automated getters, setters, and constructors |
| **Testing Starters** | `spring-boot-starter-*-test` | JUnit 5, Mockito, Spring Security Test, Spring MVC Test |

---

## Prerequisites

1. **Java Development Kit (JDK):** Version 17 (Verify via `java -version`).
2. **Apache Maven:** Version 3.8+ (or use the included `./mvnw` / `mvnw.cmd` wrapper).
3. **MySQL Server:** Version 8.0+ running locally on port `3306` (or accessible via network).

---

## Database Setup & Configuration

### 1. Create MySQL Database
Connect to your local MySQL instance (e.g. via MySQL CLI or MySQL Workbench) and execute:

```sql
CREATE DATABASE IF NOT EXISTS esg_reporting
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

### 2. Verify Database Connection
The default configuration in [`src/main/resources/application.properties`](src/main/resources/application.properties) expects:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/esg_reporting
spring.datasource.username=root
spring.datasource.password=root
```
If your local MySQL credentials differ, set environment variables or override the properties in an external configuration.

---

## Database Migrations & Seed Data

### Actual Implementation Details
- **Schema Generation:** The platform currently relies on Hibernate's automatic schema tool:
  ```properties
  spring.jpa.hibernate.ddl-auto=update
  ```
  On application boot, Hibernate scans `@Entity` classes and creates or updates database tables (`organizations`, `reporting_periods`, `metric_definitions`, `metric_entries`, `evidence`, `workflow_audits`, `carbon_calculation_records`).
- **Migration Frameworks:** Neither **Flyway** nor **Liquibase** is currently configured in `pom.xml` (see [Technical Debt / TODOs](#incomplete-features--technical-debt-todos)).
- **Seed Data Runner:** The application does not contain an automated database seeder (`CommandLineRunner`). To bootstrap initial test data, insert records via REST API endpoints or run the starter SQL script below.

### Optional Initial Seed SQL Script
If you wish to pre-populate sample organizations and standard ESG metric definitions, run this in your MySQL console:

```sql
USE esg_reporting;

-- Initial Demo Organization
INSERT INTO organizations (id, name, industry, country)
VALUES (1, 'Acme Global Enterprises', 'Technology & Manufacturing', 'United States')
ON DUPLICATE KEY UPDATE name=name;

-- Sample ESG Metric Definitions
INSERT INTO metric_definitions (id, name, pillar, unit, description) VALUES
(1, 'Scope 1 Greenhouse Gas Emissions', 'ENVIRONMENTAL', 't CO2e', 'Direct GHG emissions from owned or controlled sources'),
(2, 'Total Electricity Consumption', 'ENVIRONMENTAL', 'kWh', 'Annual purchased electricity consumption'),
(3, 'Employee Turnover Rate', 'SOCIAL', '%', 'Percentage of voluntary and involuntary employee departures'),
(4, 'Average Training Hours per Employee', 'SOCIAL', 'Hours', 'Total hours of professional training per employee per year'),
(5, 'Independent Board Member Ratio', 'GOVERNANCE', '%', 'Percentage of independent directors serving on the board')
ON DUPLICATE KEY UPDATE name=name;
```

---

## Environment Configuration

A template configuration file [`.env.example`](.env.example) is provided in the repository root:

```bash
# Server Port (Default in application.properties: 8081)
SERVER_PORT=8081

# MySQL Database Configuration
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/esg_reporting?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=your_mysql_password_here

# Evidence File Storage Location (Default: uploads/evidence)
APP_EVIDENCE_UPLOAD_DIR=uploads/evidence

# Security / Demo Administrator Password Override
# Read by SecurityConfig.java (defaults to LocalDevOnly123! if unset)
ESG_DEMO_PASSWORD=your_admin_password_here

# Carbon Calculator Integration Settings (Read by ExternalCarbonCalculatorClient.java)
# Mode: SAMPLE_LOCAL (default) | EXTERNAL
CARBON_CALCULATOR_PROVIDER=SAMPLE_LOCAL
CARBON_CALCULATOR_API_URL=https://api.external-carbon-service.com/v1
CARBON_CALCULATOR_API_KEY=your_external_carbon_api_key_here
```

---

## Build, Run & Test Commands

### 1. Clone the Repository
```bash
git clone https://github.com/Deepith-03/esg-reporting-bacend.git
cd esg-reporting-bacend
```

### 2. Compile the Project
```bash
# On Linux / macOS
./mvnw clean compile

# On Windows (PowerShell / CMD)
.\mvnw.cmd clean compile
```

### 3. Run All Tests
```bash
# On Linux / macOS
./mvnw test

# On Windows
.\mvnw.cmd test
```
*Current test suite verifies Spring application context loading, Carbon Calculator security rules, calculation services, and ESG formula calculations.*

### 4. Run Locally with Spring Boot
```bash
# On Linux / macOS
./mvnw spring-boot:run

# On Windows
.\mvnw.cmd spring-boot:run
```
The server will initialize and listen on:
```
http://localhost:8081
```

### 5. Package into Executable JAR
```bash
# On Linux / macOS
./mvnw clean package -DskipTests=false

# On Windows
.\mvnw.cmd clean package -DskipTests=false
```
Run the packaged JAR:
```bash
java -jar target/esg-reporting-backend-0.0.1-SNAPSHOT.jar
```

---

## Authentication, Authorization & Roles

Authentication is implemented using **Spring Security** with **HTTP Basic Authentication** and an **`InMemoryUserDetailsManager`**. CSRF protection is disabled (`csrf.disable()`) for stateless REST API usage. CORS explicitly permits `http://localhost:5173`.

### Default Demo Accounts

| Username | Password | Role | Description |
| :--- | :--- | :--- | :--- |
| `admin` | `LocalDevOnly123!` *(or `$ESG_DEMO_PASSWORD`)* | `ROLE_ADMIN` | System Administrator with full access to all endpoints, cleanup tools, and configuration. |
| `dataentry` | `DataEntry123!` | `ROLE_DATA_ENTRY` | Contributor responsible for creating periods, inputting metric values, uploading evidence, and submitting reports. |
| `reviewer` | `Reviewer123!` | `ROLE_REVIEWER` | Compliance Reviewer responsible for auditing submissions, approving reports, and requesting revisions with comments. |
| `viewer` | `Viewer123!` | `ROLE_VIEWER` | Auditor / Executive Viewer with read-only access to published reports, metrics, calculations, and audit history. |

### Role Permissions Matrix

| Endpoint Pattern | Method | Allowed Roles |
| :--- | :--- | :--- |
| `/api/admin/**` | ANY | `ADMIN` only |
| `/api/organizations` | `POST` | `ADMIN` only |
| `/api/organizations` | `GET` | Authenticated (`ADMIN`, `DATA_ENTRY`, `REVIEWER`, `VIEWER`) |
| `/api/organizations/{id}/periods` | `POST` | `ADMIN`, `DATA_ENTRY` |
| `/api/organizations/{id}/periods` | `GET` | Authenticated |
| `/api/metric-definitions` | `POST` | `ADMIN` only |
| `/api/metric-definitions/**` | `GET` | Authenticated |
| `/api/periods/{id}/entries` | `POST` | `ADMIN`, `DATA_ENTRY` |
| `/api/periods/{id}/entries` | `GET` | Authenticated |
| `/api/metric-entries/{id}/evidence` | `POST` | `ADMIN`, `DATA_ENTRY` |
| `/api/metric-entries/{id}/evidence` | `GET` | Authenticated |
| `/api/reporting-periods/{id}/submit` | `POST` | `ADMIN`, `DATA_ENTRY` |
| `/api/reporting-periods/{id}/resubmit`| `POST` | `ADMIN`, `DATA_ENTRY` |
| `/api/reporting-periods/{id}/request-changes` | `POST` | `ADMIN`, `REVIEWER` |
| `/api/reporting-periods/{id}/approve` | `POST` | `ADMIN`, `REVIEWER` |
| `/api/carbon-calculator/save` | `POST` | `ADMIN`, `DATA_ENTRY` |
| `/api/carbon-calculator/history/{id}` | `DELETE` | `ADMIN` only |
| `/api/carbon-calculator/**` | `GET`, `POST` | Authenticated |

---

## Complete API Endpoint Documentation

All endpoints require HTTP Basic Authentication unless specified otherwise.

### 1. System Health Check
- `GET /hi` — Public demonstration greeting endpoint.

### 2. Organizations (`/api/organizations`)
- `POST /api/organizations` — Register a new organization (`ADMIN` only).
  - Body: `{"name": "string", "industry": "string", "country": "string"}`
- `GET /api/organizations` — List all registered organizations.

### 3. Reporting Periods (`/api/organizations/{organizationId}/periods`)
- `POST /api/organizations/{organizationId}/periods` — Create a reporting period (`ADMIN`, `DATA_ENTRY`).
  - Body: `{"name": "Q1 2026", "startDate": "2026-01-01", "endDate": "2026-03-31", "reportingBoundary": "Global Operations"}`
- `GET /api/organizations/{organizationId}/periods` — List reporting periods for the specified organization.

### 4. ESG Metric Definitions (`/api/metric-definitions`)
- `POST /api/metric-definitions` — Register a new metric definition (`ADMIN` only).
  - Body: `{"name": "Water Recycled", "pillar": "ENVIRONMENTAL", "unit": "m3", "description": "Total water recycled"}`
  - *Allowed pillars:* `ENVIRONMENTAL`, `SOCIAL`, `GOVERNANCE`.
- `GET /api/metric-definitions` — List all definitions.
  - Optional Query Parameter: `?pillar=ENVIRONMENTAL` (or `SOCIAL`, `GOVERNANCE`).

### 5. Metric Entries (`/api/periods/{periodId}/entries`)
- `POST /api/periods/{periodId}/entries` — Record a metric value (`ADMIN`, `DATA_ENTRY`).
  - Body: `{"metricDefinitionId": 1, "value": "1250.50"}`
- `GET /api/periods/{periodId}/entries` — List all metric entries for a period.

### 6. Evidence Management (`/api/metric-entries/{metricEntryId}/evidence`)
- `POST /api/metric-entries/{metricEntryId}/evidence` — Upload document (`ADMIN`, `DATA_ENTRY`).
  - Content-Type: `multipart/form-data`
  - Form Fields: `file` (Multipart file up to 10MB), `description` (optional string).
- `GET /api/metric-entries/{metricEntryId}/evidence` — List evidence metadata for an entry.

### 7. Reporting Workflow & Approvals (`/api/reporting-periods/{periodId}/...`)
- `POST /api/reporting-periods/{periodId}/submit` — Transition `DRAFT` &rarr; `SUBMITTED`.
- `POST /api/reporting-periods/{periodId}/request-changes` — Transition `SUBMITTED` &rarr; `CHANGES_REQUESTED`.
  - Body (required): `{"comment": "Please attach energy bills for March"}`
- `POST /api/reporting-periods/{periodId}/approve` — Transition `SUBMITTED` &rarr; `APPROVED`.
  - Body (optional): `{"comment": "Verified and approved"}`
- `POST /api/reporting-periods/{periodId}/resubmit` — Transition `CHANGES_REQUESTED` &rarr; `SUBMITTED`.
  - Body (optional): `{"comment": "Corrected figures attached"}`

### 8. Workflow Audit Trail (`/api/reporting-periods/{periodId}/audit`)
- `GET /api/reporting-periods/{periodId}/audit` — Returns chronological status history with timestamps, actors, and comments.

### 9. Carbon Calculator (`/api/carbon-calculator/...`)
- `GET /api/carbon-calculator/factors` — Returns catalogue of reference emission factors.
- `GET /api/carbon-calculator/provider-status` — Returns active provider details (`SAMPLE_LOCAL` vs `EXTERNAL`).
- `POST /api/carbon-calculator/calculate` — Performs real-time GHG calculation.
  - Body:
    ```json
    {
      "electricityKwh": 12500,
      "fuelEntries": [
        {"fuelType": "DIESEL", "quantity": 450, "unit": "LITERS"}
      ],
      "transportEntries": [
        {"transportType": "PASSENGER_CAR_PETROL", "distance": 1850, "unit": "KM"}
      ]
    }
    ```
- `POST /api/carbon-calculator/save` — Persists calculation to MySQL database (`ADMIN`, `DATA_ENTRY`).
- `GET /api/carbon-calculator/history` — Query past calculations.
  - Optional Query Parameters: `?organizationId=1&reportingPeriodId=2`
- `GET /api/carbon-calculator/history/{id}` — Retrieve specific calculation record.
- `DELETE /api/carbon-calculator/history/{id}` — Delete calculation record (`ADMIN` only).

### 10. Admin Data Cleanup & Management (`/api/admin/cleanup/...`)
- `GET /api/admin/cleanup/preview/organizations/{id}` — Preview impact of deleting an organization.
- `GET /api/admin/cleanup/preview/periods/{id}` — Preview impact of deleting a period.
- `GET /api/admin/cleanup/preview/metric-entries/{id}` — Preview impact of deleting an entry.
- `GET /api/admin/cleanup/preview/evidence/{id}` — Preview impact of deleting an evidence file.
- `GET /api/admin/cleanup/preview/demo-data` — Preview batch demo data cleanup impact.
- `DELETE /api/admin/cleanup/organizations/{id}?allowApprovedOverride=bool` — Delete organization.
- `DELETE /api/admin/cleanup/periods/{id}?allowApprovedOverride=bool` — Delete period.
- `DELETE /api/admin/cleanup/metric-entries/{id}?allowApprovedOverride=bool` — Delete entry.
- `DELETE /api/admin/cleanup/evidence/{id}?allowApprovedOverride=bool` — Delete evidence file.
- `POST /api/admin/cleanup/clear-demo-data` — Purge demo/test organizations and data.

---

## Swagger / OpenAPI Instructions

> [!NOTE]
> **Current Status: NOT IMPLEMENTED (TODO)**
>
> Swagger / OpenAPI (`springdoc-openapi`) is **not currently included** in `pom.xml`. To add interactive Swagger UI to the project in the future, follow these steps:

1. **Add the dependency to `pom.xml`:**
   ```xml
   <dependency>
       <groupId>org.springdoc</groupId>
       <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
       <version>2.5.0</version>
   </dependency>
   ```

2. **Allow Swagger paths in `SecurityConfig.java`:**
   Add permitAll rules before authenticated matcher:
   ```java
   .requestMatchers(
       "/v3/api-docs/**",
       "/swagger-ui/**",
       "/swagger-ui.html"
   ).permitAll()
   ```

3. **Access the interactive documentation:**
   Once configured, the Swagger documentation will be accessible at:
   ```
   http://localhost:8081/swagger-ui/index.html
   ```

---

## Carbon Calculation Architecture

The platform supports a modular, pluggable carbon calculation engine:

1. **`SampleLocalCarbonCalculatorProvider` (Active Default):**
   - In-memory calculation provider utilizing reference emission factors from **UK DESNZ (Department for Energy Security and Net Zero) 2023** and **US EPA eGRID indicative reference averages**.
   - Calculates Scope 1, Scope 2, Scope 3 totals, and percentage allocations using `BigDecimal` arithmetic with `RoundingMode.HALF_UP`.
2. **`ExternalCarbonCalculatorClient` (Pluggable Adapter):**
   - Configured via environment variables (`CARBON_CALCULATOR_API_KEY`, `CARBON_CALCULATOR_API_URL`, `CARBON_CALCULATOR_PROVIDER=EXTERNAL`).
   - Prepared to connect to third-party commercial APIs (e.g. Climatiq, Carbon Interface) without modifying application code or UI contracts.

---

## Deployment Instructions

### Production Deployment via Docker

Create a `Dockerfile` in `esg-reporting-backend/`:
```dockerfile
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/esg-reporting-backend-*.jar app.jar
RUN mkdir -p uploads/evidence
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Run container with environment variables:
```bash
docker build -t esg-reporting-backend .
docker run -p 8081:8081 \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://your-prod-db-host:3306/esg_reporting" \
  -e SPRING_DATASOURCE_USERNAME="db_user" \
  -e SPRING_DATASOURCE_PASSWORD="secure_password" \
  -e ESG_DEMO_PASSWORD="your_admin_password" \
  esg-reporting-backend
```

---

## Incomplete Features & Technical Debt (TODOs)

- [ ] **TODO: Database Migrations (Flyway / Liquibase):** Transition from Hibernate's `ddl-auto=update` to Flyway or Liquibase versioned migration scripts (`V1__init.sql`) for audit-proof database schemas.
- [ ] **TODO: Database-Backed User Entity & Authentication:** Replace in-memory credentials with a database-backed `users` and `roles` schema with BCrypt password hashing and JWT token issuance.
- [ ] **TODO: Swagger / Springdoc OpenAPI Integration:** Add `springdoc-openapi-starter-webmvc-ui` to `pom.xml` and expose interactive API explorer.
- [ ] **TODO: Evidence File Download & Virus Scanning:** Implement `GET /api/evidence/{id}/download` streaming endpoint to allow file downloading and integrate ClamAV or S3 bucket scanning.
- [ ] **TODO: Expanded Integration Test Coverage:** Write MockMvc integration tests for `OrganizationController`, `ReportingPeriodController`, `EvidenceController`, and `DataCleanupController`.
- [ ] **TODO: Dynamic CORS Configuration:** Read permitted CORS origins from `application.properties` or environment variables instead of hardcoded `http://localhost:5173`.
