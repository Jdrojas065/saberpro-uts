package com.parcial.app.repository;


import com.parcial.app.model.Rol;
import com.parcial.app.model.enums.TipoRol;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByTipo(TipoRol tipo);
}