package com.parcial.app.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ResultadoDto {
    private Long          id;
    private String        estudianteNombre;
    private String        simulacroNombre;
    private Integer       puntaje;
    private String        competencia;
    private LocalDateTime fecha;
}