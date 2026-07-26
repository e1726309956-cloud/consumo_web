package com.uisrael.pedidosweb.model.dto.response;

import java.util.Date;

import lombok.Data;


@Data
public class ProductoResponseDto {
	
	private int idProducto;
	private CategoriaResponseDto categoria;
	private String nombre;
	private String descripcion;
	private Double precio;
	private int stock;
	private String imagenUrl;
	private boolean disponible;
	private Date fechaCreacion;
	
	
	
	
}
