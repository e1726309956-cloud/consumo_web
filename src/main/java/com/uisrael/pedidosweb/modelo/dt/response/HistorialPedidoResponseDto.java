package com.uisrael.pedidosweb.modelo.dt.response;

import java.util.Date;

import lombok.Data;

@Data
public class HistorialPedidoResponseDto {

	private int idHistorial;
	private int idEstadoAnterior;
	private int idEstadoNuevo;
	private String observacion;
	private Date fecha;
	private int usuarioModifica;
	private int idPedido;
	private int idUsuario;
	
	
}
