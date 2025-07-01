# Smart Clinic Management System – Architecture Design

## 1. Architecture Summary
The Smart Clinic Management System is designed as a **three-tier web application** consisting of the Presentation, Application, and Data layers.

The **Presentation Layer** consists of:
- Admin and Doctor dashboards using **Thymeleaf** templates (server-rendered views).
- Patient dashboard and appointments modules using **REST APIs** (JSON-based communication).

The **Application Layer** is powered by a **Spring Boot backend** that:
- Routes requests using Thymeleaf and REST controllers.
- Handles business logic in a centralized **Service Layer**.
- Applies data persistence through **MySQL** for structured data and **MongoDB** for flexible, document-based data.

The **Data Layer** includes:
- **MySQL Database** for storing Patients, Doctors, Appointments, and Admin entities.
- **MongoDB Database** for storing Prescriptions as documents.

This architecture improves scalability, maintainability, and allows for future API clients (like mobile apps) to easily integrate with the system. By using Spring Boot, we ensure seamless integration with CI/CD pipelines and containerized deployments via Docker.

---

## 2. Numbered Flow of Data and Control

1. **User Accesses the System:**
   - Users (Admin, Doctor, Patient) interact with the system via:
     - Thymeleaf-based dashboards: `AdminDashboard`, `DoctorDashboard`
     - REST-based modules: `Appointments`, `PatientDashboard`, `PatientRecord`

2. **Request Routed to Controllers:**
   - Requests from dashboards are routed to **Thymeleaf Controllers**.
   - Requests from REST modules are routed to **REST Controllers** (via JSON API).

3. **Controllers Delegate to Service Layer:**
   - Controllers call the **Service Layer** to process business logic, validation, and workflows.

4. **Service Layer Accesses Repositories:**
   - For structured data, the Service Layer calls **MySQL Repositories** (via Spring Data JPA).
   - For unstructured data, the Service Layer calls **MongoDB Repository** (via Spring Data MongoDB).

5. **Database Operations:**
   - MySQL Repositories interact with the **MySQL Database** to handle entities like Patient, Doctor, Appointment, and Admin.
   - MongoDB Repository interacts with the **MongoDB Database** to handle Prescription documents.

6. **Model Binding:**
   - Data retrieved from the MySQL database is mapped to **JPA Entities**.
   - Data retrieved from the MongoDB database is mapped to **MongoDB Document Models**.

7. **Response Sent to Frontend:**
   - For Thymeleaf requests: Model data is passed to Thymeleaf templates and rendered as HTML.
   - For REST API requests: Model data is serialized into JSON and returned to the client.

---
