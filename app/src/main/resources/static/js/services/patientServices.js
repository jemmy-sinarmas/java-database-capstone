import { API_BASE_URL } from "../config/config.js";

const PATIENT_API = `${API_BASE_URL}/patient`;

/**
 * Registers a new patient in the database.
 * @param {Object} data - Patient registration data (name, email, phone, password, etc.)
 * @returns {Object} - { success: boolean, message: string }
 */
export async function patientSignup(data) {
  try {
    const response = await fetch(`${PATIENT_API}`, {
      method: "POST",
      headers: { "Content-type": "application/json" },
      body: JSON.stringify(data)
    });

    const result = await response.json();

    if (!response.ok) {
      throw new Error(result.message);
    }

    return { success: true, message: result.message };
  } catch (error) {
    console.error("Error :: patientSignup ::", error);
    return { success: false, message: error.message };
  }
}

/**
 * Logs in a patient and returns the fetch response.
 * @param {Object} data - Patient login data (email, password)
 * @returns {Promise<Response>}
 */
export async function patientLogin(data) {
  return fetch(`${PATIENT_API}/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data)
  });
}

/**
 * Fetches details of the currently logged-in patient using a token.
 * @param {string} token - JWT token from localStorage
 * @returns {Object|null} - Patient object or null on failure
 */
export async function getPatientData(token) {
  try {
    const response = await fetch(`${PATIENT_API}/${token}`);
    const data = await response.json();

    if (response.ok) {
      return data.patient;
    }
    return null;
  } catch (error) {
    console.error("Error fetching patient details:", error);
    return null;
  }
}

/**
 * Fetches all appointments of a patient (used in both doctor and patient dashboards).
 * @param {number} id - Patient ID
 * @param {string} token - JWT token for authorization
 * @param {string} user - Role (e.g., "doctor" or "patient")
 * @returns {Array|null} - Appointments or null
 */
export async function getPatientAppointments(id, token, user) {
  try {
    const response = await fetch(`${PATIENT_API}/${id}/${user}/${token}`);
    const data = await response.json();

    if (response.ok) {
      return data.appointments;
    }
    return null;
  } catch (error) {
    console.error("Error fetching patient appointments:", error);
    return null;
  }
}

/**
 * Filters appointments based on a condition and doctor name.
 * @param {string} condition - Appointment condition (e.g., "past", "future")
 * @param {string} name - Doctor name for filtering
 * @param {string} token - JWT token
 * @returns {Object} - Filtered appointment data
 */
export async function filterAppointments(condition, name, token) {
  try {
    const response = await fetch(
      `${PATIENT_API}/filter/${condition}/${name}/${token}`,
      {
        method: "GET",
        headers: { "Content-Type": "application/json" }
      }
    );

    if (response.ok) {
      return await response.json();
    } else {
      console.error("Failed to filter appointments:", response.statusText);
      return { appointments: [] };
    }
  } catch (error) {
    console.error("Error:", error);
    alert("Something went wrong!");
    return { appointments: [] };
  }
}
