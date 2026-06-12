package com.parcial.app.repository;

import com.parcial.app.model.Estudiante;
import com.parcial.app.model.Resultado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface ResultadoRepository extends JpaRepository<Resultado, Long> {
    List<Resultado> findByEstudianteOrderByFechaDesc(Estudiante estudiante);
    Optional<Resultado> findFirstByEstudianteOrderByFechaDesc(Estudiante estudiante);

    @Query("SELECT AVG(r.puntaje) FROM Resultado r WHERE r.estudiante.programa.id = :programaId")
    Double promedioByPrograma(Long programaId);

    List<Resultado> findAllByOrderByFechaDesc();
}