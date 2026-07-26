package com.uisrael.pedidosweb.modelo.dt.response;

import lombok.Data;

@Data
public class EstadosGeneralesResponseDto {
    private int idEstado;
    private String nombreEstado;
    private String descripcion;
    private boolean estado;
}