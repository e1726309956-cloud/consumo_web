package com.uisrael.pedidosweb.model.dto.response;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class EntregaResponseDto {
	private int idEntrega;
    private int idPedido;
    private int idEstado;

    private String nombreEstado;
    private String tipoEntrega;

    private String nombreCliente;
    private String apellidoCliente;
    private String celularCliente;
    private String correoCliente;

    private String direccionEntrega;

    private Date fechaPedido;
    private Date fechaEntregaSolicitada;
    private Date fechaEntregaReal;

    private Double totalPedido;

    private String recibidoPor;
    private String evidenciaEntregaUrl;
    private String observacion;
    
    private String nombreProducto;
    private String imagenUrl;

    private List<DetallePedidoResponseDto> detalles;
}