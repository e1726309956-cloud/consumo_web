package com.uisrael.pedidosweb.model.dto.response;

import java.util.Date;
import java.util.List;

import lombok.Data;
@Data
public class PedidoResponseDto {
	private int idPedido;
	private Date fechaPedido;
	private Date fechaEntrega;
	private String direccionEntrega;
	private String observacion;
	private Double total;
	private String nombreUsuario;
	private String apellidoCliente;
	private int idEstado;
	private int idUsuario;
	private List<DetallePedidoResponseDto> detalles;
	private String nombreCliente;
	private String celularCliente;
	
	private String nombreEstado;
	private String tipoEstado;
}
