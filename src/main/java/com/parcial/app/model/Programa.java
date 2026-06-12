package com.parcial.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "programas")
public class Programa {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, length = 10)
    private String codigo;

    @Column(nullable = false)
    private boolean activo = true;

    public Programa() {}
    public Programa(Long id, String nombre, String codigo, boolean activo) {
        this.id = id; this.nombre = nombre; this.codigo = codigo; this.activo = activo;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}