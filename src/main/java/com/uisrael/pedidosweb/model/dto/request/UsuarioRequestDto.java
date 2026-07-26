package com.uisrael.pedidosweb.model.dto.request;

import lombok.Data;

@Data
public class UsuarioRequestDto {

    private String cedula;
    private String nombre;
    private String apellido;
    private String correo;
    private String contrasena;
    private String celular;
}