const BASES = {
  auth: "/api/v1/auth",
  user: "/api/v1/self",
  recommendation: "/api/v1/recommendation",
  swipe: "/api/v1/swipe",
  notification: "/api/v1/notification",
  chat: "/api/v1/chat"
};

const HOBBIES = [
  "SPORTS", "TRAVELING", "MUSIC", "MOVIES", "GAMING", "READING", "COOKING", "PHOTOGRAPHY", "ART", "DANCING",
  "FITNESS", "HIKING", "CYCLING", "SWIMMING", "YOGA", "FISHING", "CAMPING", "TECHNOLOGY", "BOARD_GAMES",
  "VOLUNTEERING", "LANGUAGES", "CARS", "FASHION", "PETS", "GARDENING", "WRITING", "DIY", "ASTRONOMY",
  "MARTIAL_ARTS", "TENNIS", "BASKETBALL", "FOOTBALL", "SKIING", "SNOWBOARDING", "SKATEBOARDING", "SURFING",
  "SCUBA_DIVING", "ROCK_CLIMBING", "HORSE_RIDING", "ARCHERY", "ESPORTS", "PODCASTS", "THEATER", "OPERA",
  "MAGIC", "ASTROLOGY", "TATTOOS", "BODYBUILDING", "INVESTING", "MEDITATION", "MINDFULNESS", "HISTORY",
  "SCIENCE", "PHILOSOPHY", "POLITICS", "WOODWORKING", "LEATHERCRAFT", "METALWORKING", "KNITTING",
  "CROCHETING", "MAKEUP", "JEWELRY_MAKING", "MODEL_BUILDING", "RC_CARS", "DRONES", "AVIATION",
  "SPACE_EXPLORATION", "STANDUP_COMEDY", "STREET_ART", "VLOGGING", "BLOGGING", "GRAPHIC_DESIGN",
  "VIDEO_EDITING", "ANIME", "K_POP", "COSPLAY", "TAROT", "ESCAPE_ROOMS", "PAINTBALL", "LASER_TAG",
  "WINE_TASTING", "COFFEE_CULTURE", "CRAFT_BEER", "COCKTAIL_MAKING", "CHESS", "POKER", "MAGIC_THE_GATHERING",
  "DUNGEONS_AND_DRAGONS", "PARKOUR", "FREE_RUNNING", "SKYDIVING", "BUNGEE_JUMPING", "HOT_AIR_BALLOONING",
  "TRAMPOLINING"
];

const PROFESSIONS = [
  "SOFTWARE_ENGINEER", "DATA_SCIENTIST", "SYSTEM_ADMINISTRATOR", "CYBER_SECURITY_SPECIALIST", "GAME_DEVELOPER",
  "MOBILE_DEVELOPER", "WEB_DEVELOPER", "CLOUD_ENGINEER", "NETWORK_ENGINEER", "IT_SUPPORT", "DEVOPS_ENGINEER",
  "MACHINE_LEARNING_ENGINEER", "AI_RESEARCHER", "BLOCKCHAIN_DEVELOPER", "DOCTOR", "SURGEON", "NURSE",
  "PARAMEDIC", "DENTIST", "PHARMACIST", "PSYCHOLOGIST", "PSYCHIATRIST", "PHYSIOTHERAPIST", "VETERINARIAN",
  "LAWYER", "JUDGE", "POLICE_OFFICER", "TEACHER", "PROFESSOR", "RESEARCHER", "TRANSLATOR", "ARCHITECT",
  "CIVIL_ENGINEER", "ELECTRICAL_ENGINEER", "MECHANICAL_ENGINEER", "ACCOUNTANT", "FINANCIAL_ANALYST",
  "INVESTMENT_BANKER", "ECONOMIST", "ENTREPRENEUR", "BUSINESS_ANALYST", "PROJECT_MANAGER",
  "MARKETING_SPECIALIST", "SALES_MANAGER", "HR_MANAGER", "JOURNALIST", "WRITER", "EDITOR", "ACTOR", "MUSICIAN",
  "SINGER", "DANCER", "PHOTOGRAPHER", "VIDEOGRAPHER", "GRAPHIC_DESIGNER", "UX_UI_DESIGNER", "CHEF", "BARISTA",
  "PILOT", "SCIENTIST", "BIOLOGIST", "CHEMIST", "PHYSICIST", "ATHLETE", "COACH", "PERSONAL_TRAINER",
  "MECHANIC", "ELECTRICIAN", "MODEL", "INFLUENCER", "MAKEUP_ARTIST", "TRAVEL_AGENT", "TOUR_GUIDE",
  "REAL_ESTATE_AGENT", "STREAMER", "UNEMPLOYED"
];

const SESSION_KEY = "mychat.real.session";
const CHATS_KEY = "mychat.real.chats";
const AVATAR_KEY = "mychat.real.avatar";
const DEFAULT_LOCATION = "POINT (37.6173 55.7558)";

let session = JSON.parse(localStorage.getItem(SESSION_KEY) || "null");
let knownChats = JSON.parse(localStorage.getItem(CHATS_KEY) || "[]");
let currentPartnerTag = "";
let eventSource = null;
let avatarDataUrl = localStorage.getItem(AVATAR_KEY) || "";

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
  $(id).innerHTML = values.map((value) => `<option value="${value}" ${defaults.includes(value) ? "selected" : ""}>${value.replaceAll("_", " ")}</option>`).join("");
}

function showAuth() {
  $("authView").classList.remove("hidden");
  $("appView").classList.add("hidden");
}

function showApp() {
  $("authView").classList.add("hidden");
  $("appView").classList.remove("hidden");
  $("currentUserLabel").textContent = `@${session.tag}`;
  $("profileTag").value = session.tag;
  $("profileEmail").value = session.email;
  $("profileName").value = session.name || "";
  updateProfilePreview();
  renderChatList();
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
  if (name === "chat") renderChatList();
}

async function register(event) {
  event.preventDefault();
  const password = $("registerPassword").value;
  const passwordConfirmation = $("registerPasswordConfirmation").value;
  const email = $("registerEmail").value.trim();
  const tag = $("registerTag").value.trim();

  const passwordError = validatePassword(password, passwordConfirmation);
  if (passwordError) {
    toast(passwordError);
    return;
  }

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
  toast("Введите код подтверждения из письма.");
}

function validatePassword(password, passwordConfirmation) {
  if (password !== passwordConfirmation) return "Пароли не совпадают.";
  if (password.length < 10) return "Пароль должен быть не короче 10 символов.";
  if (!/[A-ZА-Я]/.test(password)) return "Пароль должен содержать заглавную букву.";
  if (!/\d/.test(password)) return "Пароль должен содержать цифру.";
  if (!/[^a-zA-Zа-яА-Я0-9]/.test(password)) return "Пароль должен содержать спецсимвол, например ! или #.";
  return "";
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
}

function updateProfilePreview() {
  const name = $("profileName").value.trim() || session?.name || "Профиль";
  const age = $("profileAge").value || "";
  const city = $("profileCity").value.trim();
  $("profilePreviewName").textContent = name;
  $("profilePreviewMeta").textContent = [age, city].filter(Boolean).join(" · ");
  $("sidebarAvatar").innerHTML = avatarDataUrl ? `<img src="${avatarDataUrl}" alt="">` : initials(name);
  $("profileAvatarPreview").innerHTML = avatarDataUrl ? `<img src="${avatarDataUrl}" alt="">` : initials(name);
}

function initials(value) {
  return (value || session?.tag || "M").slice(0, 1).toUpperCase();
}

function handleAvatarPreview() {
  const file = $("profilePictures").files[0];
  if (!file) return;
  const reader = new FileReader();
  reader.onload = () => {
    avatarDataUrl = String(reader.result);
    localStorage.setItem(AVATAR_KEY, avatarDataUrl);
    updateProfilePreview();
  };
  reader.readAsDataURL(file);
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
    location: DEFAULT_LOCATION,
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
  $("currentUserLabel").textContent = `@${session.tag}`;
  updateProfilePreview();
  await savePreferences(false);
  toast("Профиль сохранен.");
}

async function savePreferences(showToast = true) {
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
  if (showToast) toast("Предпочтения сохранены.");
}

async function loadRecommendations() {
  const list = await request(`${BASES.recommendation}/selectPartnerStack?tag=${encodeURIComponent(session.tag)}`, { method: "POST" });
  renderRecommendations(Array.isArray(list) ? list : []);
}

function renderRecommendations(list) {
  const container = $("recommendationsList");
  if (!list.length) {
    container.innerHTML = `<div class="empty-state"><h3>Пока никого нет</h3><p>Когда другой пользователь зарегистрируется, заполнит профиль и подойдет под фильтры, он появится здесь.</p></div>`;
    return;
  }
  container.innerHTML = list.map((profile) => `
    <article class="person-card">
      <div class="mini-avatar">${initials(profile.name || profile.tag)}</div>
      <h3>${profile.name || profile.tag} ${profile.surname || ""}</h3>
      <p class="meta">@${profile.tag} · ${profile.age || "-"} · ${profile.city || ""}</p>
      <p>${profile.aboutMe || "Описание не заполнено."}</p>
      <div class="tags">${(profile.hobby || []).slice(0, 8).map((item) => `<span class="tag">${item.replaceAll("_", " ")}</span>`).join("")}</div>
      <div class="card-actions">
        <button class="pass" type="button" data-pass="${profile.tag}">Пропустить</button>
        <button class="accent" type="button" data-like="${profile.tag}">Лайк</button>
        <button type="button" data-chat="${profile.tag}">Написать</button>
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
  if (action === "like") addChat(targetTag);
  toast(action === "like" ? `Лайк отправлен @${targetTag}` : `Профиль @${targetTag} пропущен`);
}

function addChat(tag) {
  if (!tag || knownChats.includes(tag)) return;
  knownChats = [tag, ...knownChats];
  localStorage.setItem(CHATS_KEY, JSON.stringify(knownChats));
  renderChatList();
}

function renderChatList() {
  const list = $("chatList");
  if (!list) return;
  if (!knownChats.length) {
    list.innerHTML = `<div class="empty-chat">Чаты появятся после лайка или нажатия "Написать" в рекомендациях.</div>`;
    return;
  }
  list.innerHTML = knownChats.map((tag) => `
    <button class="dialog-item ${tag === currentPartnerTag ? "active" : ""}" type="button" data-dialog="${tag}">
      <span class="mini-avatar">${initials(tag)}</span>
      <span>@${tag}</span>
    </button>
  `).join("");
  list.querySelectorAll("[data-dialog]").forEach((button) => button.addEventListener("click", () => openDialog(button.dataset.dialog)));
}

async function openDialog(tag) {
  currentPartnerTag = tag;
  addChat(tag);
  switchView("chat");
  $("chatTitle").textContent = `@${tag}`;
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
  if (!currentPartnerTag) return toast("Выберите чат.");
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
  eventSource.onmessage = (event) => toast(event.data);
}

document.querySelectorAll("[data-auth-tab]").forEach((button) => button.addEventListener("click", () => switchAuthTab(button.dataset.authTab)));
document.querySelectorAll(".nav-item").forEach((button) => button.addEventListener("click", () => switchView(button.dataset.view)));
$("registerForm").addEventListener("submit", (event) => register(event).catch((error) => toast(error.message)));
$("verifyForm").addEventListener("submit", (event) => verify(event).catch((error) => toast(error.message)));
$("loginForm").addEventListener("submit", (event) => login(event).catch((error) => toast(error.message)));
$("saveProfileBtn").addEventListener("click", () => saveProfile().catch((error) => toast(error.message)));
$("loadRecommendationsBtn").addEventListener("click", () => loadRecommendations().catch((error) => toast(error.message)));
$("messageForm").addEventListener("submit", (event) => sendMessage(event).catch((error) => toast(error.message)));
$("profilePictures").addEventListener("change", handleAvatarPreview);
["profileName", "profileAge", "profileCity"].forEach((id) => $(id).addEventListener("input", updateProfilePreview));
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
