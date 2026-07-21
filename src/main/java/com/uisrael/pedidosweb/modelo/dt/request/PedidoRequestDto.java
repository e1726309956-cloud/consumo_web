package com.uisrael.pedidosweb.modelo.dt.request;

import java.util.Date;
import lombok.Data;

@Data
public class PedidoRequestDto {
	
	private int idPedido;
	private Date fechaPedido;
	private Date fechaEntrega;
	private String direccionEntrega;
	private String observacion;
	private Double total;
	private int idEstado;
	private int idUsuario;

}
