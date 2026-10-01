package com.hotel.Hotel.mapper;

import com.hotel.Hotel.domain.Habitacion;
import com.hotel.Hotel.domain.HabitacionEstandar;
import com.hotel.Hotel.domain.SuitePresidencial;
import com.hotel.Hotel.dto.request.CrearHabitacionEstandarRequest;
import com.hotel.Hotel.dto.request.CrearSuitePresidencialRequest;
import com.hotel.Hotel.dto.response.HabitacionEstandarResponse;
import com.hotel.Hotel.dto.response.HabitacionResponse;
import com.hotel.Hotel.dto.response.SuitePresidencialResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HabitacionMapper {

    // Request -> Entidad: MapStruct invoca el constructor de dominio (con sus validaciones)
    HabitacionEstandar toEntity(CrearHabitacionEstandarRequest request);

    @Mapping(target = "mayordomo", source = "incluyeMayordomo")
    @Mapping(target = "jacuzzi", source = "jacuzziPrivado")
    SuitePresidencial toEntity(CrearSuitePresidencialRequest request);

    // Entidad -> Response polimorfico: MapStruct genera un instanceof por subclase
    // y delega en el metodo concreto, sin perder camasIndividuales ni jacuzziPrivado.
    @SubclassMapping(source = HabitacionEstandar.class, target = HabitacionEstandarResponse.class)
    @SubclassMapping(source = SuitePresidencial.class, target = SuitePresidencialResponse.class)
    @BeanMapping(subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
    HabitacionResponse toResponse(Habitacion habitacion);

    HabitacionEstandarResponse toEstandarResponse(HabitacionEstandar habitacion);

    SuitePresidencialResponse toSuiteResponse(SuitePresidencial habitacion);

    List<HabitacionResponse> toResponseList(List<Habitacion> habitaciones);
}
