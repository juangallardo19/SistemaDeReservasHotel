package com.hotel.Hotel.controller;

import com.hotel.Hotel.dto.request.CrearHabitacionEstandarRequest;
import com.hotel.Hotel.dto.request.CrearSuitePresidencialRequest;
import com.hotel.Hotel.dto.response.HabitacionResponse;
import com.hotel.Hotel.service.HabitacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

// Un solo controlador para todas las habitaciones: el tipo concreto se distingue por
// el DTO de entrada y por el discriminador "tipo" en la salida.
@RestController
@RequestMapping("/api/habitaciones")
public class HabitacionController {

    private final HabitacionService habitacionService;

    public HabitacionController(HabitacionService habitacionService) {
        this.habitacionService = habitacionService;
    }

    @PostMapping("/estandar")
    public ResponseEntity<HabitacionResponse> crearEstandar(@RequestBody CrearHabitacionEstandarRequest request,
            UriComponentsBuilder uriBuilder) {
        return creada(habitacionService.crearEstandar(request), uriBuilder);
    }

    @PostMapping("/suites")
    public ResponseEntity<HabitacionResponse> crearSuite(@RequestBody CrearSuitePresidencialRequest request,
            UriComponentsBuilder uriBuilder) {
        return creada(habitacionService.crearSuite(request), uriBuilder);
    }

    @GetMapping
    public ResponseEntity<List<HabitacionResponse>> listar() {
        return ResponseEntity.ok(habitacionService.listarTodas());
    }

    private ResponseEntity<HabitacionResponse> creada(HabitacionResponse response, UriComponentsBuilder uriBuilder) {
        URI uri = uriBuilder.path("/api/habitaciones/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }
}
