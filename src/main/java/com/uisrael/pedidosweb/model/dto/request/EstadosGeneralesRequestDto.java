package com.uisrael.pedidosweb.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EstadosGeneralesRequestDto {
    private int idEstado;

    @JsonProperty("nombre")
    private String nombreEstado;

    private String descripcion;
    private boolean estado = true;
}