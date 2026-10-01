package com.hotel.Hotel.dto.response;

import java.util.UUID;

public record SuitePresidencialResponse(
        UUID id,
        String numero,
        int capacidadMaxima,
        double precioPorNoche,
        String estado,
        boolean incluyeMayordomo,
        boolean jacuzziPrivado) implements HabitacionResponse {
}
