package com.uisrael.pedidosweb.services;

import com.uisrael.pedidosweb.presentacion.dto.RestablecerPasswordDto;

public interface IRecuperacionPasswordService {

	String solicitarRecuperacion(String correo);

	boolean validarToken(String token);

	String restablecerPassword(RestablecerPasswordDto dto);

}
