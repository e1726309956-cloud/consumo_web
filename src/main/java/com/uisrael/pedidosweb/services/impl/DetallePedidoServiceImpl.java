package com.uisrael.pedidosweb.services.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.uisrael.pedidosweb.modelo.dt.request.DetallePedidoRequestDto;
import com.uisrael.pedidosweb.modelo.dt.response.DetallePedidoResponseDto;
import com.uisrael.pedidosweb.services.IDetallePedidoService;

@Service
public class DetallePedidoServiceImpl implements IDetallePedidoService {

    private final WebClient webclient;

    public DetallePedidoServiceImpl(WebClient webclient) {
        this.webclient = webclient;
    }

    @Override
    public List<DetallePedidoResponseDto> listarDetallePedido() {
        return webclient.get().uri("/detallepedidos").retrieve()
                .bodyToFlux(DetallePedidoResponseDto.class).collectList().block();
    }

    @Override
    public void guardarDetallePedido(DetallePedidoRequestDto nuevo) {
        webclient.post().uri("/detallepedidos").bodyValue(nuevo)
                .retrieve().toBodilessEntity().block();
    }

    @Override
    public DetallePedidoResponseDto buscarPorId(int id) {
        return webclient.get().uri("/detallepedidos/" + id).retrieve()
                .bodyToMono(DetallePedidoResponseDto.class).block();
    }

    @Override
    public void eliminarDetallePedido(int id) {
        webclient.delete().uri("/detallepedidos/" + id).retrieve()
                .toBodilessEntity().block();
    }
}
