package com.uisrael.pedidosweb.model.dto.request;

import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.Data;

@Data
public class ComprobantePagoRequestDto {
    private int idComprobante;
    private double monto;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fechaPago;
    private String metodoPago;
    private String urlComprobante;
    private int idPedido;
}