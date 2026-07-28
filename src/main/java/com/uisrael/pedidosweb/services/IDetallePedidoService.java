package com.uisrael.pedidosweb.services;

import java.util.List;

import com.uisrael.pedidosweb.model.dto.request.DetallePedidoRequestDto;
import com.uisrael.pedidosweb.model.dto.response.DetallePedidoResponseDto;

public interface IDetallePedidoService {
	List<DetallePedidoResponseDto> listardetallepedido();
	Void guardarDetallePedido(DetallePedidoRequestDto nuevo);

}
