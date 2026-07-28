package com.uisrael.pedidosweb.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.uisrael.pedidosweb.model.dto.request.PedidoRequestDto;
import com.uisrael.pedidosweb.model.dto.response.PedidoResponseDto;

public interface IPedidoService {

	List<PedidoResponseDto> listarpedido();

	PedidoResponseDto guardarpedido(PedidoRequestDto nuevo);

	void subirComprobante(int idPedido, MultipartFile comprobante, Double monto);

	List<PedidoResponseDto> listarPorUsuario(int idUsuario);

	PedidoResponseDto buscarPorId(int idPedido);
}
