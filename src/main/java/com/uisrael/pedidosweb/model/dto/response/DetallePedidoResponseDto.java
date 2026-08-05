package com.uisrael.pedidosweb.model.dto.response;

import lombok.Data;

@Data
public class DetallePedidoResponseDto {
	private int idDetallePedido;
	private int cantidad;
	private double precioUnitario;
	private double subtotal;
	private int idProducto;
	private int idPedido;
	
	private String nombreProducto;
	private String imagenUrl;
	

}
