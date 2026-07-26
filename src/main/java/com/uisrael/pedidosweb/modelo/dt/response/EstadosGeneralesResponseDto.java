package com.uisrael.pedidosweb.modelo.dt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EstadosGeneralesResponseDto {
    private int idEstado;
    
    @JsonProperty("nombre")  // <--- Esto lee "nombre" de la API Backend
    private String nombreEstado;
    
    private String descripcion;
    private boolean estado;
}