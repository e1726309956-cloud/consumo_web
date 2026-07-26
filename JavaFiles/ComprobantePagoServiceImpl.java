package com.uisrael.pedidosweb.services.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.uisrael.pedidosweb.modelo.dt.request.ComprobantePagoRequestDto;
import com.uisrael.pedidosweb.modelo.dt.response.ComprobantePagoResponseDto;
import com.uisrael.pedidosweb.services.IComprobantePagoService;

@Service
public class ComprobantePagoServiceImpl implements IComprobantePagoService {

    private final WebClient webClient;

    public ComprobantePagoServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<ComprobantePagoResponseDto> listarComprobantesPago() {
        return webClient.get()
                .uri("/comprobantes-pago") // <-- SI DA 404, PRUEBA CAMBIAR SOLO ESTE TEXTO A: "/comprobantes" O "/comprobantepago"
                .retrieve()
                .bodyToFlux(ComprobantePagoResponseDto.class)
                .collectList()
                .block();
    }

    @Override
    public Void guardarComprobantePago(ComprobantePagoRequestDto nuevo) {
        webClient.post()
                .uri("/comprobantes-pago") // <-- Mismo texto que arriba
                .bodyValue(nuevo)
                .retrieve()
                .toBodilessEntity()
                .block();
        return null;
    }
}