package com.uisrael.pedidosweb.model.dto.request;

import java.util.Date;

import lombok.Data;

@Data
public class ProductoRequestDto {
	
	private int idProducto;
	
	private Integer categoria;
	private String nombre;
	private String descripcion;
	private Double precio;
	private int stock;
	private String imagenUrl;
	private boolean disponible;
	private Date fechaCreacion;
	


}
