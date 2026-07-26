package com.uisrael.pedidosweb.modelo.dt.request;

import lombok.Data;

@Data
public class EstadosGeneralesRequestDto {
    private int idEstado;
    private String nombreEstado;
    private String descripcion;
    private boolean estado;
}