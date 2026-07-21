package com.uisrael.pedidosweb.modelo.dt.response;

import lombok.Data;

@Data
public class DetallePedidoResponseDto {
	private int idDetallePedido;
	private int cantidad;
	private double precioUnitario;
	private double subtotal;
	private int idProducto;
	private int idPedido;
	

}
