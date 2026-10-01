package com.hotel.Hotel.dto.request;

public record CrearHabitacionEstandarRequest(
        String numero,
        double precioPorNoche,
        int capacidadMaxima,
        int camasIndividuales) {
}
