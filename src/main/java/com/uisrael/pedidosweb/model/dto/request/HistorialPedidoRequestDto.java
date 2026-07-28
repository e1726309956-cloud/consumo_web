package com.uisrael.pedidosweb.model.dto.request;

import java.util.Date;

import lombok.Data;

@Data
public class HistorialPedidoRequestDto {
	private int idHistorial;
	private int idEstadoAnterior;
	private int idEstadoNuevo;
	private String observacion;
	private Date fecha;
	private int usuarioModifica;
	private int idPedido;
	private int idUsuario;
	
}
