package com.uisrael.pedidosweb.services;

import com.uisrael.pedidosweb.model.dto.request.LoginRequestDto;
import com.uisrael.pedidosweb.model.dto.request.UsuarioRequestDto;
import com.uisrael.pedidosweb.model.dto.response.UsuarioResponseDto;

public interface IUsuarioService {

	UsuarioResponseDto iniciarSesion(LoginRequestDto login);

	UsuarioResponseDto registrarUsuario(UsuarioRequestDto usuario);
}