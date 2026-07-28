package com.uisrael.pedidosweb.services;

import java.util.List;

import com.uisrael.pedidosweb.model.dto.request.EntregaRequestDto;
import com.uisrael.pedidosweb.model.dto.response.EntregaResponseDto;

public interface IEntregaService {
    List<EntregaResponseDto> listarEntregas();
    Void guardarEntrega(EntregaRequestDto nuevo);
}