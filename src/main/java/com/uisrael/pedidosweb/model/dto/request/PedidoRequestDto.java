package com.uisrael.pedidosweb.model.dto.request;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class PedidoRequestDto {

    private Date fechaEntrega;
    private String direccionEntrega;
    private String observacion;
    private int idUsuario;

    private List<DetallePedidoRequestDto> detalles;
}