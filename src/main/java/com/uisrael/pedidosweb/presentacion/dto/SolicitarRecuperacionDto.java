package com.uisrael.pedidosweb.presentacion.dto;

public class SolicitarRecuperacionDto {

	private String correo;

	public SolicitarRecuperacionDto() {
	}

	public SolicitarRecuperacionDto(String correo) {
		this.correo = correo;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

}
