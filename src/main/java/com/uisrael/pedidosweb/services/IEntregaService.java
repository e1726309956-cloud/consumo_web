package com.uisrael.pedidosweb.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.uisrael.pedidosweb.model.dto.response.EntregaResponseDto;

public interface IEntregaService {
	List<EntregaResponseDto> listarActivas();

	EntregaResponseDto buscarPorId(int idEntrega);

	EntregaResponseDto cambiarEstado(int idEntrega, int idEstado, String observacion);

	EntregaResponseDto finalizarEntrega(int idEntrega, MultipartFile evidencia, String recibidoPor, String observacion);
	
	List<EntregaResponseDto> listarTodos();
}