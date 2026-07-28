package com.uisrael.pedidosweb.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.uisrael.pedidosweb.model.dto.request.CambiarEstadoComprobanteRequestDto;
import com.uisrael.pedidosweb.model.dto.request.ComprobantePagoRequestDto;
import com.uisrael.pedidosweb.model.dto.response.ComprobantePagoResponseDto;

public interface IComprobantePagoService {
	List<ComprobantePagoResponseDto> listarComprobantesPago();

	Void guardarComprobantePago(ComprobantePagoRequestDto nuevo);

	ComprobantePagoResponseDto buscarPorPedido(int idPedido);

	ComprobantePagoResponseDto subirComprobante(int idPedido, MultipartFile archivo, String tipoPago, Double monto,
			String observacion);

	List<ComprobantePagoResponseDto> listarPorPedido(int idPedido);

	ComprobantePagoResponseDto cambiarEstado(int idComprobante, CambiarEstadoComprobanteRequestDto request);
}