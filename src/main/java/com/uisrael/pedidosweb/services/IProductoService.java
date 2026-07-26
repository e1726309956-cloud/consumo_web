package com.uisrael.pedidosweb.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.uisrael.pedidosweb.model.dto.request.ProductoRequestDto;
import com.uisrael.pedidosweb.model.dto.response.ProductoResponseDto;

public interface IProductoService {

	List<ProductoResponseDto> listarProducto();

	void guardarProducto(ProductoRequestDto producto, MultipartFile imagen);

	void inactivarProducto(int idProducto);

	void activarProducto(int idProducto);

	ProductoResponseDto buscarPorId(int idProducto);

	void actualizarProducto(ProductoRequestDto producto, MultipartFile imagen);
	
	List<ProductoResponseDto> buscarPorCategoria(int idCategoria);
}
