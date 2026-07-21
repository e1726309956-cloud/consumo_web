package com.uisrael.pedidosweb.services;

import java.util.List;

import com.uisrael.pedidosweb.model.dto.request.ProductoRequestDto;
import com.uisrael.pedidosweb.model.dto.response.ProductoResponseDto;

public interface IProductoService {
	
	List<ProductoResponseDto> listarProducto();
	void guardarProducto(ProductoRequestDto producto);
}
