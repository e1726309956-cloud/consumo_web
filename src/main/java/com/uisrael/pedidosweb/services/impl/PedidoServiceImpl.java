package com.uisrael.pedidosweb.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.uisrael.pedidosweb.modelo.dt.request.PedidoRequestDto;
import com.uisrael.pedidosweb.modelo.dt.response.PedidoResponseDto;
import com.uisrael.pedidosweb.services.IPedidoService;

@Service
public class PedidoServiceImpl implements IPedidoService {
	
	private final WebClient webclient;
	
	public PedidoServiceImpl(WebClient webclient) {
		this.webclient = webclient;
	}
	
	@Override
	public List<PedidoResponseDto> listarpedido() {
		// TODO Auto-generated method stub
		return webclient.get().uri("/pedidos").retrieve()
				.bodyToFlux(PedidoResponseDto.class).collectList().block();
	}

	@Override
	public Void guardarpedido(PedidoRequestDto nuevo) {
		webclient.post().uri("/pedidos").bodyValue(nuevo).retrieve().toBodilessEntity().block();
		return null;
	}


}
