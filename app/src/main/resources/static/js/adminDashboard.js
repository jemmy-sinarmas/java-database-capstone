// adminDashboard.js

import { openModal } from "../components/modals.js";
import {
  getDoctors,
  filterDoctors,
  saveDoctor
} from "../services/doctorServices.js";
import { createDoctorCard } from "../components/doctorCard.js";

// Add Doctor Button Event
const addDocBtn = document.getElementById("addDocBtn");
if (addDocBtn) {
  addDocBtn.addEventListener("click", () => openModal("addDoctor"));
}

// Load all doctor cards when DOM is ready
window.addEventListener("DOMContentLoaded", loadDoctorCards);

// Load and display all doctors
async function loadDoctorCards() {
  try {
    const doctors = await getDoctors();
    renderDoctorCards(doctors);
  } catch (error) {
    console.error("Error loading doctor cards:", error);
  }
}

// Render list of doctors as cards
function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  contentDiv.innerHTML = "";

  if (!doctors || doctors.length === 0) {
    contentDiv.textContent = "No doctors found.";
    return;
  }

  doctors.forEach((doctor) => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}

// Filter Events
const searchBar = document.getElementById("searchBar");
const timeFilter = document.getElementById("filterTime");
const specialtyFilter = document.getElementById("filterSpecialty");

if (searchBar && timeFilter && specialtyFilter) {
  searchBar.addEventListener("input", filterDoctorsOnChange);
  timeFilter.addEventListener("change", filterDoctorsOnChange);
  specialtyFilter.addEventListener("change", filterDoctorsOnChange);
}

// Filter handler
async function filterDoctorsOnChange() {
  const name = searchBar.value.trim();
  const time = timeFilter.value.trim();
  const specialty = specialtyFilter.value.trim();

  try {
    const data = await filterDoctors(name || "", time || "", specialty || "");
    const doctors = data.doctors || [];

    if (doctors.length > 0) {
      renderDoctorCards(doctors);
    } else {
      const contentDiv = document.getElementById("content");
      contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
    }
  } catch (error) {
    alert("Something went wrong while filtering doctors.");
  }
}

// Admin Add Doctor - Collects form and submits to backend
export async function adminAddDoctor() {
  const name = document.getElementById("docName").value;
  const email = document.getElementById("docEmail").value;
  const password = document.getElementById("docPassword").value;
  const phone = document.getElementById("docPhone").value;
  const specialty = document.getElementById("docSpecialty").value;
  const availableTimes = Array.from(
    document.querySelectorAll("input[name='docAvailability']:checked")
  ).map((checkbox) => checkbox.value);

  const token = localStorage.getItem("token");
  if (!token) {
    alert("Admin login required.");
    return;
  }

  const doctor = { name, email, password, phone, specialty, availableTimes };

  const response = await saveDoctor(doctor, token);

  if (response.success) {
    alert("Doctor added successfully.");
    document.getElementById("modal").style.display = "none";
    loadDoctorCards();
  } else {
    alert(response.message || "Failed to add doctor.");
  }
}
