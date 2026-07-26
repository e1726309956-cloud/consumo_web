package com.uisrael.pedidosweb.modelo.dt.response;

import java.time.LocalDate;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
@Data
public class PedidoResponseDto {
	private int idPedido;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaPedido;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaEntrega;
	private String direccionEntrega;
	private String observacion;
	private Double total;
	private int idEstado;
	private int idUsuario;
}
