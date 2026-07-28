package com.uisrael.pedidosweb.services.impl;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.uisrael.pedidosweb.model.dto.request.CambiarEstadoEntregaRequestDto;
import com.uisrael.pedidosweb.model.dto.response.EntregaResponseDto;
import com.uisrael.pedidosweb.services.IEntregaService;

@Service
public class EntregaServiceImpl implements IEntregaService {

	private final WebClient webClient;

	public EntregaServiceImpl(WebClient webClient) {

		this.webClient = webClient;
	}

	@Override
	public List<EntregaResponseDto> listarActivas() {

		return webClient.get().uri("/entregas/activas").retrieve().bodyToFlux(EntregaResponseDto.class).collectList().block();
	}

	@Override
	public EntregaResponseDto buscarPorId(int idEntrega) {

		return webClient.get().uri("/entregas/{idEntrega}", idEntrega).retrieve().bodyToMono(EntregaResponseDto.class)
				.block();
	}

	@Override
	public EntregaResponseDto cambiarEstado(int idEntrega, int idEstado, String observacion) {

		CambiarEstadoEntregaRequestDto request = new CambiarEstadoEntregaRequestDto();

		request.setIdEstado(idEstado);

		request.setObservacion(observacion != null ? observacion.trim() : null);

		return webClient.put().uri("/entregas/{idEntrega}/estado", idEntrega).bodyValue(request).retrieve()
				.bodyToMono(EntregaResponseDto.class).block();
	}

	@Override
	public EntregaResponseDto finalizarEntrega(int idEntrega, MultipartFile evidencia, String recibidoPor,
			String observacion) {

		MultipartBodyBuilder builder = new MultipartBodyBuilder();

		builder.part("evidencia", evidencia.getResource());

		builder.part("recibidoPor", recibidoPor);

		if (observacion != null && !observacion.isBlank()) {

			builder.part("observacion", observacion.trim());
		}

		return webClient.post().uri("/entregas/{idEntrega}/finalizar", idEntrega)
				.contentType(MediaType.MULTIPART_FORM_DATA).body(BodyInserters.fromMultipartData(builder.build()))
				.retrieve().bodyToMono(EntregaResponseDto.class).block();
	}

	@Override
	public List<EntregaResponseDto> listarTodos() {

		return webClient.get().uri("/entregas").retrieve().bodyToFlux(EntregaResponseDto.class).collectList().block();
	}
}