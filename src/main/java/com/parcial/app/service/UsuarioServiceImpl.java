package com.parcial.app.service;

import com.parcial.app.model.Usuario;
import com.parcial.app.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository repo;

    public UsuarioServiceImpl(UsuarioRepository repo) { this.repo = repo; }

    @Override public List<Usuario>      findAll()              { return repo.findAll(); }
    @Override public Optional<Usuario>  findById(Long id)      { return repo.findById(id); }
    @Override public Optional<Usuario>  findByEmail(String e)  { return repo.findByEmail(e); }
    @Override public Usuario            save(Usuario u)        { return repo.save(u); }
    @Override public void               deleteById(Long id)    { repo.deleteById(id); }
    @Override public long               count()                { return repo.count(); }
}