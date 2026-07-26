package com.uisrael.pedidosweb.services;

import java.util.List;
import com.uisrael.pedidosweb.modelo.dt.request.HistorialPedidoRequestDto;
import com.uisrael.pedidosweb.modelo.dt.response.HistorialPedidoResponseDto;

public interface IHistorialPedidoService {
    List<HistorialPedidoResponseDto> listarHistorial();
    void guardarHistorial(HistorialPedidoRequestDto nuevo);
    HistorialPedidoResponseDto buscarPorId(int id);
    void eliminarHistorial(int id);
}
