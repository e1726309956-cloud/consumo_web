package com.uisrael.pedidosweb.services;

import java.util.List;

import com.uisrael.pedidosweb.model.dto.request.EstadosGeneralesRequestDto;
import com.uisrael.pedidosweb.model.dto.response.EstadosGeneralesResponseDto;

public interface IEstadosGeneralesService {
    List<EstadosGeneralesResponseDto> listarEstadosGenerales();
    Void guardarEstadoGeneral(EstadosGeneralesRequestDto nuevo);
    

    void eliminarEstadoGeneral(int id);
    EstadosGeneralesResponseDto obtenerPorId(int id);
}