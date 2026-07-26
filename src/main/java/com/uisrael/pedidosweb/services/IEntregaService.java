package com.uisrael.pedidosweb.services;

import java.util.List;
import com.uisrael.pedidosweb.modelo.dt.request.EntregaRequestDto;
import com.uisrael.pedidosweb.modelo.dt.response.EntregaResponseDto;

public interface IEntregaService {
    List<EntregaResponseDto> listarEntregas();
    Void guardarEntrega(EntregaRequestDto nuevo);
}