package com.hotel.Hotel.mapper;

import com.hotel.Hotel.domain.Cliente;
import com.hotel.Hotel.domain.Reserva;
import com.hotel.Hotel.dto.request.ActualizarClienteRequest;
import com.hotel.Hotel.dto.request.CrearClienteRequest;
import com.hotel.Hotel.dto.response.ClienteResponse;
import com.hotel.Hotel.dto.response.ClienteResumenResponse;
import com.hotel.Hotel.dto.response.ReservaItemResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Comparator;
import java.util.List;

@Mapper(componentModel = "spring", uses = ReservaMapper.class)
public interface ClienteMapper {

    ClienteResponse toResponse(Cliente cliente);

    List<ClienteResponse> toResponseList(List<Cliente> clientes);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "penalizaciones", ignore = true)
    @Mapping(target = "reservas", ignore = true)
    Cliente toEntity(CrearClienteRequest request);

    // ---------- Tarea 2: resumen ejecutivo con agregados calculados ----------

    @Mapping(target = "totalReservasRealizadas", expression = "java(cliente.getReservas().size())")
    @Mapping(target = "montoTotalGastado", source = "reservas", qualifiedByName = "calcularMontoTotal")
    @Mapping(target = "reservasRecientes", source = "reservas", qualifiedByName = "ordenarRecientes")
    ClienteResumenResponse toResumenResponse(Cliente cliente);

    @Named("calcularMontoTotal")
    default double calcularMontoTotal(List<Reserva> reservas) {
        return reservas.stream()
                .mapToDouble(Reserva::getCostoTotal)
                .sum();
    }

    // Historial de la reserva mas reciente a la mas antigua; cada elemento
    // se proyecta con ReservaMapper.toItemResponse (sin referencia al cliente).
    @Named("ordenarRecientes")
    default List<ReservaItemResponse> ordenarRecientes(List<Reserva> reservas) {
        List<Reserva> ordenadas = reservas.stream()
                .sorted(Comparator.comparing((Reserva r) -> r.getPeriodo().fechaInicio()).reversed())
                .toList();
        return toReservaItems(ordenadas);
    }

    List<ReservaItemResponse> toReservaItems(List<Reserva> reservas);

    // ---------- Tarea 3: actualizacion parcial segura (PATCH) ----------

    // IGNORE: si un campo del DTO llega null, se conserva el valor actual de la entidad.
    // Los campos sensibles se ignoran explicitamente para evitar Mass Assignment.
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "penalizaciones", ignore = true)
    @Mapping(target = "reservas", ignore = true)
    void updateClienteFromDto(ActualizarClienteRequest dto, @MappingTarget Cliente entity);
}
