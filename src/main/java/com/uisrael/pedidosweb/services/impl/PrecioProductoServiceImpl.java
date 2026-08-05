package com.uisrael.pedidosweb.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.uisrael.pedidosweb.model.dto.request.PrecioProductoRequestDto;
import com.uisrael.pedidosweb.model.dto.response.PrecioProductoResponseDto;
import com.uisrael.pedidosweb.services.IPrecioProductoService;


@Service
public class PrecioProductoServiceImpl implements IPrecioProductoService {

	private final WebClient webClient;

	public PrecioProductoServiceImpl(WebClient webClient) {

		this.webClient = webClient;
	}

	@Override
	public PrecioProductoResponseDto registrarPrecio(int idProducto, Double precio) {

		PrecioProductoRequestDto request = new PrecioProductoRequestDto();

		request.setPrecio(precio);

		return webClient.post().uri("/precios-productos/producto/{idProducto}", idProducto).bodyValue(request)
				.retrieve().bodyToMono(PrecioProductoResponseDto.class).block();
	}

	@Override
	public PrecioProductoResponseDto obtenerActivo(int idProducto) {

		return webClient.get().uri("/precios-productos/producto/{idProducto}/activo", idProducto).retrieve()
				.bodyToMono(PrecioProductoResponseDto.class).block();
	}

	@Override
	public List<PrecioProductoResponseDto> listarHistorial(int idProducto) {

		return webClient.get().uri("/precios-productos/producto/{idProducto}/historial", idProducto).retrieve()
				.bodyToFlux(PrecioProductoResponseDto.class).collectList().block();
	}
}