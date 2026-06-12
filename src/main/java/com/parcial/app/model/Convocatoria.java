package com.parcial.app.model;

import com.parcial.app.model.enums.TipoExamen;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "convocatorias")
public class Convocatoria {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoExamen tipo;

    @Column(nullable = false)
    private LocalDate fecha;

    private LocalDate fechaCierre;

    @Column(nullable = false)
    private boolean activa = true;

    public Convocatoria() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public TipoExamen getTipo() { return tipo; }
    public void setTipo(TipoExamen tipo) { this.tipo = tipo; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public LocalDate getFechaCierre() { return fechaCierre; }
    public void setFechaCierre(LocalDate fechaCierre) { this.fechaCierre = fechaCierre; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
}