package com.uisrael.pedidosweb.model.dto.request;
import lombok.Data;

@Data
public class CategoriaRequestDto {
	
	private int idCategoria;

	private String nombre;
	private String descripcion;
	private boolean estado;
	

}
