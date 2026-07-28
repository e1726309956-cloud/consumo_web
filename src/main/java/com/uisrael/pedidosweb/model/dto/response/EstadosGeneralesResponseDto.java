package com.uisrael.pedidosweb.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EstadosGeneralesResponseDto {
    private int idEstado;

    @JsonProperty("nombre")
    private String nombreEstado;

    private String descripcion;
    private boolean estado;
}