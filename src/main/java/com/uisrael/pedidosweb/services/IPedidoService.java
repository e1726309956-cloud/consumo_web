package com.uisrael.pedidosweb.services;

import java.util.List;

import com.uisrael.pedidosweb.modelo.dt.request.PedidoRequestDto;
import com.uisrael.pedidosweb.modelo.dt.response.PedidoResponseDto;

public interface IPedidoService {
	
	List<PedidoResponseDto> listarpedido();
	Void guardarpedido(PedidoRequestDto nuevo);

}
