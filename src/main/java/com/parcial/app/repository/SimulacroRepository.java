package com.parcial.app.repository;

import com.parcial.app.model.Docente;
import com.parcial.app.model.Simulacro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SimulacroRepository extends JpaRepository<Simulacro, Long> {
    List<Simulacro> findByDocenteAndActivoTrueOrderByFechaCreacionDesc(Docente docente);
    long countByDocenteAndActivoTrue(Docente docente);
    List<Simulacro> findByDocente(Docente docente);
}