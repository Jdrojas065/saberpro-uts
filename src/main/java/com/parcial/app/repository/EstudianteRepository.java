package com.parcial.app.repository;

import com.parcial.app.model.Estudiante;
import com.parcial.app.model.enums.EstadoInscripcion;
import com.parcial.app.model.Programa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
    Optional<Estudiante> findByUsuarioEmail(String email);
    List<Estudiante> findByPrograma(Programa programa);
    long countByEstadoInscripcion(EstadoInscripcion estado);
    
    @Query("SELECT e FROM Estudiante e JOIN FETCH e.usuario u LEFT JOIN FETCH e.programa p")
    List<Estudiante> findAllConDetalle();
}