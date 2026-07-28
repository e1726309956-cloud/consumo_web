package com.uisrael.pedidosweb.model.dto.response;

import java.util.Date;
import lombok.Data;

@Data
public class ComprobantePagoResponseDto {
	private int idComprobante;
    private int idPedido;
    private String tipoPago;
    private String archivoUrl;
    private Double monto;
    private Date fechaSubida;
    private int idEstado;
    private String observacion;
}