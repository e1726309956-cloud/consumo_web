package com.uisrael.pedidosweb.modelo.dt.request;

import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.Data;

@Data
public class EntregaRequestDto {
    private int idEntrega;
    private String direccionEntrega;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fechaEntrega;
    private String estadoEntrega;
    private int idPedido;
    private int idUsuario;
}