# Schema Design for Smart Clinic Management System

---

## MySQL Database Design

### Table: patients
- id: INT, Primary Key, AUTO_INCREMENT
- first_name: VARCHAR(50), NOT NULL
- last_name: VARCHAR(50), NOT NULL
- email: VARCHAR(100), NOT NULL, UNIQUE
- phone: VARCHAR(20), NOT NULL
- password_hash: VARCHAR(255), NOT NULL
- date_of_birth: DATE, NULL
- created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP

_Notes:_  
- Emails should be validated via backend logic.  
- If a patient is deleted, consider cascading delete or soft delete to retain appointment history.

---

### Table: doctors
- id: INT, Primary Key, AUTO_INCREMENT
- first_name: VARCHAR(50), NOT NULL
- last_name: VARCHAR(50), NOT NULL
- email: VARCHAR(100), NOT NULL, UNIQUE
- phone: VARCHAR(20), NOT NULL
- specialization: VARCHAR(100), NOT NULL
- password_hash: VARCHAR(255), NOT NULL
- is_active: BOOLEAN DEFAULT TRUE
- created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP

_Notes:_  
- Emails should be unique.  
- When deleting a doctor, cascading delete should carefully handle associated appointments or mark them as canceled.

---

### Table: appointments
- id: INT, Primary Key, AUTO_INCREMENT
- doctor_id: INT, Foreign Key → doctors(id), NOT NULL
- patient_id: INT, Foreign Key → patients(id), NOT NULL
- appointment_time: DATETIME, NOT NULL
- status: INT DEFAULT 0  _(0 = Scheduled, 1 = Completed, 2 = Cancelled)_
- created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP

_Notes:_  
- Doctor should not have overlapping appointments (enforce via application logic).  
- Appointments should be retained even if patients or doctors are deleted (consider soft deletes or status flagging).

---

### Table: admin
- id: INT, Primary Key, AUTO_INCREMENT
- username: VARCHAR(50), NOT NULL, UNIQUE
- email: VARCHAR(100), NOT NULL, UNIQUE
- password_hash: VARCHAR(255), NOT NULL
- created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP

_Notes:_  
- Admins should have strong password policies and multi-factor authentication if possible.

---

### Table: clinic_locations _(Optional Extension)_
- id: INT, Primary Key, AUTO_INCREMENT
- location_name: VARCHAR(100), NOT NULL
- address: TEXT, NOT NULL
- phone: VARCHAR(20), NOT NULL

---

## MongoDB Collection Design

### Collection: prescriptions

```json
{
  "_id": "ObjectId('64abc123456')",
  "patientId": 12,
  "doctorId": 5,
  "appointmentId": 51,
  "patientName": "John Smith",
  "doctorName": "Dr. Alice Tan",
  "medications": [
    {
      "name": "Paracetamol",
      "dosage": "500mg",
      "instructions": "Take 1 tablet every 6 hours."
    },
    {
      "name": "Ibuprofen",
      "dosage": "200mg",
      "instructions": "Take after meals."
    }
  ],
  "doctorNotes": "Monitor temperature daily. Return if symptoms worsen.",
  "refillCount": 2,
  "pharmacy": {
    "name": "Walgreens SF",
    "location": "Market Street"
  },
  "createdAt": "2025-06-24T09:30:00Z"
}
