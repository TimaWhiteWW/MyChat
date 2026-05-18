const API_HOST = window.location.hostname || "localhost";
const BASES = {
  auth: `http://${API_HOST}:8081/api/v1/auth`,
  user: `http://${API_HOST}:8082/api/v1/self`,
  recommendation: `http://${API_HOST}:8083/api/v1/recommendation`,
  swipe: `http://${API_HOST}:8084/api/v1/swipe`,
  notification: `http://${API_HOST}:8085/api/v1/notification`,
  chat: `http://${API_HOST}:8085/api/v1/chat`
};

const HOBBIES = ["SPORTS", "TRAVELING", "MUSIC", "MOVIES", "GAMING", "READING", "COOKING", "PHOTOGRAPHY", "ART", "DANCING", "FITNESS", "HIKING", "TECHNOLOGY", "BOARD_GAMES", "LANGUAGES", "CARS", "FASHION", "PETS", "WRITING", "INVESTING", "MEDITATION", "SCIENCE", "CHESS"];
const PROFESSIONS = ["SOFTWARE_ENGINEER", "DATA_SCIENTIST", "WEB_DEVELOPER", "DOCTOR", "NURSE", "PSYCHOLOGIST", "LAWYER", "TEACHER", "ARCHITECT", "FINANCIAL_ANALYST", "ENTREPRENEUR", "PROJECT_MANAGER", "JOURNALIST", "MUSICIAN", "PHOTOGRAPHER", "CHEF", "PILOT", "SCIENTIST", "ATHLETE", "MODEL", "UNEMPLOYED"];
const SESSION_KEY = "mychat.real.session";

let session = JSON.parse(localStorage.getItem(SESSION_KEY) || "null");
let currentPartnerTag = "";
let eventSource = null;

const $ = (id) => document.getElementById(id);

function toast(message) {
  $("toast").textContent = message;
  $("toast").classList.remove("hidden");
  setTimeout(() => $("toast").classList.add("hidden"), 3500);
}

async function request(url, options = {}) {
  const response = await fetch(url, {
    headers: options.body instanceof FormData ? options.headers : {
      "Content-Type": "application/json",
      ...(options.headers || {})
    },
    ...options
  });
  const text = await response.text();
  let payload = null;
  try {
    payload = text ? JSON.parse(text) : null;
  } catch {
    payload = text;
  }
  if (!response.ok) {
    throw new Error(typeof payload === "string" ? payload : payload?.message || payload?.error || `${response.status} ${response.statusText}`);
  }
  return payload;
}

function selectedValues(id) {
  return Array.from($(id).selectedOptions).map((option) => option.value);
}

function fillMultiSelect(id, values, defaults = []) {
  $(id).innerHTML = values.map((value) => `<option value="${value}" ${defaults.includes(value) ? "selected" : ""}>${value}</option>`).join("");
}

function showAuth() {
  $("authView").classList.remove("hidden");
  $("appView").classList.add("hidden");
}

function showApp() {
  $("authView").classList.add("hidden");
  $("appView").classList.remove("hidden");
  $("currentUserLabel").textContent = `${session.tag} · ${session.email}`;
  $("profileTag").value = session.tag;
  $("profileEmail").value = session.email;
  $("profileName").value = session.name || "";
  startNotifications();
}

function saveSession(patch) {
  session = { ...(session || {}), ...patch };
  localStorage.setItem(SESSION_KEY, JSON.stringify(session));
}

function switchAuthTab(tab) {
  document.querySelectorAll("[data-auth-tab]").forEach((button) => button.classList.toggle("active", button.dataset.authTab === tab));
  $("loginForm").classList.toggle("hidden", tab !== "login");
  $("registerForm").classList.toggle("hidden", tab !== "register");
  $("verifyForm").classList.add("hidden");
}

function switchView(name) {
  document.querySelectorAll(".screen").forEach((screen) => screen.classList.add("hidden"));
  $(`${name}View`).classList.remove("hidden");
  document.querySelectorAll(".nav-item").forEach((button) => button.classList.toggle("active", button.dataset.view === name));
}

async function register(event) {
  event.preventDefault();
  const password = $("registerPassword").value;
  const passwordConfirmation = $("registerPasswordConfirmation").value;
  const email = $("registerEmail").value.trim();
  const tag = $("registerTag").value.trim();

  await request(`${BASES.auth}/register`, {
    method: "POST",
    body: JSON.stringify({
      name: $("registerName").value.trim(),
      tag,
      email,
      password,
      passwordConfirmation
    })
  });

  saveSession({ email, tag, name: $("registerName").value.trim() });
  $("verifyForm").classList.remove("hidden");
  $("registerForm").classList.add("hidden");
  toast("Аккаунт создан. Подтверди email кодом.");
}

async function verify(event) {
  event.preventDefault();
  await request(`${BASES.auth}/verify`, {
    method: "POST",
    body: JSON.stringify({ email: session.email, code: $("verifyCode").value.trim() })
  });
  toast("Email подтвержден. Теперь можно войти.");
  switchAuthTab("login");
  $("loginEmail").value = session.email;
}

async function login(event) {
  event.preventDefault();
  const email = $("loginEmail").value.trim();
  const password = $("loginPassword").value;
  const tokens = await request(`${BASES.auth}/login?email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`);
  saveSession({ email, accessToken: tokens.accessToken, refreshToken: tokens.refreshToken, tag: session?.tag || email.split("@")[0] });
  showApp();
  toast("Вход выполнен.");
}

async function saveProfile() {
  const form = new FormData();
  const fields = {
    tag: $("profileTag").value.trim(),
    email: $("profileEmail").value.trim(),
    name: $("profileName").value.trim(),
    surname: $("profileSurname").value.trim(),
    phoneNumber: $("profilePhone").value.trim(),
    weight: $("profileWeight").value,
    height: $("profileHeight").value,
    earnings: $("profileEarnings").value,
    age: $("profileAge").value,
    gender: $("profileGender").value,
    aboutMe: $("profileAboutMe").value.trim(),
    city: $("profileCity").value.trim(),
    country: $("profileCountry").value.trim(),
    location: $("profileLocation").value.trim(),
    personalityExtraversion: $("personalityExtraversion").value,
    personalityOpenness: $("personalityOpenness").value,
    personalityConscientiousness: $("personalityConscientiousness").value,
    lifeValueFamily: $("lifeValueFamily").value,
    lifeValueCareer: $("lifeValueCareer").value,
    activityLevel: $("activityLevel").value
  };
  Object.entries(fields).forEach(([key, value]) => form.append(key, value));
  selectedValues("profileHobby").forEach((value) => form.append("hobby", value));
  selectedValues("profileProfession").forEach((value) => form.append("profession", value));
  Array.from($("profilePictures").files).forEach((file) => form.append("pictures", file));

  await request(`${BASES.user}/create`, { method: "POST", body: form });
  saveSession({ tag: fields.tag, email: fields.email, name: fields.name });
  $("currentUserLabel").textContent = `${session.tag} · ${session.email}`;
  toast("Профиль сохранен.");
}

async function savePreferences() {
  await request(`${BASES.user}/preferences`, {
    method: "POST",
    body: JSON.stringify({
      userTag: session.tag,
      preferencesHobby: selectedValues("preferencesHobby"),
      preferencesProfessions: selectedValues("preferencesProfessions"),
      minEarnings: Number($("minEarnings").value),
      minBMI: Number($("minBMI").value),
      maxBMI: Number($("maxBMI").value),
      minHeight: Number($("minHeight").value),
      maxHeight: Number($("maxHeight").value),
      minAge: Number($("minAge").value),
      maxAge: Number($("maxAge").value),
      distance: Number($("distance").value)
    })
  });
  toast("Предпочтения сохранены.");
}

async function loadRecommendations() {
  const list = await request(`${BASES.recommendation}/selectPartnerStack?tag=${encodeURIComponent(session.tag)}`, { method: "POST" });
  renderRecommendations(Array.isArray(list) ? list : []);
}

function renderRecommendations(list) {
  const container = $("recommendationsList");
  if (!list.length) {
    container.innerHTML = `<div class="person-card"><h3>Нет рекомендаций</h3><p class="meta">Создай профиль и предпочтения, затем обнови список.</p></div>`;
    return;
  }
  container.innerHTML = list.map((profile) => `
    <article class="person-card">
      <h3>${profile.name || profile.tag} ${profile.surname || ""}</h3>
      <p class="meta">@${profile.tag} · ${profile.age || "-"} · ${profile.city || ""}, ${profile.country || ""}</p>
      <p>${profile.aboutMe || "Описание не заполнено."}</p>
      <div class="tags">${(profile.hobby || []).slice(0, 6).map((item) => `<span class="tag">${item}</span>`).join("")}</div>
      <div class="card-actions">
        <button class="pass" type="button" data-pass="${profile.tag}">Пропуск</button>
        <button class="accent" type="button" data-like="${profile.tag}">Лайк</button>
        <button type="button" data-chat="${profile.tag}">Чат</button>
      </div>
    </article>
  `).join("");
  container.querySelectorAll("[data-pass]").forEach((button) => button.addEventListener("click", () => swipe("pass", button.dataset.pass)));
  container.querySelectorAll("[data-like]").forEach((button) => button.addEventListener("click", () => swipe("like", button.dataset.like)));
  container.querySelectorAll("[data-chat]").forEach((button) => button.addEventListener("click", () => openDialog(button.dataset.chat)));
}

async function swipe(action, targetTag) {
  await request(`${BASES.swipe}/${action}`, {
    method: "POST",
    body: JSON.stringify({ userTag: session.tag, likedUserTag: targetTag })
  });
  toast(action === "like" ? `Лайк отправлен @${targetTag}` : `Профиль @${targetTag} пропущен`);
}

async function openDialog(tag = $("chatPartnerTag").value.trim()) {
  if (!tag) return toast("Укажи тег собеседника.");
  currentPartnerTag = tag;
  $("chatPartnerTag").value = tag;
  switchView("chat");
  const messages = await request(`${BASES.chat}/dialog?userTag=${encodeURIComponent(session.tag)}&partnerTag=${encodeURIComponent(tag)}`);
  renderMessages(messages || []);
}

function renderMessages(messages) {
  $("messages").innerHTML = messages.map((message) => `
    <div class="message ${message.fromTag === session.tag ? "mine" : ""}">
      <strong>@${message.fromTag}</strong><br>${message.text}
    </div>
  `).join("");
  $("messages").scrollTop = $("messages").scrollHeight;
}

async function sendMessage(event) {
  event.preventDefault();
  const text = $("messageInput").value.trim();
  if (!currentPartnerTag) return toast("Сначала открой диалог.");
  if (!text) return;
  await request(`${BASES.chat}/send`, {
    method: "POST",
    body: JSON.stringify({ fromTag: session.tag, toTag: currentPartnerTag, text })
  });
  $("messageInput").value = "";
  await openDialog(currentPartnerTag);
}

function startNotifications() {
  if (eventSource) eventSource.close();
  eventSource = new EventSource(`${BASES.notification}/subscribe?userTag=${encodeURIComponent(session.tag)}`);
  eventSource.onmessage = (event) => {
    $("notificationStream").textContent = `${new Date().toLocaleTimeString()} · ${event.data}\n${$("notificationStream").textContent}`;
  };
}

async function checkServices() {
  const checks = [
    ["Auth", `${BASES.auth}/login?email=healthcheck@example.com&password=bad`],
    ["User", `${BASES.user}/preferences`],
    ["Recommendation", `${BASES.recommendation}/selectPartnerStack?tag=${encodeURIComponent(session?.tag || "health")}`],
    ["Swipe", `${BASES.swipe}/like`],
    ["Notification", `${BASES.chat}/dialog?userTag=health&partnerTag=check`]
  ];
  $("serviceStatus").innerHTML = "";
  for (const [name, url] of checks) {
    try {
      await fetch(url, { method: name === "Recommendation" ? "POST" : "GET" });
      $("serviceStatus").insertAdjacentHTML("beforeend", `<div class="status-card"><strong>${name}</strong><span class="status-ok">доступен</span></div>`);
    } catch (error) {
      $("serviceStatus").insertAdjacentHTML("beforeend", `<div class="status-card"><strong>${name}</strong><span class="status-bad">нет ответа</span></div>`);
    }
  }
}

document.querySelectorAll("[data-auth-tab]").forEach((button) => button.addEventListener("click", () => switchAuthTab(button.dataset.authTab)));
document.querySelectorAll(".nav-item").forEach((button) => button.addEventListener("click", () => switchView(button.dataset.view)));
$("registerForm").addEventListener("submit", (event) => register(event).catch((error) => toast(error.message)));
$("verifyForm").addEventListener("submit", (event) => verify(event).catch((error) => toast(error.message)));
$("loginForm").addEventListener("submit", (event) => login(event).catch((error) => toast(error.message)));
$("saveProfileBtn").addEventListener("click", () => saveProfile().catch((error) => toast(error.message)));
$("savePreferencesBtn").addEventListener("click", () => savePreferences().catch((error) => toast(error.message)));
$("loadRecommendationsBtn").addEventListener("click", () => loadRecommendations().catch((error) => toast(error.message)));
$("openDialogBtn").addEventListener("click", () => openDialog().catch((error) => toast(error.message)));
$("messageForm").addEventListener("submit", (event) => sendMessage(event).catch((error) => toast(error.message)));
$("checkServicesBtn").addEventListener("click", () => checkServices());
$("logoutBtn").addEventListener("click", () => {
  localStorage.removeItem(SESSION_KEY);
  if (eventSource) eventSource.close();
  session = null;
  showAuth();
});

fillMultiSelect("profileHobby", HOBBIES, ["MUSIC", "MOVIES"]);
fillMultiSelect("profileProfession", PROFESSIONS, ["SOFTWARE_ENGINEER"]);
fillMultiSelect("preferencesHobby", HOBBIES, ["MUSIC", "TRAVELING"]);
fillMultiSelect("preferencesProfessions", PROFESSIONS, ["SOFTWARE_ENGINEER", "DATA_SCIENTIST"]);

if (session?.accessToken || session?.tag) {
  showApp();
} else {
  showAuth();
}
