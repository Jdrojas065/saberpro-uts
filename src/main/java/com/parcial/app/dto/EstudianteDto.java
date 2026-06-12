package com.parcial.app.dto;

import lombok.Data;

@Data
public class EstudianteDto {
    private Long   id;
    private String nombre;
    private String apellido;
    private String email;
    private String documento;
    private String programa;
    private String estadoInscripcion;
    private String tipoExamen;
}