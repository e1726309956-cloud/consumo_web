package com.uisrael.pedidosweb.modelo.dt.request;

import java.time.LocalDate;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class HistorialPedidoRequestDto {
	private int idHistorial;
	private int idEstadoAnterior;
	private int idEstadoNuevo;
	private String observacion;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate fecha;
	private int usuarioModifica;
	private int idPedido;
	private int idUsuario;
	
}
