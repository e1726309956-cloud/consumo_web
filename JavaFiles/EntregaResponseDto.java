package com.uisrael.pedidosweb.modelo.dt.response;

import java.util.Date;
import lombok.Data;

@Data
public class EntregaResponseDto {
    private int idEntrega;
    private String direccionEntrega;
    private Date fechaEntrega;
    private String estadoEntrega;
    private int idPedido;
    private int idUsuario;
}