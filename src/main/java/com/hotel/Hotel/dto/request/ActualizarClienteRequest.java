package com.hotel.Hotel.dto.request;

// Ambos campos son opcionales: null significa "no modificar".
// No expone id, activo, penalizaciones ni reservas (proteccion contra Mass Assignment).
public record ActualizarClienteRequest(
        String nombre,
        String email) {
}
