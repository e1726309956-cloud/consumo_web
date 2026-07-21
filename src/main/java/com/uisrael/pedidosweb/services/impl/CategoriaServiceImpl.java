package com.uisrael.pedidosweb.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.uisrael.pedidosweb.model.dto.request.CategoriaRequestDto;
import com.uisrael.pedidosweb.model.dto.response.CategoriaResponseDto;
import com.uisrael.pedidosweb.services.ICategoriaService;

@Service
public class CategoriaServiceImpl implements ICategoriaService{
	
	private final WebClient webClient;

    public CategoriaServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

	@Override
	public List<CategoriaResponseDto> listarCategorias() {
		return webClient.get().uri("/categoria").retrieve().bodyToFlux(CategoriaResponseDto.class)
                .collectList()
                .block();
	}

	@Override
	public void guardarCategoria(CategoriaRequestDto categoria) {
		 webClient.post().uri("/categoria").bodyValue(categoria).retrieve()
         .toBodilessEntity()
         .block();
		
	}
	
}
