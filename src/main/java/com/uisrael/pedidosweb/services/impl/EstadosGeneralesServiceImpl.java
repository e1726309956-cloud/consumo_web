package com.uisrael.pedidosweb.services.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.uisrael.pedidosweb.model.dto.request.EstadosGeneralesRequestDto;
import com.uisrael.pedidosweb.model.dto.response.EstadosGeneralesResponseDto;
import com.uisrael.pedidosweb.services.IEstadosGeneralesService;

@Service
public class EstadosGeneralesServiceImpl implements IEstadosGeneralesService {

    private final WebClient webclient;

    public EstadosGeneralesServiceImpl(WebClient webclient) {
        this.webclient = webclient;
    }

    @Override
    public List<EstadosGeneralesResponseDto> listarEstadosGenerales() {
        return webclient.get()
                .uri("/estadosgenerales")
                .retrieve()
                .bodyToFlux(EstadosGeneralesResponseDto.class)
                .collectList()
                .block();
    }

    @Override
    public Void guardarEstadoGeneral(EstadosGeneralesRequestDto nuevo) {
        if (nuevo.getIdEstado() > 0) {
            // Actualizar registro mediante PUT pasándole el ID en la URL
            webclient.put()
                    .uri("/estadosgenerales/{id}", nuevo.getIdEstado())
                    .bodyValue(nuevo)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } else {
            // Crear nuevo registro mediante POST
            webclient.post()
                    .uri("/estadosgenerales")
                    .bodyValue(nuevo)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        }
        return null;
    }

    @Override
    public void eliminarEstadoGeneral(int id) {
        webclient.delete()
                .uri("/estadosgenerales/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    @Override
    public EstadosGeneralesResponseDto obtenerPorId(int id) {
        return webclient.get()
                .uri("/estadosgenerales/{id}", id)
                .retrieve()
                .bodyToMono(EstadosGeneralesResponseDto.class)
                .block();
    }
}