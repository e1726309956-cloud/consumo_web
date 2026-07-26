package com.uisrael.pedidosweb.modelo.dt.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EntregaRequestDto {

    @JsonProperty("idPedido")
    private Integer idPedido;

    @JsonProperty("idEstado")
    private Integer idEstado;

    @JsonProperty("tipoEntrega")
    private String tipoEntrega;

    @JsonProperty("recibidoPor")
    private String recibidoPor;

    @JsonProperty("observacion")
    private String observacion;

    @JsonProperty("evidenciaEntregaUrl")
    private String evidenciaEntregaUrl;

    // Getters y Setters
    public Integer getIdPedido() { return idPedido; }
    public void setIdPedido(Integer idPedido) { this.idPedido = idPedido; }

    public Integer getIdEstado() { return idEstado; }
    public void setIdEstado(Integer idEstado) { this.idEstado = idEstado; }

    public String getTipoEntrega() { return tipoEntrega; }
    public void setTipoEntrega(String tipoEntrega) { this.tipoEntrega = tipoEntrega; }

    public String getRecibidoPor() { return recibidoPor; }
    public void setRecibidoPor(String recibidoPor) { this.recibidoPor = recibidoPor; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }

    public String getEvidenciaEntregaUrl() { return evidenciaEntregaUrl; }
    public void setEvidenciaEntregaUrl(String evidenciaEntregaUrl) { this.evidenciaEntregaUrl = evidenciaEntregaUrl; }
}