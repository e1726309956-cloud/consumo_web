package com.uisrael.pedidosweb.model.dto.response;

import java.util.Date;
import lombok.Data;

@Data
public class EntregaResponseDto {
    private int idEntrega;
    private int idPedido;
    private int idEstado;
    private String tipoEntrega;
    private String recibidoPor;
    private String observacion;
    private Date fechaEntrega;
}