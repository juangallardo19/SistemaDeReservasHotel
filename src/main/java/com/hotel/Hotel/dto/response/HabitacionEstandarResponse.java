package com.hotel.Hotel.dto.response;

import java.util.UUID;

public record HabitacionEstandarResponse(
        UUID id,
        String numero,
        int capacidadMaxima,
        double precioPorNoche,
        String estado,
        int camasIndividuales) implements HabitacionResponse {
}
