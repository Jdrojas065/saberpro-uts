package com.parcial.app.service;

import com.parcial.app.model.Estudiante;
import com.parcial.app.model.enums.EstadoInscripcion;
import com.parcial.app.repository.EstudianteRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class EstudianteServiceImpl implements EstudianteService {

    private final EstudianteRepository repo;

    public EstudianteServiceImpl(EstudianteRepository repo) { this.repo = repo; }

 // EstudianteServiceImpl.java
    @Override 
    public List<Estudiante> findAll() { 
        return repo.findAllConDetalle(); // ← ¿está así o sigue siendo repo.findAll()?
    }
    @Override public Optional<Estudiante>  findById(Long id)                 { return repo.findById(id); }
    @Override public Optional<Estudiante>  findByEmail(String email)         { return repo.findByUsuarioEmail(email); }
    @Override public Estudiante            save(Estudiante e)                { return repo.save(e); }
    @Override public long                  countByEstado(EstadoInscripcion s){ return repo.countByEstadoInscripcion(s); }
}