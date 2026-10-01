package com.hotel.Hotel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Recorre las 4 pruebas del Taller 7 contra la API completa
// (controladores + servicios + MapStruct + JPA) usando H2 en memoria.
@SpringBootTest
@AutoConfigureMockMvc
class Taller7IntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper json;

    @Test
    void flujoCompletoDelTaller() throws Exception {
        // Prueba 1: creacion de suite presidencial -> 201 con ID generado
        String suiteId = crear("/api/habitaciones/suites", """
                { "numero": "P05-501", "precioPorNoche": 350.0, "capacidadMaxima": 2,
                  "jacuzziPrivado": true, "incluyeMayordomo": true }""")
                .get("id").asText();

        String estandarId = crear("/api/habitaciones/estandar", """
                { "numero": "P02-201", "precioPorNoche": 120.0, "capacidadMaxima": 3,
                  "camasIndividuales": 2 }""")
                .get("id").asText();

        // Prueba 2: consulta polimorfica con discriminador "tipo" y campos propios
        mvc.perform(get("/api/habitaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].tipo").value("ESTANDAR"))
                .andExpect(jsonPath("$[0].camasIndividuales").value(2))
                .andExpect(jsonPath("$[1].tipo").value("SUITE"))
                .andExpect(jsonPath("$[1].jacuzziPrivado").value(true))
                .andExpect(jsonPath("$[1].incluyeMayordomo").value(true));

        String clienteId = crear("/api/clientes", """
                { "nombre": "Santiago Ospina", "email": "santiago@hotel.com" }""")
                .get("id").asText();

        // 2 noches en suite (700) + 3 noches en estandar (360) = 1060
        crear("/api/reservas", """
                { "clienteId": "%s", "habitacionId": "%s",
                  "fechaInicio": "2026-12-01T15:00:00", "fechaFin": "2026-12-03T15:00:00" }"""
                .formatted(clienteId, suiteId));
        crear("/api/reservas", """
                { "clienteId": "%s", "habitacionId": "%s",
                  "fechaInicio": "2026-12-10T15:00:00", "fechaFin": "2026-12-13T15:00:00" }"""
                .formatted(clienteId, estandarId));

        // Prueba 3: resumen con agregados calculados y lista embebida sin ciclos
        mvc.perform(get("/api/clientes/{id}/resumen", clienteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalReservasRealizadas").value(2))
                .andExpect(jsonPath("$.montoTotalGastado").value(1060.0))
                .andExpect(jsonPath("$.reservasRecientes", hasSize(2)))
                .andExpect(jsonPath("$.reservasRecientes[0].numeroHabitacion").value("P02-201"))
                .andExpect(jsonPath("$.reservasRecientes[0].estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.reservasRecientes[0]", not(hasKey("cliente"))));

        // Prueba 4: PATCH solo con nombre -> email, activo y penalizaciones intactos
        mvc.perform(patch("/api/clientes/{id}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "nombre": "Nuevo Nombre Modificado" }"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Nuevo Nombre Modificado"))
                .andExpect(jsonPath("$.email").value("santiago@hotel.com"))
                .andExpect(jsonPath("$.activo").value(true))
                .andExpect(jsonPath("$.penalizaciones").value(0));

        // Intento de Mass Assignment: los campos protegidos no existen en el DTO y se ignoran
        mvc.perform(patch("/api/clientes/{id}", clienteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "activo": false, "penalizaciones": 99 }"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(true))
                .andExpect(jsonPath("$.penalizaciones").value(0));

        // Verificacion releyendo desde la base de datos
        mvc.perform(get("/api/clientes/{id}", clienteId))
                .andExpect(jsonPath("$.nombre").value("Nuevo Nombre Modificado"))
                .andExpect(jsonPath("$.email").value("santiago@hotel.com"));
    }

    private JsonNode crear(String url, String body) throws Exception {
        String respuesta = mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return json.readTree(respuesta);
    }
}
