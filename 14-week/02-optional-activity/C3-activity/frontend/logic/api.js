"use strict";

/**
 * Funciones puras para hablar con la API (login y recetas).
 * Reciben `fetchFn` como parámetro para poder probarlas sin servidor real
 * (ver test/api.test.js). En el navegador se usa el fetch normal.
 */

const DEFAULT_BASE_URL = "http://localhost:8080";

async function request(fetchFn, url, options) {
  try {
    return await fetchFn(url, options);
  } catch (err) {
    // fetch solo lanza error si no hubo respuesta: backend apagado o CORS bloqueado
    throw new Error(
      "No se pudo conectar con el servidor. Verifica que el backend esté corriendo."
    );
  }
}

/** POST /api/auth/login -> devuelve el token JWT. */
async function login(username, password, options = {}) {
  const { baseUrl = DEFAULT_BASE_URL, fetchFn = fetch } = options;

  const response = await request(fetchFn, `${baseUrl}/api/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password }),
  });

  if (response.status === 401) {
    throw new Error("Usuario o contraseña incorrectos");
  }
  if (!response.ok) {
    throw new Error(`Error del servidor (${response.status})`);
  }

  const data = await response.json();
  return data.token;
}

/** GET /api/recipes con el token en el header Authorization. */
async function getRecipes(token, options = {}) {
  const { baseUrl = DEFAULT_BASE_URL, fetchFn = fetch } = options;

  const response = await request(fetchFn, `${baseUrl}/api/recipes`, {
    headers: { Authorization: `Bearer ${token}` },
  });

  if (response.status === 401) {
    const error = new Error("Tu sesión expiró o no estás autorizado. Inicia sesión de nuevo.");
    error.status = 401;
    throw error;
  }
  if (!response.ok) {
    throw new Error(`Error del servidor (${response.status})`);
  }

  return response.json();
}

/** Tiempo total (preparación + cocción) de una receta, en minutos. */
function totalMinutes(recipe) {
  return (recipe.prepTimeMinutes || 0) + (recipe.cookTimeMinutes || 0);
}

// Funciona en el navegador (window.Api) y en Node (require, para las pruebas)
if (typeof module !== "undefined" && module.exports) {
  module.exports = { login, getRecipes, totalMinutes };
} else {
  window.Api = { login, getRecipes, totalMinutes };
}
