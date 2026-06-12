package com.parcial.app.service;

import com.parcial.app.model.Convocatoria;
import com.parcial.app.repository.ConvocatoriaRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ConvocatoriaServiceImpl implements ConvocatoriaService {

    private final ConvocatoriaRepository repo;

    public ConvocatoriaServiceImpl(ConvocatoriaRepository repo) { this.repo = repo; }

    @Override public List<Convocatoria>      findActivas()  { return repo.findByActivaTrue(); }
    @Override public Optional<Convocatoria>  findProxima()  {
        return repo.findFirstByActivaTrueAndFechaAfterOrderByFechaAsc(LocalDate.now());
    }
    @Override public Convocatoria            save(Convocatoria c) { return repo.save(c); }
    @Override public long                    count()              { return repo.count(); }
}