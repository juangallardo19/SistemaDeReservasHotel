package com.hotel.Hotel.mapper;

import com.hotel.Hotel.domain.Reserva;
import com.hotel.Hotel.dto.response.ReservaItemResponse;
import com.hotel.Hotel.dto.response.ReservaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservaMapper {

    @Mapping(target = "estado", expression = "java(reserva.getEstado().name())")
    @Mapping(target = "nombreHuesped", source = "cliente.nombre")
    @Mapping(target = "habitacionNumero", source = "habitacion.numero")
    @Mapping(target = "fechaInicio", expression = "java(reserva.getPeriodo().fechaInicio().toLocalDate())")
    @Mapping(target = "fechaFin", expression = "java(reserva.getPeriodo().fechaFin().toLocalDate())")
    ReservaResponse toResponse(Reserva reserva);

    List<ReservaResponse> toResponseList(List<Reserva> reservas);

    // Proyeccion plana para el resumen del cliente: nunca referencia al cliente
    @Mapping(target = "idReserva", source = "id")
    @Mapping(target = "numeroHabitacion", source = "habitacion.numero")
    @Mapping(target = "fechaInicio", source = "periodo.fechaInicio")
    @Mapping(target = "fechaFin", source = "periodo.fechaFin")
    ReservaItemResponse toItemResponse(Reserva reserva);
}
