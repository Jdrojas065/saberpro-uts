package com.parcial.app.service;

import com.parcial.app.model.Convocatoria;
import java.util.List;
import java.util.Optional;

public interface ConvocatoriaService {
    List<Convocatoria> findActivas();
    Optional<Convocatoria> findProxima();
    Convocatoria save(Convocatoria c);
    long count();
}