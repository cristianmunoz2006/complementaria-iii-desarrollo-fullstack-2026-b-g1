package com.recetario;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas del backend: login con JWT, endpoints protegidos (incluye los
 * casos de error 401) y CORS. Correr con: mvn test
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    private String loginBody(String username, String password) {
        return "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
    }

    private String loginAndGetToken() throws Exception {
        String body = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody("admin", "admin123")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return mapper.readTree(body).get("token").asText();
    }

    @Test
    void loginConCredencialesValidasDevuelveToken() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    // --- casos de error ---

    @Test
    void loginConCredencialesInvalidasDevuelve401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody("admin", "clave-incorrecta")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void endpointProtegidoSinTokenDevuelve401() throws Exception {
        mockMvc.perform(get("/api/recipes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void endpointProtegidoConTokenInvalidoDevuelve401() throws Exception {
        mockMvc.perform(get("/api/recipes")
                        .header("Authorization", "Bearer token.falso.invalido"))
                .andExpect(status().isUnauthorized());
    }

    // --- flujo correcto de extremo a extremo ---

    @Test
    void endpointProtegidoConTokenValidoDevuelve200() throws Exception {
        String token = loginAndGetToken();

        mockMvc.perform(get("/api/recipes")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // --- CORS ---

    @Test
    void preflightCorsPermiteElOrigenDelFrontend() throws Exception {
        mockMvc.perform(options("/api/recipes")
                        .header("Origin", "http://localhost:8081")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Access-Control-Request-Headers", "Authorization"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:8081"));
    }
}
