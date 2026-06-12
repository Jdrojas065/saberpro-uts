package com.parcial.app.repository;

import com.parcial.app.model.Convocatoria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ConvocatoriaRepository extends JpaRepository<Convocatoria, Long> {
    List<Convocatoria> findByActivaTrue();
    Optional<Convocatoria> findFirstByActivaTrueAndFechaAfterOrderByFechaAsc(LocalDate hoy);
}