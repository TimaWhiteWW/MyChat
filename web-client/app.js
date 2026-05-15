const API_HOST = window.location.protocol.startsWith("http")
  ? window.location.hostname
  : "localhost";
const API_BASE = window.MYCHAT_API_BASE || `http://${API_HOST}:8085/api/v1/demo`;
const SESSION_KEY = "mychat.session";
const VERIFY_EMAIL_KEY = "mychat.verify.email";

let sessionUser = null;
let discoverQueue = [];
let selectedChatUser = null;
let unreadChats = 0;

const $ = (id) => document.getElementById(id);

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: options.body instanceof FormData
      ? { ...(options.headers || {}) }
      : { "Content-Type": "application/json", ...(options.headers || {}) },
    ...options
  });
  const text = await response.text();
  const payload = text ? JSON.parse(text) : null;
  if (!response.ok) {
    throw new Error(payload?.error || text || `${response.status} ${response.statusText}`);
  }
  return payload;
}

function toast(text) {
  $("toast").textContent = text;
  $("toast").classList.remove("hidden");
  setTimeout(() => $("toast").classList.add("hidden"), 3000);
}

function updateBadge() {
  $("chatBadge").textContent = unreadChats;
  $("chatBadge").classList.toggle("hidden", unreadChats === 0);
}

function switchAuthTab(tab) {
  document.querySelectorAll("[data-auth-tab]").forEach((button) => {
    button.classList.toggle("active", button.dataset.authTab === tab);
  });
  $("loginForm").classList.toggle("hidden", tab !== "login");
  $("registerForm").classList.toggle("hidden", tab !== "register");
  $("verifyForm").classList.add("hidden");
}

function showVerify(email) {
  localStorage.setItem(VERIFY_EMAIL_KEY, email);
  $("loginForm").classList.add("hidden");
  $("registerForm").classList.add("hidden");
  $("verifyForm").classList.remove("hidden");
  toast("Код подтверждения отправлен на почту. Если SMTP не настроен, смотри лог сервиса.");
}

function switchView(name) {
  document.querySelectorAll(".screen").forEach((screen) => screen.classList.add("hidden"));
  $(`${name}View`).classList.remove("hidden");
  document.querySelectorAll(".nav-item").forEach((button) => {
    button.classList.toggle("active", button.dataset.view === name);
  });
  if (name === "chat") {
    unreadChats = 0;
    updateBadge();
  }
}

function showAuth() {
  $("authView").classList.remove("hidden");
  $("appView").classList.add("hidden");
}

async function showApp(user) {
  sessionUser = user;
  localStorage.setItem(SESSION_KEY, JSON.stringify(user));
  localStorage.removeItem(VERIFY_EMAIL_KEY);
  $("authView").classList.add("hidden");
  $("appView").classList.remove("hidden");
  $("currentUserLabel").textContent = `${user.name} · ${user.email}`;
  loadProfile(user);
  await refreshDiscover();
  await refreshMatches();
}

function loadProfile(user) {
  $("profileName").value = user.name || "";
  $("profileAge").value = user.age || 21;
  $("profileCity").value = user.city || "";
  $("profileGender").value = user.gender || "MALE";
  $("profileInterests").value = user.interests || "";
  $("profilePhoto").value = user.photo?.startsWith("data:") ? "" : (user.photo || "");
  $("profileBio").value = user.bio || "";
  renderProfilePreview(user);
}

function renderProfilePreview(user) {
  $("profilePreviewName").textContent = user.name || "Профиль";
  $("profilePreviewMeta").textContent = `${user.age || "-"} · ${user.city || "Город не указан"}`;
  $("profileAvatar").innerHTML = user.photo
    ? `<img src="${user.photo}" alt="${user.name || "Фото"}">`
    : initials(user);
}

async function saveProfile() {
  let updated = await request(`/users/${sessionUser.id}`, {
    method: "PUT",
    body: JSON.stringify({
      ...sessionUser,
      name: $("profileName").value.trim(),
      age: Number($("profileAge").value),
      city: $("profileCity").value.trim(),
      gender: $("profileGender").value,
      interests: $("profileInterests").value.trim(),
      photo: $("profilePhoto").value.trim() || sessionUser.photo || "",
      bio: $("profileBio").value.trim()
    })
  });

  const file = $("profilePhotoFile").files[0];
  if (file) {
    const formData = new FormData();
    formData.append("photo", file);
    updated = await request(`/users/${sessionUser.id}/photo`, {
      method: "POST",
      body: formData
    });
    $("profilePhotoFile").value = "";
  }

  sessionUser = updated;
  localStorage.setItem(SESSION_KEY, JSON.stringify(updated));
  $("currentUserLabel").textContent = `${updated.name} · ${updated.email}`;
  renderProfilePreview(updated);
  toast("Профиль сохранен");
}

async function refreshDiscover() {
  discoverQueue = await request(`/users/${sessionUser.id}/discover`);
  renderDiscover();
}

function initials(user) {
  return (user.name || user.email || "?").slice(0, 1).toUpperCase();
}

function photoMarkup(user, className = "photo") {
  if (user.photo) {
    return `<div class="${className}"><img src="${user.photo}" alt="${user.name || "Фото"}"></div>`;
  }
  return `<div class="${className}">${initials(user)}</div>`;
}

function interestTags(value) {
  return (value || "")
    .split(",")
    .map((item) => item.trim())
    .filter(Boolean)
    .map((item) => `<span class="tag">${item}</span>`)
    .join("");
}

function renderDiscover() {
  const card = $("discoverCard");
  if (!discoverQueue.length) {
    card.innerHTML = `
      <div class="photo">OK</div>
      <div>
        <div class="person-name">Анкеты закончились</div>
        <p class="meta">Обнови список или зарегистрируй еще пользователей.</p>
      </div>
      <button id="reloadDiscoverBtn" class="secondary">Обновить</button>
    `;
    $("reloadDiscoverBtn").addEventListener("click", refreshDiscover);
    return;
  }

  const person = discoverQueue[0];
  card.innerHTML = `
    ${photoMarkup(person)}
    <div>
      <div class="person-name">${person.name}, ${person.age || "-"}</div>
      <p class="meta">${person.city || "Город не указан"} · ${person.gender || ""}</p>
    </div>
    <p>${person.bio || "Пользователь пока не заполнил описание."}</p>
    <div class="tags">${interestTags(person.interests)}</div>
    <div class="card-actions">
      <button class="pass" id="passBtn">Пропустить</button>
      <button class="like" id="likeBtn">Лайк</button>
    </div>
  `;
  $("passBtn").addEventListener("click", () => passUser(person.id));
  $("likeBtn").addEventListener("click", () => likeUser(person.id));
}

async function passUser(targetId) {
  await request(`/users/${sessionUser.id}/pass/${targetId}`, { method: "POST" });
  discoverQueue.shift();
  renderDiscover();
}

async function likeUser(targetId) {
  const result = await request(`/users/${sessionUser.id}/like/${targetId}`, { method: "POST" });
  discoverQueue.shift();
  toast(result.match ? "Новый матч. Чат открыт в разделе сообщений." : "Лайк отправлен");
  renderDiscover();
  await refreshMatches();
}

async function refreshMatches() {
  const matches = await request(`/users/${sessionUser.id}/matches`);
  renderMatches(matches);
  renderChatMatches(matches);
  if (selectedChatUser) {
    const stillMatched = matches.some((user) => user.id === selectedChatUser.id);
    if (!stillMatched) selectedChatUser = null;
  }
  await renderMessages();
}

function renderMatches(matches) {
  const container = $("matchesList");
  if (!matches.length) {
    container.innerHTML = `<p class="meta">Пока нет матчей. Поставь лайки в разделе поиска.</p>`;
    return;
  }
  container.innerHTML = matches.map((user) => `
    <article class="match-item">
      <div class="match-user">
        ${photoMarkup(user, "avatar")}
        <div>
          <strong>${user.name}, ${user.age || "-"}</strong>
          <span class="meta">${user.city || ""}</span>
        </div>
      </div>
      <span>${user.bio || ""}</span>
      <button data-chat="${user.id}">Написать</button>
    </article>
  `).join("");
  container.querySelectorAll("[data-chat]").forEach((button) => {
    const user = matches.find((item) => item.id === button.dataset.chat);
    button.addEventListener("click", () => openChat(user));
  });
}

function renderChatMatches(matches) {
  const container = $("chatMatches");
  if (!matches.length) {
    container.innerHTML = `<p class="meta">Сначала нужен взаимный лайк.</p>`;
    return;
  }
  container.innerHTML = matches.map((user) => `
    <button class="match-item chat-person" data-chat="${user.id}">
      ${photoMarkup(user, "avatar")}
      <span>
        <strong>${user.name || user.email}</strong>
        <span class="meta">${user.city || ""}</span>
      </span>
    </button>
  `).join("");
  container.querySelectorAll("[data-chat]").forEach((button) => {
    const user = matches.find((item) => item.id === button.dataset.chat);
    button.addEventListener("click", () => openChat(user));
  });
}

async function openChat(user) {
  selectedChatUser = user;
  switchView("chat");
  toast(`Диалог с ${user.name || user.email} открыт`);
  await renderMessages();
}

async function renderMessages() {
  const container = $("messages");
  if (!selectedChatUser) {
    $("chatTitle").textContent = "Выбери пользователя из списка.";
    container.innerHTML = "";
    return;
  }
  $("chatTitle").textContent = `Чат с ${selectedChatUser.name || selectedChatUser.email}`;
  const dialog = await request(`/chat/${sessionUser.id}/${selectedChatUser.id}`);
  if (!dialog.length) {
    container.innerHTML = `<p class="meta">Сообщений пока нет.</p>`;
    return;
  }
  container.innerHTML = dialog.map((message) => `
    <div class="message ${message.fromTag === sessionUser.id ? "mine" : ""}">
      ${message.text}
    </div>
  `).join("");
  container.scrollTop = container.scrollHeight;
}

async function sendMessage(text) {
  if (!selectedChatUser) throw new Error("Сначала выбери матч");
  await request("/chat/send", {
    method: "POST",
    body: JSON.stringify({
      fromTag: sessionUser.id,
      toTag: selectedChatUser.id,
      text
    })
  });
  unreadChats += 1;
  updateBadge();
  await renderMessages();
}

document.querySelectorAll("[data-auth-tab]").forEach((button) => {
  button.addEventListener("click", () => switchAuthTab(button.dataset.authTab));
});

document.querySelectorAll(".nav-item").forEach((button) => {
  button.addEventListener("click", async () => {
    switchView(button.dataset.view);
    if (button.dataset.view === "discover") await refreshDiscover();
    if (button.dataset.view === "matches" || button.dataset.view === "chat") await refreshMatches();
  });
});

$("registerForm").addEventListener("submit", async (event) => {
  event.preventDefault();
  try {
    const email = $("registerEmail").value.trim();
    await request("/register", {
      method: "POST",
      body: JSON.stringify({
        name: $("registerName").value.trim(),
        email,
        password: $("registerPassword").value
      })
    });
    showVerify(email);
  } catch (error) {
    toast(error.message);
  }
});

$("verifyForm").addEventListener("submit", async (event) => {
  event.preventDefault();
  try {
    const user = await request("/verify", {
      method: "POST",
      body: JSON.stringify({
        email: localStorage.getItem(VERIFY_EMAIL_KEY),
        code: $("verifyCode").value.trim()
      })
    });
    await showApp(user);
  } catch (error) {
    toast(error.message);
  }
});

$("loginForm").addEventListener("submit", async (event) => {
  event.preventDefault();
  try {
    const user = await request("/login", {
      method: "POST",
      body: JSON.stringify({
        email: $("loginEmail").value.trim(),
        password: $("loginPassword").value
      })
    });
    await showApp(user);
  } catch (error) {
    toast(error.message);
  }
});

$("saveProfileBtn").addEventListener("click", async () => {
  try {
    await saveProfile();
  } catch (error) {
    toast(error.message);
  }
});

$("seedBtn").addEventListener("click", refreshDiscover);

$("logoutBtn").addEventListener("click", () => {
  localStorage.removeItem(SESSION_KEY);
  sessionUser = null;
  selectedChatUser = null;
  showAuth();
});

$("messageForm").addEventListener("submit", async (event) => {
  event.preventDefault();
  const text = $("messageInput").value.trim();
  if (!text) return;
  try {
    await sendMessage(text);
    $("messageInput").value = "";
  } catch (error) {
    toast(error.message);
  }
});

const savedSession = localStorage.getItem(SESSION_KEY);
const pendingEmail = localStorage.getItem(VERIFY_EMAIL_KEY);
if (savedSession) {
  showApp(JSON.parse(savedSession)).catch(() => showAuth());
} else if (pendingEmail) {
  showVerify(pendingEmail);
} else {
  showAuth();
}
