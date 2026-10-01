package com.hotel.Hotel.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

// Sub-DTO sin referencia al cliente: corta el ciclo Cliente -> Reservas -> Cliente al serializar.
public record ReservaItemResponse(
        UUID idReserva,
        String numeroHabitacion,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        String estado,
        double costoTotal) {
}
