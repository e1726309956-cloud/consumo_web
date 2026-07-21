package com.uisrael.pedidosweb.services;

import java.util.List;

import com.uisrael.pedidosweb.model.dto.request.CategoriaRequestDto;
import com.uisrael.pedidosweb.model.dto.response.CategoriaResponseDto;

public interface ICategoriaService {
	
	List<CategoriaResponseDto> listarCategorias();
	void guardarCategoria(CategoriaRequestDto categoria);
}
