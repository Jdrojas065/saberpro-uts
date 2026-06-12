package com.parcial.app.service;

import com.parcial.app.model.Estudiante;
import com.parcial.app.model.enums.EstadoInscripcion;
import java.util.List;
import java.util.Optional;

public interface EstudianteService {
    List<Estudiante> findAll();
    Optional<Estudiante> findById(Long id);
    Optional<Estudiante> findByEmail(String email);
    Estudiante save(Estudiante estudiante);
    long countByEstado(EstadoInscripcion estado);
}