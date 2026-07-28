package com.uisrael.pedidosweb.model.dto.request;

public class CambiarEstadoEntregaRequestDto {

	private int idEstado;
	private String observacion;

	public CambiarEstadoEntregaRequestDto() {
	}

	public int getIdEstado() {
		return idEstado;
	}

	public void setIdEstado(int idEstado) {
		this.idEstado = idEstado;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

}
