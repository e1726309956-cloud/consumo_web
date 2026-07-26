package com.uisrael.pedidosweb.modelo.dt.response;

import java.util.Date;
import lombok.Data;

@Data
public class ComprobantePagoResponseDto {
    private int idComprobante;
    private double monto;
    private Date fechaPago;
    private String metodoPago;
    private String urlComprobante;
    private int idPedido;
}