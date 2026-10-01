package com.hotel.Hotel.dto.request;

public record CrearSuitePresidencialRequest(
        String numero,
        double precioPorNoche,
        int capacidadMaxima,
        boolean incluyeMayordomo,
        boolean jacuzziPrivado) {
}
