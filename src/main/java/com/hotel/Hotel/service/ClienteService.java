package com.hotel.Hotel.service;

import com.hotel.Hotel.domain.Cliente;
import com.hotel.Hotel.dto.request.ActualizarClienteRequest;
import com.hotel.Hotel.dto.request.CrearClienteRequest;
import com.hotel.Hotel.dto.response.ClienteResponse;
import com.hotel.Hotel.dto.response.ClienteResumenResponse;
import com.hotel.Hotel.exception.RecursoNoEncontradoException;
import com.hotel.Hotel.mapper.ClienteMapper;
import com.hotel.Hotel.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    public ClienteService(ClienteRepository clienteRepository, ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    @Transactional
    public ClienteResponse crear(CrearClienteRequest request) {
        Cliente cliente = new Cliente(request.nombre(), request.email());
        Cliente guardado = clienteRepository.save(cliente);
        return clienteMapper.toResponse(guardado);
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listarTodos() {
        return clienteMapper.toResponseList(clienteRepository.findAll());
    }

    @Transactional(readOnly = true)
    public ClienteResponse obtenerPorId(UUID id) {
        return clienteMapper.toResponse(buscar(id));
    }

    // El mapeo ocurre dentro de la transaccion: las reservas (LAZY) y sus habitaciones
    // se cargan aqui y salen ya convertidas a DTO, sin exponer entidades al controlador.
    @Transactional(readOnly = true)
    public ClienteResumenResponse obtenerResumen(UUID id) {
        return clienteMapper.toResumenResponse(buscar(id));
    }

    @Transactional
    public ClienteResponse actualizarParcial(UUID id, ActualizarClienteRequest request) {
        // 1. Cargar la entidad desde la base de datos
        Cliente cliente = buscar(id);

        if (request.email() != null) {
            clienteRepository.findByEmail(request.email())
                    .filter(otro -> !otro.getId().equals(id))
                    .ifPresent(otro -> {
                        throw new IllegalStateException("El email " + request.email() + " ya está registrado");
                    });
        }

        // 2. Aplicar solo los campos no nulos (MapStruct + NullValuePropertyMappingStrategy.IGNORE)
        clienteMapper.updateClienteFromDto(request, cliente);

        // 3. Guardar y retornar la vista actualizada
        return clienteMapper.toResponse(clienteRepository.save(cliente));
    }

    private Cliente buscar(UUID id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con ID: " + id));
    }
}
