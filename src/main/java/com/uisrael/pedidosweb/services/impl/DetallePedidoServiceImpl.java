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
	public List<DetallePedidoResponseDto> listardetallepedido() {
		return webclient.get().uri("/detallepedidos").retrieve()
				.bodyToFlux(DetallePedidoResponseDto.class).collectList().block();
	}

	@Override
	public Void guardarDetallePedido(DetallePedidoRequestDto nuevo) {
		webclient.post().uri("/detallepedidos").bodyValue(nuevo).retrieve().toBodilessEntity().block();
		return null;
	}

	
}
