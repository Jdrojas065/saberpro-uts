package com.parcial.app.repository;

import com.parcial.app.model.Pregunta;
import com.parcial.app.model.Simulacro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PreguntaRepository extends JpaRepository<Pregunta, Long> {
    List<Pregunta> findBySimulacro(Simulacro simulacro);
    long countBySimulacro(Simulacro simulacro);
}