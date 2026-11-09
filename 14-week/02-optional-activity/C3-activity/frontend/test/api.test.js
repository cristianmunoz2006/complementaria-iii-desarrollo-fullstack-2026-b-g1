"use strict";

const { test } = require("node:test");
const assert = require("node:assert/strict");
const { login, getRecipes, totalMinutes } = require("../logic/api.js");

/** fetch falso: responde con el status y el body indicados, y guarda la petición recibida. */
function fakeFetch(status, body) {
  const calls = [];
  const fn = async (url, options) => {
    calls.push({ url, options });
    return { ok: status >= 200 && status < 300, status, json: async () => body };
  };
  fn.calls = calls;
  return fn;
}

test("login devuelve el token cuando las credenciales son correctas", async () => {
  const fetchFn = fakeFetch(200, { token: "abc.def.ghi" });

  const token = await login("admin", "admin123", { fetchFn });

  assert.equal(token, "abc.def.ghi");
  assert.equal(fetchFn.calls[0].url, "http://localhost:8080/api/auth/login");
  assert.equal(fetchFn.calls[0].options.method, "POST");
  assert.deepEqual(JSON.parse(fetchFn.calls[0].options.body), {
    username: "admin",
    password: "admin123",
  });
});

// --- casos de error ---

test("login lanza un error claro con credenciales incorrectas (401)", async () => {
  const fetchFn = fakeFetch(401, { message: "Usuario o contraseña incorrectos" });

  await assert.rejects(
    () => login("admin", "mala", { fetchFn }),
    { message: "Usuario o contraseña incorrectos" }
  );
});

test("login lanza un error claro si el backend no responde", async () => {
  const fetchFn = async () => {
    throw new TypeError("Failed to fetch");
  };

  await assert.rejects(
    () => login("admin", "admin123", { fetchFn }),
    /No se pudo conectar con el servidor/
  );
});

test("getRecipes envía el token en el header Authorization y devuelve la lista", async () => {
  const recipes = [{ id: 1, name: "Pasta Carbonara" }];
  const fetchFn = fakeFetch(200, recipes);

  const result = await getRecipes("mi-token", { fetchFn });

  assert.deepEqual(result, recipes);
  assert.equal(fetchFn.calls[0].options.headers.Authorization, "Bearer mi-token");
});

test("getRecipes con token vencido (401) lanza un error con status 401", async () => {
  const fetchFn = fakeFetch(401, null);

  await assert.rejects(
    () => getRecipes("token-vencido", { fetchFn }),
    (err) => err.status === 401
  );
});

test("totalMinutes suma preparación y cocción", () => {
  assert.equal(totalMinutes({ prepTimeMinutes: 10, cookTimeMinutes: 15 }), 25);
  assert.equal(totalMinutes({ prepTimeMinutes: 20, cookTimeMinutes: 0 }), 20);
});
