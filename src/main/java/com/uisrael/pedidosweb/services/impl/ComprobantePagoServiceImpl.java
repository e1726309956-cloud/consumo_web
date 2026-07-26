package com.uisrael.pedidosweb.services.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException; // <-- Importante

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
        try {
            return webClient.get()
                    .uri("/comprobantes-pago")
                    .retrieve()
                    .bodyToFlux(ComprobantePagoResponseDto.class)
                    .collectList()
                    .block();
        } catch (WebClientResponseException e) {
            System.err.println(">>> ERROR AL LISTAR COMPROBANTES: " + e.getStatusCode());
            System.err.println(">>> DETALLE: " + e.getResponseBodyAsString());
            throw e;
        }
    }

    @Override
    public Void guardarComprobantePago(ComprobantePagoRequestDto nuevo) {
        try {
            webClient.post()
                    .uri("/comprobantes-pago")
                    .bodyValue(nuevo)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            return null;
        } catch (WebClientResponseException e) {
            // 🚨 IMPRIME EL ERROR DETALLADO DEL PUERTO 8080 EN CONSOLA
            System.err.println("=================================================");
            System.err.println(">>> ERROR EN EL BACKEND AL GUARDAR (8080): " + e.getStatusCode());
            System.err.println(">>> DETALLE DE LA RESPUESTA DE ERROR: " + e.getResponseBodyAsString());
            System.err.println("=================================================");
            throw e;
        }
    }
}