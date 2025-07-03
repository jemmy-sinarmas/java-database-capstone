export function renderHeader() {
  const headerDiv = document.getElementById("header");

  // Clear session if user lands on root page
  if (window.location.pathname.endsWith("/")) {
    localStorage.removeItem("userRole");
    localStorage.removeItem("token");
    headerDiv.innerHTML = `
      <header class="header">
        <div class="logo-section">
          <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
          <span class="logo-title">Hospital CMS</span>
        </div>
      </header>`;
    return;
  }

  const role = localStorage.getItem("userRole");
  const token = localStorage.getItem("token");

  // Validate session if role requires token
  if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
    localStorage.removeItem("userRole");
    alert("Session expired or invalid login. Please log in again.");
    window.location.href = "/";
    return;
  }

  // Start building header HTML
  let headerContent = `
    <header class="header">
      <div class="logo-section">
        <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
        <span class="logo-title">Hospital CMS</span>
      </div>
      <nav>`;

  // Role-based header actions
  if (role === "admin") {
    headerContent += `
      <button id="addDocBtn" class="adminBtn" onclick="openModal('addDoctor')">Add Doctor</button>
      <a href="#" id="logoutLink">Logout</a>`;
  } else if (role === "doctor") {
    headerContent += `
      <button class="adminBtn" onclick="selectRole('doctor')">Home</button>
      <a href="#" id="logoutLink">Logout</a>`;
  } else if (role === "patient") {
    headerContent += `
      <button id="patientLogin" class="adminBtn">Login</button>
      <button id="patientSignup" class="adminBtn">Sign Up</button>`;
  } else if (role === "loggedPatient") {
    headerContent += `
      <button id="home" class="adminBtn" onclick="window.location.href='/pages/loggedPatientDashboard.html'">Home</button>
      <button id="patientAppointments" class="adminBtn" onclick="window.location.href='/pages/patientAppointments.html'">Appointments</button>
      <a href="#" id="logoutPatientLink">Logout</a>`;
  }

  headerContent += `</nav></header>`;

  // Inject header HTML
  headerDiv.innerHTML = headerContent;

  // Attach event listeners to dynamic buttons
  attachHeaderButtonListeners();
}

function attachHeaderButtonListeners() {
  const loginBtn = document.getElementById("patientLogin");
  const signupBtn = document.getElementById("patientSignup");
  const logoutLink = document.getElementById("logoutLink");
  const logoutPatientLink = document.getElementById("logoutPatientLink");

  if (loginBtn) {
    loginBtn.addEventListener("click", () => openModal("loginPatient"));
  }

  if (signupBtn) {
    signupBtn.addEventListener("click", () => openModal("signupPatient"));
  }

  if (logoutLink) {
    logoutLink.addEventListener("click", logout);
  }

  if (logoutPatientLink) {
    logoutPatientLink.addEventListener("click", logoutPatient);
  }
}

function logout() {
  localStorage.removeItem("token");
  localStorage.removeItem("userRole");
  window.location.href = "/";
}

function logoutPatient() {
  localStorage.removeItem("token");
  localStorage.setItem("userRole", "patient");
  window.location.href = "/pages/patientDashboard.html";
}

// Call renderHeader when the page loads
document.addEventListener("DOMContentLoaded", renderHeader);
