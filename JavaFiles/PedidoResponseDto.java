package com.uisrael.pedidosweb.modelo.dt.response;

import java.util.Date;

import lombok.Data;
@Data
public class PedidoResponseDto {
	private int idPedido;
	private Date fechaPedido;
	private Date fechaEntrega;
	private String direccionEntrega;
	private String observacion;
	private Double total;
	private int idEstado;
	private int idUsuario;
}
