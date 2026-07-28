package com.uisrael.pedidosweb.model.dto.request;

import lombok.Data;

@Data
public class CambiarEstadoPedidoRequestDto {
	private int idEstado;
    private int idUsuario;
    private String observacion;
}
