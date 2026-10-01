package com.hotel.Hotel.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

public record CrearReservaRequest(
        UUID clienteId,
        UUID habitacionId,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin) {
}
