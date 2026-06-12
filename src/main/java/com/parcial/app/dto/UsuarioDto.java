package com.parcial.app.dto;

import lombok.Data;

@Data
public class UsuarioDto {
    private Long   id;
    private String nombre;
    private String apellido;
    private String email;
    private String rol;
    private boolean activo;
}