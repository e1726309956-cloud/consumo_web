package com.uisrael.pedidosweb.services.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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
        try {
            return webClient.get()
                    .uri("/entregas")
                    .retrieve()
                    .bodyToFlux(EntregaResponseDto.class)
                    .collectList()
                    .block();
        } catch (WebClientResponseException e) {
            System.err.println(">>> ERROR AL LISTAR ENTREGAS: " + e.getStatusCode());
            System.err.println(">>> DETALLE: " + e.getResponseBodyAsString());
            throw e;
        }
    }

    @Override
    public Void guardarEntrega(EntregaRequestDto dto) {
        try {
            webClient.post()
                    .uri("/entregas")
                    .bodyValue(dto)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            return null;
        } catch (WebClientResponseException e) {
            System.err.println("=================================================");
            System.err.println(">>> ERROR EN EL BACKEND AL GUARDAR ENTREGA: " + e.getStatusCode());
            System.err.println(">>> DETALLE DEL ERROR: " + e.getResponseBodyAsString());
            System.err.println("=================================================");
            throw e;
        }
    }
}