package com.uisrael.pedidosweb.services;

import java.util.List;
import com.uisrael.pedidosweb.modelo.dt.request.ComprobantePagoRequestDto;
import com.uisrael.pedidosweb.modelo.dt.response.ComprobantePagoResponseDto;

public interface IComprobantePagoService {
    List<ComprobantePagoResponseDto> listarComprobantesPago();
    Void guardarComprobantePago(ComprobantePagoRequestDto nuevo);
}