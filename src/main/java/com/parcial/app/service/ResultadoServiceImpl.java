package com.parcial.app.service;

import com.parcial.app.model.Estudiante;
import com.parcial.app.model.Resultado;
import com.parcial.app.repository.ResultadoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ResultadoServiceImpl implements ResultadoService {

    private final ResultadoRepository repo;

    public ResultadoServiceImpl(ResultadoRepository repo) { this.repo = repo; }

    @Override public List<Resultado>     findByEstudiante(Estudiante e) { return repo.findByEstudianteOrderByFechaDesc(e); }
    @Override public Optional<Resultado> findUltimo(Estudiante e)       { return repo.findFirstByEstudianteOrderByFechaDesc(e); }
    @Override public Resultado           save(Resultado r)              { return repo.save(r); }
}