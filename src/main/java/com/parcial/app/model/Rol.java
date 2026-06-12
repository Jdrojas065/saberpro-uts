package com.parcial.app.model;

import com.parcial.app.model.enums.TipoRol;
import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Rol {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private TipoRol tipo;

    @Column(nullable = false)
    private String nombre;

    public Rol() {}
    public Rol(Long id, TipoRol tipo, String nombre) {
        this.id = id; this.tipo = tipo; this.nombre = nombre;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public TipoRol getTipo() { return tipo; }
    public void setTipo(TipoRol tipo) { this.tipo = tipo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}