package com.uisrael.pedidosweb.model.dto.request;

import lombok.Data;

@Data
public class LoginRequestDto {

    private String correo;
    private String contrasena;
}