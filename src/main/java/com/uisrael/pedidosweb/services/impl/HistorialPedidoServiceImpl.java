package com.uisrael.pedidosweb.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.uisrael.pedidosweb.modelo.dt.request.HistorialPedidoRequestDto;
import com.uisrael.pedidosweb.modelo.dt.response.HistorialPedidoResponseDto;
import com.uisrael.pedidosweb.services.IHistorialPedidoService;

@Service
public class HistorialPedidoServiceImpl implements IHistorialPedidoService {

	private final WebClient webclient;

	public HistorialPedidoServiceImpl(WebClient webclient) {
		this.webclient = webclient;
	}
	
	@Override
	public List<HistorialPedidoResponseDto> listarhistorialpedido() {
		// TODO Auto-generated method stub
		return webclient.get().uri("/historialpedidos").retrieve()
				.bodyToFlux(HistorialPedidoResponseDto.class).collectList().block();
	}

	@Override
	public Void guardarhistorialpedido(HistorialPedidoRequestDto nuevo) {
		webclient.post().uri("/historialpedidos").bodyValue(nuevo).retrieve().toBodilessEntity().block();
		return null;
	}

}
