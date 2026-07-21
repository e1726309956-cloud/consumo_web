package com.uisrael.pedidosweb.services;

import java.util.List;

import com.uisrael.pedidosweb.modelo.dt.request.DetallePedidoRequestDto;
import com.uisrael.pedidosweb.modelo.dt.response.DetallePedidoResponseDto;

public interface IDetallePedidoService {
	List<DetallePedidoResponseDto> listardetallepedido();
	Void guardarDetallePedido(DetallePedidoRequestDto nuevo);

}
