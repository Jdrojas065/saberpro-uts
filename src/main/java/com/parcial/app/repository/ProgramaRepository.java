package com.parcial.app.repository;

import com.parcial.app.model.Programa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProgramaRepository extends JpaRepository<Programa, Long> {
    List<Programa> findByActivoTrue();
}