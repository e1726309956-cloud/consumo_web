package com.uisrael.pedidosweb.services.impl;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.uisrael.pedidosweb.model.dto.request.PedidoRequestDto;
import com.uisrael.pedidosweb.model.dto.response.PedidoResponseDto;
import com.uisrael.pedidosweb.services.IPedidoService;

@Service
public class PedidoServiceImpl implements IPedidoService {

	private final WebClient webClient;

	public PedidoServiceImpl(WebClient webClient) {
		this.webClient = webClient;
	}

	@Override
	public List<PedidoResponseDto> listarpedido() {

		return webClient.get().uri("/pedidos").retrieve().bodyToFlux(PedidoResponseDto.class).collectList().block();
	}

	@Override
	public PedidoResponseDto guardarpedido(PedidoRequestDto nuevo) {

		return webClient.post().uri("/pedidos").contentType(MediaType.APPLICATION_JSON).bodyValue(nuevo).retrieve()
				.bodyToMono(PedidoResponseDto.class).block();
	}

	@Override
	public void subirComprobante(int idPedido, MultipartFile comprobante, Double monto) {

		if (comprobante == null || comprobante.isEmpty()) {
			return;
		}

		MultipartBodyBuilder multipart = new MultipartBodyBuilder();

		multipart.part("archivo", comprobante.getResource());

		multipart.part("tipoPago", "ABONO_INICIAL");

		if (monto != null) {
			multipart.part("monto", monto.toString());
		}

		multipart.part("observacion", "Comprobante adjuntado al generar el pedido");

		webClient.post().uri("/pedidos/{idPedido}/comprobante", idPedido).contentType(MediaType.MULTIPART_FORM_DATA)
				.body(BodyInserters.fromMultipartData(multipart.build())).retrieve().toBodilessEntity().block();
	}

	@Override
	public List<PedidoResponseDto> listarPorUsuario(int idUsuario) {

		return webClient.get().uri("/pedidos/usuario/{idUsuario}", idUsuario).retrieve()
				.bodyToFlux(PedidoResponseDto.class).collectList().block();
	}

	@Override
	public PedidoResponseDto buscarPorId(int idPedido) {

		return webClient.get().uri("/pedidos/{idPedido}", idPedido).retrieve().bodyToMono(PedidoResponseDto.class)
				.block();
	}
}