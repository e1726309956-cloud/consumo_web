package com.uisrael.pedidosweb.services;

import java.util.List;

import com.uisrael.pedidosweb.model.dto.request.HistorialPedidoRequestDto;
import com.uisrael.pedidosweb.model.dto.response.HistorialPedidoResponseDto;

public interface IHistorialPedidoService {
	List<HistorialPedidoResponseDto> listarhistorialpedido();
	Void guardarhistorialpedido(HistorialPedidoRequestDto nuevo);

}
