package com.hotel.Hotel.service;

import com.hotel.Hotel.domain.Habitacion;
import com.hotel.Hotel.dto.request.CrearHabitacionEstandarRequest;
import com.hotel.Hotel.dto.request.CrearSuitePresidencialRequest;
import com.hotel.Hotel.dto.response.HabitacionResponse;
import com.hotel.Hotel.mapper.HabitacionMapper;
import com.hotel.Hotel.repository.HabitacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HabitacionService {

    private final HabitacionRepository habitacionRepository;
    private final HabitacionMapper habitacionMapper;

    public HabitacionService(HabitacionRepository habitacionRepository, HabitacionMapper habitacionMapper) {
        this.habitacionRepository = habitacionRepository;
        this.habitacionMapper = habitacionMapper;
    }

    @Transactional
    public HabitacionResponse crearEstandar(CrearHabitacionEstandarRequest request) {
        return guardar(habitacionMapper.toEntity(request));
    }

    @Transactional
    public HabitacionResponse crearSuite(CrearSuitePresidencialRequest request) {
        return guardar(habitacionMapper.toEntity(request));
    }

    // Consulta polimorfica: con herencia JOINED, Hibernate devuelve instancias concretas
    // (HabitacionEstandar o SuitePresidencial) y el mapper elige la respuesta exacta.
    @Transactional(readOnly = true)
    public List<HabitacionResponse> listarTodas() {
        return habitacionMapper.toResponseList(habitacionRepository.findAllByOrderByNumeroAsc());
    }

    private HabitacionResponse guardar(Habitacion habitacion) {
        if (habitacionRepository.findByNumero(habitacion.getNumero()).isPresent()) {
            throw new IllegalStateException("Ya existe una habitación con el número " + habitacion.getNumero());
        }
        return habitacionMapper.toResponse(habitacionRepository.save(habitacion));
    }
}
