package com.uisrael.pedidosweb.model.dto.response;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class UsuarioResponseDto {

    private int idUsuario;
    private String cedula;
    private String nombre;
    private String apellido;
    private String correo;
    private String celular;
    private String estado;
    private Date fechaRegistro;
    private List<RolResponseDto> roles;
}