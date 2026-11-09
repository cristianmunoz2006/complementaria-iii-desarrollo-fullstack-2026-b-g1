"use strict";

// Interfaz: login -> guarda el token -> pide las recetas con el token.
// La comunicación con la API vive en logic/api.js (window.Api).

const TOKEN_KEY = "recetario_token";

const loginView = document.getElementById("login-view");
const dataView = document.getElementById("data-view");
const loginForm = document.getElementById("login-form");
const loginError = document.getElementById("login-error");
const loginBtn = document.getElementById("login-btn");
const logoutBtn = document.getElementById("logout-btn");

const loadingEl = document.getElementById("loading");
const dataErrorEl = document.getElementById("data-error");
const emptyEl = document.getElementById("empty");
const listEl = document.getElementById("recipe-list");

function showView(name) {
  loginView.classList.toggle("d-none", name !== "login");
  dataView.classList.toggle("d-none", name !== "data");
}

// Estados de la vista de datos: loading | error | empty | data
function setDataState(name) {
  loadingEl.classList.toggle("d-none", name !== "loading");
  dataErrorEl.classList.toggle("d-none", name !== "error");
  emptyEl.classList.toggle("d-none", name !== "empty");
  listEl.classList.toggle("d-none", name !== "data");
}

function renderRecipes(recipes) {
  listEl.innerHTML = "";
  recipes.forEach((recipe) => {
    const li = document.createElement("li");
    li.className = "list-group-item";

    const left = document.createElement("div");
    const name = document.createElement("strong");
    name.textContent = recipe.name;
    const meta = document.createElement("div");
    meta.className = "recipe-meta";
    meta.textContent = `${recipe.cuisine} · ${recipe.difficulty}`;
    left.append(name, meta);

    const time = document.createElement("span");
    time.className = "badge badge-success";
    time.textContent = `${Api.totalMinutes(recipe)} min`;

    li.append(left, time);
    listEl.appendChild(li);
  });
}

async function loadRecipes() {
  showView("data");
  setDataState("loading");

  try {
    const recipes = await Api.getRecipes(sessionStorage.getItem(TOKEN_KEY));
    if (recipes.length === 0) {
      setDataState("empty");
      return;
    }
    renderRecipes(recipes);
    setDataState("data");
  } catch (err) {
    if (err.status === 401) {
      // token vencido o inválido: volver al login
      logout();
      showLoginError(err.message);
      return;
    }
    dataErrorEl.textContent = err.message;
    setDataState("error");
  }
}

function showLoginError(message) {
  loginError.textContent = message;
  loginError.classList.remove("d-none");
}

function logout() {
  sessionStorage.removeItem(TOKEN_KEY);
  listEl.innerHTML = "";
  showView("login");
}

loginForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  loginError.classList.add("d-none");
  loginBtn.disabled = true;

  try {
    const token = await Api.login(
      document.getElementById("username").value,
      document.getElementById("password").value
    );
    sessionStorage.setItem(TOKEN_KEY, token);
    loginForm.reset();
    await loadRecipes();
  } catch (err) {
    showLoginError(err.message);
  } finally {
    loginBtn.disabled = false;
  }
});

logoutBtn.addEventListener("click", logout);

// Si ya había una sesión abierta, entrar directo a la vista de datos
if (sessionStorage.getItem(TOKEN_KEY)) {
  loadRecipes();
}
