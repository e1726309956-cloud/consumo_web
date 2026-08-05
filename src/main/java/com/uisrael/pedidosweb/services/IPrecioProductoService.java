package com.uisrael.pedidosweb.services;

import java.util.List;

import com.uisrael.pedidosweb.model.dto.response.PrecioProductoResponseDto;

public interface IPrecioProductoService {

	PrecioProductoResponseDto registrarPrecio(int idProducto, Double precio);

	PrecioProductoResponseDto obtenerActivo(int idProducto);

	List<PrecioProductoResponseDto> listarHistorial(int idProducto);

}
