package com.uisrael.pedidosweb.services.impl;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException; // <-- Importante

import com.uisrael.pedidosweb.model.dto.request.CambiarEstadoComprobanteRequestDto;
import com.uisrael.pedidosweb.model.dto.request.ComprobantePagoRequestDto;
import com.uisrael.pedidosweb.model.dto.response.ComprobantePagoResponseDto;
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
			return webClient.get().uri("/comprobantes-pago").retrieve().bodyToFlux(ComprobantePagoResponseDto.class)
					.collectList().block();
		} catch (WebClientResponseException e) {
			System.err.println(">>> ERROR AL LISTAR COMPROBANTES: " + e.getStatusCode());
			System.err.println(">>> DETALLE: " + e.getResponseBodyAsString());
			throw e;
		}
	}

	@Override
	public Void guardarComprobantePago(ComprobantePagoRequestDto nuevo) {
		try {
			webClient.post().uri("/comprobantes-pago").bodyValue(nuevo).retrieve().toBodilessEntity().block();
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

	@Override
	public ComprobantePagoResponseDto buscarPorPedido(int idPedido) {

		try {

			return webClient.get().uri("/comprobantes-pago/pedido/{idPedido}", idPedido).retrieve()
					.bodyToMono(ComprobantePagoResponseDto.class).block();

		} catch (WebClientResponseException e) {

			if (e.getStatusCode().value() == 404 || e.getStatusCode().value() == 204) {

				return null;
			}

			throw e;
		}
	}

	@Override
	public ComprobantePagoResponseDto subirComprobante(int idPedido, MultipartFile archivo, String tipoPago,
			Double monto, String observacion) {

		MultipartBodyBuilder multipart = new MultipartBodyBuilder();

		multipart.part("archivo", archivo.getResource());

		multipart.part("tipoPago", tipoPago);

		multipart.part("monto", monto.toString());

		if (observacion != null && !observacion.isBlank()) {

			multipart.part("observacion", observacion);
		}

		return webClient.post().uri("/pedidos/{idPedido}/comprobante", idPedido)
				.contentType(MediaType.MULTIPART_FORM_DATA).body(BodyInserters.fromMultipartData(multipart.build()))
				.retrieve().bodyToMono(ComprobantePagoResponseDto.class).block();
	}

	@Override
	public List<ComprobantePagoResponseDto> listarPorPedido(int idPedido) {

		return webClient.get().uri("/comprobantes-pago/pedido/{idPedido}/lista", idPedido).retrieve()
				.bodyToFlux(ComprobantePagoResponseDto.class).collectList().block();
	}

	@Override
	public ComprobantePagoResponseDto cambiarEstado(int idComprobante, CambiarEstadoComprobanteRequestDto request) {

		return webClient.put().uri("/comprobantes-pago/{idComprobante}/estado", idComprobante).bodyValue(request)
				.retrieve().bodyToMono(ComprobantePagoResponseDto.class).block();
	}
}