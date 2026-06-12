package com.parcial.app.service;

import com.parcial.app.model.Simulacro;
import com.parcial.app.model.Docente;
import java.util.List;

public interface SimulacroService {
    List<Simulacro> findByDocente(Docente docente);
    long countActivosByDocente(Docente docente);
    Simulacro save(Simulacro s);
}