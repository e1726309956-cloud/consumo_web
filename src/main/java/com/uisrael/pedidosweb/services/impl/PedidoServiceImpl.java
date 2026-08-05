package com.uisrael.pedidosweb.services.impl;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.uisrael.pedidosweb.model.dto.request.CambiarEstadoPedidoRequestDto;
import com.uisrael.pedidosweb.model.dto.request.PedidoRequestDto;
import com.uisrael.pedidosweb.model.dto.response.ErrorApiResponseDto;
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
	public PedidoResponseDto guardarpedido(PedidoRequestDto pedido) {

		return webClient.post().uri("/pedidos").bodyValue(pedido).retrieve()

				.onStatus(status -> status.value() == 409,
						response -> response.bodyToMono(ErrorApiResponseDto.class).flatMap(error -> {

							String mensaje = error.getMensaje() != null && !error.getMensaje().isBlank()
									? error.getMensaje()
									: "Uno de los productos ya no tiene stock suficiente.";

							return reactor.core.publisher.Mono.error(new RuntimeException(mensaje));
						}))

				.onStatus(status -> status.is4xxClientError(),
						response -> response.bodyToMono(ErrorApiResponseDto.class).flatMap(error -> {

							String mensaje = error.getMensaje() != null && !error.getMensaje().isBlank()
									? error.getMensaje()
									: "No fue posible generar el pedido.";

							return reactor.core.publisher.Mono.error(new RuntimeException(mensaje));
						}))

				.onStatus(status -> status.is5xxServerError(),
						response -> response.bodyToMono(ErrorApiResponseDto.class)
								.defaultIfEmpty(new ErrorApiResponseDto()).flatMap(error -> {

									String mensaje = error.getMensaje() != null && !error.getMensaje().isBlank()
											? error.getMensaje()
											: "El servidor no pudo procesar el pedido. Intente nuevamente.";

									return reactor.core.publisher.Mono.error(new RuntimeException(mensaje));
								}))

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

	@Override
	public PedidoResponseDto cambiarEstado(int idPedido, CambiarEstadoPedidoRequestDto request) {

		return webClient.put().uri("/pedidos/{idPedido}/estado", idPedido).bodyValue(request).retrieve()
				.bodyToMono(PedidoResponseDto.class).block();
	}
}