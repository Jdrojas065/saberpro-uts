package com.parcial.app.service;

import com.parcial.app.model.Docente;
import com.parcial.app.model.Simulacro;
import com.parcial.app.repository.SimulacroRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SimulacroServiceImpl implements SimulacroService {

    private final SimulacroRepository repo;

    public SimulacroServiceImpl(SimulacroRepository repo) { this.repo = repo; }

    @Override public List<Simulacro> findByDocente(Docente d)         { return repo.findByDocenteAndActivoTrueOrderByFechaCreacionDesc(d); }
    @Override public long            countActivosByDocente(Docente d)  { return repo.countByDocenteAndActivoTrue(d); }
    @Override public Simulacro       save(Simulacro s)                 { return repo.save(s); }
}