package com.uisrael.pedidosweb.services.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.uisrael.pedidosweb.modelo.dt.request.EntregaRequestDto;
import com.uisrael.pedidosweb.modelo.dt.response.EntregaResponseDto;
import com.uisrael.pedidosweb.services.IEntregaService;

@Service
public class EntregaServiceImpl implements IEntregaService {

    private final WebClient webClient;

    public EntregaServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<EntregaResponseDto> listarEntregas() {
        return webClient.get().uri("/entregas").retrieve()
                .bodyToFlux(EntregaResponseDto.class).collectList().block();
    }

    @Override
    public Void guardarEntrega(EntregaRequestDto nuevo) {
        webClient.post().uri("/entregas").bodyValue(nuevo).retrieve().toBodilessEntity().block();
        return null;
    }
}