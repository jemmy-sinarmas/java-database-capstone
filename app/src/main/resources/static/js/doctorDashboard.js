// doctorDashboard.js

import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";

const appointmentTableBody = document.getElementById("patientTableBody");
const searchBar = document.getElementById("searchBar");
const todayButton = document.getElementById("todayButton");
const datePicker = document.getElementById("datePicker");

let selectedDate = new Date().toISOString().split("T")[0];
let token = localStorage.getItem("token");
let patientName = null;

// Search bar filtering
if (searchBar) {
  searchBar.addEventListener("input", () => {
    patientName = searchBar.value.trim();
    if (!patientName) patientName = "null";
    loadAppointments();
  });
}

// Today button
if (todayButton) {
  todayButton.addEventListener("click", () => {
    selectedDate = new Date().toISOString().split("T")[0];
    if (datePicker) datePicker.value = selectedDate;
    loadAppointments();
  });
}

// Date picker
if (datePicker) {
  datePicker.addEventListener("change", () => {
    selectedDate = datePicker.value;
    loadAppointments();
  });
}

// Main loader
async function loadAppointments() {
  try {
    const appointments = await getAllAppointments(selectedDate, patientName, token);
    appointmentTableBody.innerHTML = "";

    if (!appointments || appointments.length === 0) {
      appointmentTableBody.innerHTML =
        '<tr><td colspan="5">No Appointments found for selected date.</td></tr>';
      return;
    }

    appointments.forEach((appointment) => {
      const row = createPatientRow(appointment);
      appointmentTableBody.appendChild(row);
    });
  } catch (error) {
    console.error("Error loading appointments:", error);
    appointmentTableBody.innerHTML =
      '<tr><td colspan="5">Failed to load appointments. Please try again later.</td></tr>';
  }
}

// Load on page ready
window.addEventListener("DOMContentLoaded", () => {
  if (datePicker) datePicker.value = selectedDate;
  loadAppointments();
});
