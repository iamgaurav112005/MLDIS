# MLDIS – MediLab and Drug Store Information System

A Java 17 + Spring Boot + Spring Data JPA hospital management application with a responsive browser frontend and relational database.

## Modules included
- Login and staff accounts (demo role labels)
- Administration: staff accounts and departments
- Patient registration with generated patient IDs
- Laboratory, radiology and pathology investigation orders and result entry
- Patient history/notes field
- Medicine stock, stock adjustment and low-stock indicators
- OPD, IPD and Emergency billing with discount and tax calculation

## Technology
- Backend: Java 17, Spring Boot 3, REST API
- Frontend: HTML, CSS, vanilla JavaScript
- Database: H2 file database by default; MySQL profile included
- ORM: Spring Data JPA / Hibernate

## Run quickly (Ubuntu / Linux)
Install Java and Maven:
```bash
sudo apt update
sudo apt install -y openjdk-17-jdk maven
java -version
mvn -version
```
Extract the ZIP, enter the project directory, then:
```bash
mvn spring-boot:run
```
Open http://localhost:8080

Default demo logins:
| Role | Username | Password |
|---|---|---|
| Administrator | admin | admin123 |
| Registration | reception | desk123 |
| Doctor | doctor | doctor123 |
| Lab technician | lab | lab123 |
| Dispensary | pharmacy | pharmacy123 |\n| Demo patient portal | patient | patient123 |

The default H2 database is stored in `./data/mldisdb`. H2 console: http://localhost:8080/h2-console
JDBC URL: `jdbc:h2:file:./data/mldisdb`, user `sa`, blank password.

## Run with MySQL
1. Install/start MySQL and create a database/user, or use the included Docker Compose file:
```bash
docker compose up -d
```
2. Start the app with:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```
MySQL defaults: database `mldis`, user `mldis`, password `mldis123`. Override with `DB_URL`, `DB_USER`, and `DB_PASSWORD` environment variables.

## Build executable JAR
```bash
mvn clean package
java -jar target/mldis-hospital-1.0.0.jar
```

## Important prototype limitations
This is an educational project starter, not a production-ready clinical system. The sample login uses plain-text demo passwords and a simple session. Before real deployment, implement password hashing, strict role-based authorization for every operation, CSRF protection, audit logs, backups, HTTPS, data retention/privacy controls, validation, and clinical workflow review. Do not enter real patient information into this demo.

## Role-based dashboards
After sign-in, the application displays navigation and dashboard content according to the account role:
- ADMIN: full administration dashboard and all modules
- REGISTRATION: receptionist dashboard, patient registration and billing
- DOCTOR: doctor dashboard, patient records and investigations
- TECHNICIAN: laboratory queue and result entry
- PHARMACY: medicine inventory and stock adjustment
- PATIENT: personal profile, investigation status/results and bills

The seeded demo patient is `PT-DEMO-001` (Demo Patient). The patient account is `patient` / `patient123`.
To create another patient portal login, first register the patient, then use Admin > Staff Accounts to create a PATIENT account and enter that patient's exact Patient ID in the Linked Patient ID field.
