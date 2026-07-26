package com.uisrael.pedidosweb.services.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.uisrael.pedidosweb.modelo.dt.request.EstadosGeneralesRequestDto;
import com.uisrael.pedidosweb.modelo.dt.response.EstadosGeneralesResponseDto;
import com.uisrael.pedidosweb.services.IEstadosGeneralesService;

@Service
public class EstadosGeneralesServiceImpl implements IEstadosGeneralesService {

    private final WebClient webclient;

    public EstadosGeneralesServiceImpl(WebClient webclient) {
        this.webclient = webclient;
    }

    @Override
    public List<EstadosGeneralesResponseDto> listarEstadosGenerales() {
        return webclient.get().uri("/estadosgenerales").retrieve()
                .bodyToFlux(EstadosGeneralesResponseDto.class).collectList().block();
    }

    @Override
    public Void guardarEstadoGeneral(EstadosGeneralesRequestDto nuevo) {
        webclient.post().uri("/estadosgenerales").bodyValue(nuevo).retrieve().toBodilessEntity().block();
        return null;
    }
}