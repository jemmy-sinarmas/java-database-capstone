# User Stories
---

# Admin User Stories

## User Story 1
**Title:**  
_As an admin, I want to log into the portal with my username and password, so that I can manage the platform securely._

**Acceptance Criteria:**
1. The admin can enter valid credentials to access the portal.
2. Invalid login attempts are rejected with proper error messages.
3. The admin is redirected to the dashboard upon successful login.

**Priority:** High  
**Story Points:** 3  

**Notes:**
- Ensure secure authentication (consider hashing passwords).

---

## User Story 2
**Title:**  
_As an admin, I want to log out of the portal, so that I can protect system access._

**Acceptance Criteria:**
1. The admin can click a logout button.
2. The session is terminated upon logout.
3. The admin is redirected to the login page after logout.

**Priority:** High  
**Story Points:** 2  

**Notes:**
- Session timeout should also trigger logout.

---

## User Story 3
**Title:**  
_As an admin, I want to add doctors to the portal, so that I can onboard new medical staff._

**Acceptance Criteria:**
1. The admin can enter and submit new doctor details.
2. The system validates all required fields.
3. The doctor profile is visible to patients after addition.

**Priority:** High  
**Story Points:** 5  

**Notes:**
- Ensure email uniqueness and role assignment.

---

## User Story 4
**Title:**  
_As an admin, I want to delete a doctor's profile from the portal, so that I can manage active users._

**Acceptance Criteria:**
1. The admin can view the list of doctors.
2. The admin can select and delete a doctor's profile.
3. Deleted doctors are removed from the patient view.

**Priority:** Medium  
**Story Points:** 4  

**Notes:**
- Deletion should trigger appointment cancellation logic.

---

## User Story 5
**Title:**  
_As an admin, I want to run a stored procedure in MySQL CLI to get the number of appointments per month, so that I can track usage statistics._

**Acceptance Criteria:**
1. The stored procedure can be called from the MySQL CLI.
2. The procedure correctly calculates monthly appointment counts.
3. The system can export the results if needed.

**Priority:** Low  
**Story Points:** 3  

**Notes:**
- Optional integration with a dashboard in the future.

---

# Patient User Stories

## User Story 1
**Title:**  
_As a patient, I want to view a list of doctors without logging in, so that I can explore options before registering._

**Acceptance Criteria:**
1. The doctor list is publicly accessible.
2. Patients can view specialization and contact information.
3. Only active doctors are displayed.

**Priority:** High  
**Story Points:** 3  

**Notes:**
- Support filtering by specialization.

---

## User Story 2
**Title:**  
_As a patient, I want to sign up using my email and password, so that I can book appointments._

**Acceptance Criteria:**
1. The patient can register with a valid email and password.
2. The system sends a confirmation or welcome message.
3. Duplicate email registrations are rejected.

**Priority:** High  
**Story Points:** 4  

**Notes:**
- Consider email verification in the future.

---

## User Story 3
**Title:**  
_As a patient, I want to log into the portal, so that I can manage my bookings._

**Acceptance Criteria:**
1. The patient can log in using registered credentials.
2. Failed login attempts are properly handled.
3. Login redirects to the patient dashboard.

**Priority:** High  
**Story Points:** 3  

**Notes:**
- Should implement session management.

---

## User Story 4
**Title:**  
_As a patient, I want to log out of the portal, so that I can secure my account._

**Acceptance Criteria:**
1. The patient can click a logout button.
2. The session is terminated upon logout.
3. The system redirects to the homepage.

**Priority:** High  
**Story Points:** 2  

**Notes:**
- Session timeout should log out inactive users.

---

## User Story 5
**Title:**  
_As a patient, I want to book an hour-long appointment, so that I can consult with a doctor._

**Acceptance Criteria:**
1. The patient can select an available slot.
2. The system confirms and saves the appointment.
3. The booked slot becomes unavailable for others.

**Priority:** High  
**Story Points:** 5  

**Notes:**
- Validate doctor availability.

---

## User Story 6
**Title:**  
_As a patient, I want to view my upcoming appointments, so that I can prepare accordingly._

**Acceptance Criteria:**
1. Patients can view a list of their future appointments.
2. Details include date, time, doctor name, and status.
3. Canceled or past appointments are not shown.

**Priority:** Medium  
**Story Points:** 3  

**Notes:**
- Consider adding reminder notifications later.

---

# Doctor User Stories

## User Story 1
**Title:**  
_As a doctor, I want to log into the portal, so that I can manage my appointments._

**Acceptance Criteria:**
1. The doctor can log in with valid credentials.
2. Invalid logins show an error message.
3. The doctor is redirected to their dashboard after login.

**Priority:** High  
**Story Points:** 3  

**Notes:**
- Secure authentication is required.

---

## User Story 2
**Title:**  
_As a doctor, I want to log out of the portal, so that I can protect my data._

**Acceptance Criteria:**
1. The doctor can click a logout button.
2. The session is terminated and the doctor is redirected.
3. Inactive sessions expire automatically.

**Priority:** High  
**Story Points:** 2  

**Notes:**
- Follow best practices for session handling.

---

## User Story 3
**Title:**  
_As a doctor, I want to view my appointment calendar, so that I can stay organized._

**Acceptance Criteria:**
1. The calendar displays all upcoming appointments.
2. Appointments are shown with patient names and times.
3. The doctor can filter by day, week, or month.

**Priority:** High  
**Story Points:** 5  

**Notes:**
- Integration with external calendars is optional for later.

---

## User Story 4
**Title:**  
_As a doctor, I want to mark my unavailability, so that patients only see available slots._

**Acceptance Criteria:**
1. The doctor can select and block unavailable time slots.
2. Blocked slots are not shown to patients.
3. Patients with existing appointments in blocked slots are notified.

**Priority:** High  
**Story Points:** 5  

**Notes:**
- Should handle conflicts gracefully.

---

## User Story 5
**Title:**  
_As a doctor, I want to update my profile with specialization and contact information, so that patients have up-to-date information._

**Acceptance Criteria:**
1. The doctor can update their name, specialization, and contact details.
2. Changes are immediately reflected in the doctor directory.
3. Profile updates are validated before saving.

**Priority:** Medium  
**Story Points:** 4  

**Notes:**
- Admin should be able to override doctor profiles if necessary.

---

## User Story 6
**Title:**  
_As a doctor, I want to view patient details for upcoming appointments, so that I can be prepared._

**Acceptance Criteria:**
1. The doctor can access patient details for scheduled appointments.
2. Patient history and notes are displayed securely.
3. The system restricts access to only the doctor’s assigned patients.

**Priority:** High  
**Story Points:** 4  

**Notes:**
- Ensure patient privacy and role-based access control.
