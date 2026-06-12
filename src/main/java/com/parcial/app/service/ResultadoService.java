package com.parcial.app.service;


import com.parcial.app.model.Resultado;
import com.parcial.app.model.Estudiante;
import java.util.List;
import java.util.Optional;

public interface ResultadoService {
    List<Resultado> findByEstudiante(Estudiante e);
    Optional<Resultado> findUltimo(Estudiante e);
    Resultado save(Resultado r);
}