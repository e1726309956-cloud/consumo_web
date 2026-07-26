package com.uisrael.pedidosweb.services.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.uisrael.pedidosweb.model.dto.request.LoginRequestDto;
import com.uisrael.pedidosweb.model.dto.request.UsuarioRequestDto;
import com.uisrael.pedidosweb.model.dto.response.UsuarioResponseDto;
import com.uisrael.pedidosweb.services.IUsuarioService;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

    private final WebClient webClient;

    public UsuarioServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public UsuarioResponseDto iniciarSesion(
            LoginRequestDto login) {

        return webClient.post()
                .uri("/usuario/login")
                .bodyValue(login)
                .retrieve()
                .bodyToMono(UsuarioResponseDto.class)
                .block();
    }
    
    @Override
    public UsuarioResponseDto registrarUsuario(
            UsuarioRequestDto usuario) {

        return webClient.post()
                .uri("/usuario/registro")
                .bodyValue(usuario)
                .retrieve()
                .bodyToMono(UsuarioResponseDto.class)
                .block();
    }
}