import { openModal } from "../components/modals.js";
import { API_BASE_URL } from "../config/config.js";

// API Endpoints
const ADMIN_API = `${API_BASE_URL}/admin`;
const DOCTOR_API = `${API_BASE_URL}/doctor/login`;

// Wait until DOM is ready
window.onload = function () {
  const adminBtn = document.getElementById("adminLoginBtn");
  const doctorBtn = document.getElementById("doctorLoginBtn");

  if (adminBtn) {
    adminBtn.addEventListener("click", () => openModal("adminLogin"));
  }

  if (doctorBtn) {
    doctorBtn.addEventListener("click", () => openModal("doctorLogin"));
  }
};

// Admin Login Handler
window.adminLoginHandler = async function () {
  const username = document.getElementById("adminUsername").value;
  const password = document.getElementById("adminPassword").value;

  const admin = { username, password };

  try {
    const res = await fetch(ADMIN_API, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(admin)
    });

    if (res.ok) {
      const data = await res.json();
      localStorage.setItem("token", data.token);
      selectRole("admin");
    } else {
      alert("❌ Invalid admin credentials!");
    }
  } catch (err) {
    console.error("Admin login error:", err);
    alert("❌ Something went wrong. Please try again.");
  }
};

// Doctor Login Handler
window.doctorLoginHandler = async function () {
  const email = document.getElementById("doctorEmail").value;
  const password = document.getElementById("doctorPassword").value;

  const doctor = { email, password };

  try {
    const res = await fetch(DOCTOR_API, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(doctor)
    });

    if (res.ok) {
      const data = await res.json();
      localStorage.setItem("token", data.token);
      selectRole("doctor");
    } else {
      alert("❌ Invalid doctor credentials!");
    }
  } catch (err) {
    console.error("Doctor login error:", err);
    alert("❌ Something went wrong. Please try again.");
  }
};

// Role selection and redirection
function selectRole(role) {
  localStorage.setItem("userRole", role);

  switch (role) {
    case "admin":
      window.location.href = "/pages/adminDashboard.html";
      break;
    case "doctor":
      window.location.href = "/pages/doctorDashboard.html";
      break;
    default:
      alert("❌ Unknown role. Cannot redirect.");
  }
}
