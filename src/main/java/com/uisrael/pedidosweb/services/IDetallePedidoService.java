package com.uisrael.pedidosweb.services;

import java.util.List;
import com.uisrael.pedidosweb.modelo.dt.request.DetallePedidoRequestDto;
import com.uisrael.pedidosweb.modelo.dt.response.DetallePedidoResponseDto;

public interface IDetallePedidoService {
    List<DetallePedidoResponseDto> listarDetallePedido();
    void guardarDetallePedido(DetallePedidoRequestDto nuevo);
    DetallePedidoResponseDto buscarPorId(int id);
    void eliminarDetallePedido(int id);
}
