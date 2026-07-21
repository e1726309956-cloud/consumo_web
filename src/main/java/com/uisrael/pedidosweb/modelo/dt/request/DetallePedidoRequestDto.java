package com.uisrael.pedidosweb.modelo.dt.request;

import lombok.Data;

@Data
public class DetallePedidoRequestDto {

	private int idDetallePedido;
	private int cantidad;
	private double precioUnitario;
	private double subtotal;
	private int idProducto;
	private int idPedido;
	
}
