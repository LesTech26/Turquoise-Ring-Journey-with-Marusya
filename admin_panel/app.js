const storageKey = "turquoise-ring-admin-data";
const accessKey = "turquoise-ring-admin-unlocked";

const seedData = {
  districts: [
    {
      id: "bolhov",
      name: "Болховский район",
      accentName: "Бо́лховский район",
      status: "published",
      heroImage: "",
      history:
        "Один из старейших районов Орловской области. Материалы для детей подаются через короткие исторические заметки, ремесла и местные символы.",
      facts: [
        "Болхов известен старинными храмами и торговыми традициями.",
        "В народном костюме часто встречались красные и синие акценты.",
        "Район подходит для первого учебного маршрута по истории края."
      ],
      images: ["assets/districts/bolhov-1.jpg", "assets/districts/bolhov-2.jpg"]
    },
    {
      id: "livny",
      name: "Ливенский район",
      accentName: "Ли́венский район",
      status: "review",
      heroImage: "",
      history:
        "Раздел о Ливенском районе можно связать с ремеслами, ярмарками и музыкальными традициями. Текст рассчитан на школьников 7-14 лет.",
      facts: [
        "Ливенская гармошка считается узнаваемым культурным символом.",
        "В заданиях района удобно использовать вопросы о промыслах.",
        "Фотогалерея хранит изображения достопримечательностей и костюма."
      ],
      images: ["assets/districts/livny-1.jpg"]
    },
    {
      id: "mcenck",
      name: "Мценский район",
      accentName: "Мце́нский район",
      status: "draft",
      heroImage: "",
      history:
        "Карточка района хранит историю, факты, изображения и связи с вопросами викторины. Администратор может дополнять материалы перед публикацией.",
      facts: [
        "Район можно дополнить временной лентой по эпохам.",
        "Для костюма задаются отдельные элементы и ссылки на 3D-модели."
      ],
      images: []
    }
  ],
  costumes: [
    {
      id: "costume-1",
      districtId: "bolhov",
      name: "Праздничный пояс",
      category: "Пояс",
      color: "#cc0000",
      image: "assets/costumes/belt-bolhov.png",
      model: "assets/models/belt-bolhov.glb",
      description:
        "Яркий тканый пояс используется как заметный элемент праздничного наряда. В приложении его можно показывать отдельно и в составе костюма."
    },
    {
      id: "costume-2",
      districtId: "livny",
      name: "Украшенный сарафан",
      category: "Сарафан",
      color: "#1e3a8a",
      image: "assets/costumes/sarafan-livny.png",
      model: "assets/models/sarafan-livny.glb",
      description:
        "Сарафан с контрастной отделкой хранит описание, изображение и путь к 3D-модели для интерактивного просмотра."
    }
  ],
  questions: [
    {
      id: "question-1",
      districtId: "bolhov",
      type: "single",
      text: "Какой цвет часто встречался в праздничных элементах костюма?",
      answers: ["Красный", "Серый", "Черный"],
      correctIndex: 0,
      points: 5,
      explanation: "Красный цвет использовался как яркий праздничный акцент."
    },
    {
      id: "question-2",
      districtId: "livny",
      type: "single",
      text: "Какой музыкальный символ связан с Ливнами?",
      answers: ["Гармошка", "Гусли", "Балалайка"],
      correctIndex: 0,
      points: 5,
      explanation: "Ливенская гармошка стала заметным культурным образом района."
    }
  ],
  users: [
    { id: "u1", name: "Аня", completedDistricts: 9, averageScore: 86, lastSeen: "2026-06-21" },
    { id: "u2", name: "Миша", completedDistricts: 14, averageScore: 91, lastSeen: "2026-06-22" },
    { id: "u3", name: "София", completedDistricts: 6, averageScore: 74, lastSeen: "2026-06-19" },
    { id: "u4", name: "Класс 4Б", completedDistricts: 18, averageScore: 88, lastSeen: "2026-06-23" }
  ],
  activity: [12, 18, 16, 25, 21, 30, 28, 34, 38, 33, 41, 46]
};

let state = loadState();
let selectedDistrictId = state.districts[0]?.id || null;
let selectedCostumeId = state.costumes[0]?.id || null;
let selectedQuestionId = state.questions[0]?.id || null;

const $ = (selector) => document.querySelector(selector);
const $$ = (selector) => Array.from(document.querySelectorAll(selector));

const viewTitles = {
  dashboard: "Статистика",
  districts: "Районы",
  costumes: "Костюмы",
  questions: "Викторина",
  exchange: "JSON"
};

document.addEventListener("DOMContentLoaded", () => {
  setupAccess();
  bindNavigation();
  bindDistricts();
  bindCostumes();
  bindQuestions();
  bindExchange();
  bindGlobalActions();
  renderAll();
});

function loadState() {
  const saved = localStorage.getItem(storageKey);
  if (!saved) return structuredClone(seedData);
  try {
    const parsed = JSON.parse(saved);
    return normalizeData(parsed);
  } catch {
    return structuredClone(seedData);
  }
}

function normalizeData(data) {
  return {
    districts: Array.isArray(data.districts) ? data.districts : [],
    costumes: Array.isArray(data.costumes) ? data.costumes : [],
    questions: Array.isArray(data.questions) ? data.questions : [],
    users: Array.isArray(data.users) ? data.users : [],
    activity: Array.isArray(data.activity) ? data.activity : []
  };
}

function saveState(message = "Сохранено") {
  localStorage.setItem(storageKey, JSON.stringify(state));
  renderAll();
  showToast(message);
}

function setupAccess() {
  const lockScreen = $("#lockScreen");
  if (sessionStorage.getItem(accessKey) === "true") {
    lockScreen.classList.add("hidden");
  }

  $("#lockForm").addEventListener("submit", (event) => {
    event.preventDefault();
    if ($("#accessCode").value.trim() === "admin2026") {
      sessionStorage.setItem(accessKey, "true");
      lockScreen.classList.add("hidden");
      showToast("Админ-панель открыта");
      return;
    }
    showToast("Неверное кодовое слово");
  });

  let clicks = 0;
  $("#brandButton").addEventListener("click", () => {
    clicks += 1;
    if (clicks >= 5) {
      sessionStorage.removeItem(accessKey);
      lockScreen.classList.remove("hidden");
      clicks = 0;
    }
    setTimeout(() => {
      clicks = 0;
    }, 1400);
  });
}

function bindNavigation() {
  $$(".nav-link").forEach((button) => {
    button.addEventListener("click", () => {
      const view = button.dataset.view;
      $$(".nav-link").forEach((item) => item.classList.toggle("active", item === button));
      $$(".view").forEach((item) => item.classList.remove("active"));
      $(`#${view}View`).classList.add("active");
      $("#viewTitle").textContent = viewTitles[view];
      if (view === "exchange") renderJsonArea();
    });
  });
}

function bindGlobalActions() {
  $("#quickExportButton").addEventListener("click", downloadJson);
  $("#resetProgressButton").addEventListener("click", () => {
    state.users = state.users.map((user) => ({
      ...user,
      completedDistricts: 0,
      averageScore: 0,
      lastSeen: new Date().toISOString().slice(0, 10)
    }));
    state.activity = state.activity.map(() => 0);
    saveState("Прогресс пользователей сброшен");
  });
}

function renderAll() {
  ensureSelections();
  renderDashboard();
  renderDistrictList();
  renderDistrictForm();
  renderDistrictOptions();
  renderCostumeList();
  renderCostumeForm();
  renderQuestionList();
  renderQuestionForm();
  renderJsonPreview();
}

function ensureSelections() {
  if (!state.districts.some((item) => item.id === selectedDistrictId)) {
    selectedDistrictId = state.districts[0]?.id || null;
  }
  if (!state.costumes.some((item) => item.id === selectedCostumeId)) {
    selectedCostumeId = state.costumes[0]?.id || null;
  }
  if (!state.questions.some((item) => item.id === selectedQuestionId)) {
    selectedQuestionId = state.questions[0]?.id || null;
  }
}

function renderDashboard() {
  const totalUsers = state.users.length;
  const completed = state.users.reduce((sum, user) => sum + Number(user.completedDistricts || 0), 0);
  const averageScore = totalUsers
    ? Math.round(state.users.reduce((sum, user) => sum + Number(user.averageScore || 0), 0) / totalUsers)
    : 0;

  const metrics = [
    ["Пользователи", totalUsers, "активных профилей"],
    ["Районы", state.districts.length, "карточек контента"],
    ["Костюмы", state.costumes.length, "элементов наряда"],
    ["Средний балл", `${averageScore}%`, `${completed} прохождений`]
  ];

  $("#metricGrid").innerHTML = metrics
    .map(
      ([label, value, note]) => `
        <article class="metric-card">
          <span>${label}</span>
          <strong>${value}</strong>
          <small>${note}</small>
        </article>
      `
    )
    .join("");

  const maxValue = Math.max(...state.activity, 1);
  $("#activityChart").innerHTML = state.activity
    .map((value, index) => {
      const height = Math.max(18, Math.round((value / maxValue) * 205));
      return `
        <div class="chart-bar">
          <span style="height:${height}px"></span>
          <small>${index + 1}</small>
        </div>
      `;
    })
    .join("");

  const best = [...state.users].sort((a, b) => b.averageScore - a.averageScore)[0];
  $("#bestScoreLabel").textContent = best ? `${best.name}: ${best.averageScore}%` : "Нет данных";
  $("#usersTable").innerHTML = state.users
    .map(
      (user) => `
        <tr>
          <td>${escapeHtml(user.name)}</td>
          <td>${Number(user.completedDistricts || 0)}</td>
          <td>${Number(user.averageScore || 0)}%</td>
          <td>${escapeHtml(user.lastSeen || "")}</td>
        </tr>
      `
    )
    .join("");
}

function bindDistricts() {
  $("#districtSearch").addEventListener("input", renderDistrictList);
  $("#addDistrictButton").addEventListener("click", () => {
    const id = uniqueId("district");
    state.districts.push({
      id,
      name: "Новый район",
      accentName: "",
      status: "draft",
      heroImage: "",
      history: "",
      facts: ["Новый факт"],
      images: []
    });
    selectedDistrictId = id;
    saveState("Район добавлен");
  });

  $("#districtForm").addEventListener("submit", (event) => {
    event.preventDefault();
    const district = getSelectedDistrict();
    if (!district) return;
    district.name = $("#districtName").value.trim();
    district.accentName = $("#districtAccentName").value.trim();
    district.status = $("#districtStatus").value;
    district.heroImage = $("#districtHeroImage").value.trim();
    district.history = $("#districtHistory").value.trim();
    district.facts = $$("#factsEditor input").map((input) => input.value.trim()).filter(Boolean);
    district.images = $$("#imagesEditor input").map((input) => input.value.trim()).filter(Boolean);
    saveState("Район сохранен");
  });

  $("#deleteDistrictButton").addEventListener("click", () => {
    if (!selectedDistrictId) return;
    state.districts = state.districts.filter((item) => item.id !== selectedDistrictId);
    state.costumes = state.costumes.filter((item) => item.districtId !== selectedDistrictId);
    state.questions = state.questions.filter((item) => item.districtId !== selectedDistrictId);
    selectedDistrictId = state.districts[0]?.id || null;
    saveState("Район удален");
  });

  $("#addFactButton").addEventListener("click", () => addInlineField("#factsEditor", "Новый факт"));
  $("#addImageButton").addEventListener("click", () => addInlineField("#imagesEditor", "assets/districts/image.jpg"));
}

function renderDistrictList() {
  const query = $("#districtSearch").value?.trim().toLowerCase() || "";
  const districts = state.districts.filter((item) => item.name.toLowerCase().includes(query));
  $("#districtList").innerHTML = districts
    .map((district) => itemCard(district, selectedDistrictId, district.status, district.heroImage))
    .join("");
  $$("#districtList .item-card").forEach((button) => {
    button.addEventListener("click", () => {
      selectedDistrictId = button.dataset.id;
      renderAll();
    });
  });
}

function renderDistrictForm() {
  const district = getSelectedDistrict();
  setDisabled("#districtForm", !district);
  if (!district) return;

  $("#districtEditorTitle").textContent = district.name;
  $("#districtName").value = district.name || "";
  $("#districtAccentName").value = district.accentName || "";
  $("#districtStatus").value = district.status || "draft";
  $("#districtHeroImage").value = district.heroImage || "";
  $("#districtHistory").value = district.history || "";
  renderInlineFields("#factsEditor", district.facts || []);
  renderInlineFields("#imagesEditor", district.images || []);
}

function getSelectedDistrict() {
  return state.districts.find((item) => item.id === selectedDistrictId);
}

function bindCostumes() {
  $("#addCostumeButton").addEventListener("click", () => {
    const id = uniqueId("costume");
    state.costumes.push({
      id,
      districtId: selectedDistrictId || state.districts[0]?.id || "",
      name: "Новый элемент",
      category: "Головной убор",
      color: "#cc0000",
      image: "",
      model: "",
      description: ""
    });
    selectedCostumeId = id;
    saveState("Элемент костюма добавлен");
  });

  $("#costumeForm").addEventListener("submit", (event) => {
    event.preventDefault();
    const costume = getSelectedCostume();
    if (!costume) return;
    costume.name = $("#costumeName").value.trim();
    costume.districtId = $("#costumeDistrict").value;
    costume.category = $("#costumeCategory").value;
    costume.color = $("#costumeColor").value;
    costume.image = $("#costumeImage").value.trim();
    costume.model = $("#costumeModel").value.trim();
    costume.description = $("#costumeDescription").value.trim();
    saveState("Элемент костюма сохранен");
  });

  $("#deleteCostumeButton").addEventListener("click", () => {
    state.costumes = state.costumes.filter((item) => item.id !== selectedCostumeId);
    selectedCostumeId = state.costumes[0]?.id || null;
    saveState("Элемент костюма удален");
  });
}

function renderCostumeList() {
  $("#costumeList").innerHTML = state.costumes
    .map((costume) => {
      const district = state.districts.find((item) => item.id === costume.districtId);
      return itemCard(
        { ...costume, name: costume.name },
        selectedCostumeId,
        `${costume.category} · ${district?.name || "Без района"}`,
        costume.image,
        costume.color
      );
    })
    .join("");
  $$("#costumeList .item-card").forEach((button) => {
    button.addEventListener("click", () => {
      selectedCostumeId = button.dataset.id;
      renderAll();
    });
  });
}

function renderCostumeForm() {
  const costume = getSelectedCostume();
  setDisabled("#costumeForm", !costume);
  if (!costume) return;

  $("#costumeEditorTitle").textContent = costume.name;
  $("#costumeName").value = costume.name || "";
  $("#costumeDistrict").value = costume.districtId || "";
  $("#costumeCategory").value = costume.category || "Головной убор";
  $("#costumeColor").value = costume.color || "#cc0000";
  $("#costumeImage").value = costume.image || "";
  $("#costumeModel").value = costume.model || "";
  $("#costumeDescription").value = costume.description || "";
}

function getSelectedCostume() {
  return state.costumes.find((item) => item.id === selectedCostumeId);
}

function bindQuestions() {
  $("#questionDistrictFilter").addEventListener("change", renderQuestionList);
  $("#addQuestionButton").addEventListener("click", () => {
    const id = uniqueId("question");
    state.questions.push({
      id,
      districtId: selectedDistrictId || state.districts[0]?.id || "",
      type: "single",
      text: "Новый вопрос",
      answers: ["Первый ответ", "Второй ответ"],
      correctIndex: 0,
      points: 5,
      explanation: ""
    });
    selectedQuestionId = id;
    saveState("Вопрос добавлен");
  });

  $("#questionForm").addEventListener("submit", (event) => {
    event.preventDefault();
    const question = getSelectedQuestion();
    if (!question) return;
    question.districtId = $("#questionDistrict").value;
    question.type = $("#questionType").value;
    question.points = Number($("#questionPoints").value || 1);
    question.text = $("#questionText").value.trim();
    question.answers = $$("#answersEditor .answer-input").map((input) => input.value.trim()).filter(Boolean);
    const selected = $("#answersEditor input[type='radio']:checked");
    question.correctIndex = selected ? Number(selected.value) : 0;
    question.explanation = $("#questionExplanation").value.trim();
    saveState("Вопрос сохранен");
  });

  $("#deleteQuestionButton").addEventListener("click", () => {
    state.questions = state.questions.filter((item) => item.id !== selectedQuestionId);
    selectedQuestionId = state.questions[0]?.id || null;
    saveState("Вопрос удален");
  });

  $("#addAnswerButton").addEventListener("click", () => {
    const question = getSelectedQuestion();
    const answers = question?.answers || [];
    answers.push("Новый ответ");
    renderAnswers(answers, question?.correctIndex || 0);
  });
}

function renderQuestionList() {
  const filter = $("#questionDistrictFilter").value || "all";
  const questions = state.questions.filter((item) => filter === "all" || item.districtId === filter);
  $("#questionList").innerHTML = questions
    .map((question) => {
      const district = state.districts.find((item) => item.id === question.districtId);
      return itemCard(
        { ...question, name: question.text },
        selectedQuestionId,
        `${district?.name || "Без района"} · ${question.points} баллов`
      );
    })
    .join("");
  $$("#questionList .item-card").forEach((button) => {
    button.addEventListener("click", () => {
      selectedQuestionId = button.dataset.id;
      renderAll();
    });
  });
}

function renderQuestionForm() {
  const question = getSelectedQuestion();
  setDisabled("#questionForm", !question);
  if (!question) return;

  $("#questionEditorTitle").textContent = question.text;
  $("#questionDistrict").value = question.districtId || "";
  $("#questionType").value = question.type || "single";
  $("#questionPoints").value = question.points || 5;
  $("#questionText").value = question.text || "";
  $("#questionExplanation").value = question.explanation || "";
  renderAnswers(question.answers || [], question.correctIndex || 0);
}

function renderAnswers(answers, correctIndex) {
  $("#answersEditor").innerHTML = answers
    .map(
      (answer, index) => `
        <div class="inline-row answer-row">
          <input class="answer-radio" type="radio" name="correctAnswer" value="${index}" ${index === correctIndex ? "checked" : ""} aria-label="Верный ответ" />
          <input class="answer-input" value="${escapeAttribute(answer)}" />
          <button class="remove-button" type="button" aria-label="Удалить ответ">×</button>
        </div>
      `
    )
    .join("");
  $$("#answersEditor .remove-button").forEach((button) => {
    button.addEventListener("click", () => {
      button.closest(".inline-row").remove();
      $$("#answersEditor input[type='radio']").forEach((radio, index) => {
        radio.value = index;
      });
    });
  });
}

function getSelectedQuestion() {
  return state.questions.find((item) => item.id === selectedQuestionId);
}

function renderDistrictOptions() {
  const options = state.districts
    .map((district) => `<option value="${district.id}">${escapeHtml(district.name)}</option>`)
    .join("");
  $("#costumeDistrict").innerHTML = options;
  $("#questionDistrict").innerHTML = options;
  $("#questionDistrictFilter").innerHTML = `<option value="all">Все районы</option>${options}`;
}

function bindExchange() {
  $("#downloadJsonButton").addEventListener("click", downloadJson);
  $("#copyJsonButton").addEventListener("click", async () => {
    renderJsonArea();
    await navigator.clipboard.writeText($("#jsonArea").value);
    showToast("JSON скопирован");
  });
  $("#importJsonButton").addEventListener("click", () => importFromText($("#jsonArea").value));
  $("#restoreSeedButton").addEventListener("click", () => {
    state = structuredClone(seedData);
    selectedDistrictId = state.districts[0]?.id || null;
    selectedCostumeId = state.costumes[0]?.id || null;
    selectedQuestionId = state.questions[0]?.id || null;
    saveState("Примерные данные восстановлены");
  });
  $("#importFileInput").addEventListener("change", async (event) => {
    const file = event.target.files?.[0];
    if (!file) return;
    const text = await file.text();
    importFromText(text);
    event.target.value = "";
  });
}

function renderJsonArea() {
  $("#jsonArea").value = JSON.stringify(state, null, 2);
}

function renderJsonPreview() {
  const rows = [
    ["Районы", state.districts.length],
    ["Элементы костюмов", state.costumes.length],
    ["Вопросы", state.questions.length],
    ["Пользователи", state.users.length],
    ["Точек активности", state.activity.length]
  ];
  $("#jsonPreview").innerHTML = rows
    .map(
      ([label, value]) => `
        <div class="preview-line">
          <strong>${label}</strong>
          <span>${value}</span>
        </div>
      `
    )
    .join("");
}

function downloadJson() {
  const blob = new Blob([JSON.stringify(state, null, 2)], { type: "application/json;charset=utf-8" });
  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.download = "turquoise-ring-admin-data.json";
  link.click();
  URL.revokeObjectURL(url);
  showToast("JSON экспортирован");
}

function importFromText(text) {
  try {
    const parsed = normalizeData(JSON.parse(text));
    state = parsed;
    selectedDistrictId = state.districts[0]?.id || null;
    selectedCostumeId = state.costumes[0]?.id || null;
    selectedQuestionId = state.questions[0]?.id || null;
    saveState("JSON импортирован");
  } catch {
    showToast("JSON не загружен: проверьте формат");
  }
}

function renderInlineFields(selector, values) {
  $(selector).innerHTML = values.map((value) => inlineField(value)).join("");
  bindInlineRemovers(selector);
}

function addInlineField(selector, value) {
  $(selector).insertAdjacentHTML("beforeend", inlineField(value));
  bindInlineRemovers(selector);
}

function inlineField(value) {
  return `
    <div class="inline-row">
      <input value="${escapeAttribute(value)}" />
      <button class="remove-button" type="button" aria-label="Удалить строку">×</button>
    </div>
  `;
}

function bindInlineRemovers(selector) {
  $$(`${selector} .remove-button`).forEach((button) => {
    button.onclick = () => button.closest(".inline-row").remove();
  });
}

function itemCard(item, activeId, note = "", image = "", color = "") {
  const initials = item.name
    .split(/\s+/)
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0])
    .join("")
    .toUpperCase();
  const thumbStyle = color ? `style="background:${escapeAttribute(color)}"` : "";
  const media = image
    ? `<img src="${escapeAttribute(image)}" alt="" onerror="this.remove()" />`
    : escapeHtml(initials || "БК");

  return `
    <button class="item-card ${item.id === activeId ? "active" : ""}" type="button" data-id="${item.id}">
      <span class="thumb" ${thumbStyle}>${media}</span>
      <span>
        <strong>${escapeHtml(item.name || "Без названия")}</strong>
        <small>${escapeHtml(note || "")}</small>
      </span>
    </button>
  `;
}

function setDisabled(formSelector, disabled) {
  $$(formSelector + " input, " + formSelector + " textarea, " + formSelector + " select, " + formSelector + " button").forEach(
    (element) => {
      element.disabled = disabled;
    }
  );
}

function uniqueId(prefix) {
  return `${prefix}-${Date.now().toString(36)}-${Math.random().toString(36).slice(2, 7)}`;
}

function showToast(message) {
  const toast = $("#toast");
  toast.textContent = message;
  toast.classList.add("show");
  clearTimeout(showToast.timer);
  showToast.timer = setTimeout(() => toast.classList.remove("show"), 2300);
}

function escapeHtml(value) {
  return String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

function escapeAttribute(value) {
  return escapeHtml(value).replaceAll("`", "&#096;");
}
