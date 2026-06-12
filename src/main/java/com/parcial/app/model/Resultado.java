package com.parcial.app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resultados")
public class Resultado {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "simulacro_id")
    private Simulacro simulacro;

    @Column(nullable = false)
    private Integer puntaje;

    private Integer puntajeLecturaCritica;
    private Integer puntajeRazonamientoCuantitativo;
    private Integer puntajeCompetenciasCiudadanas;
    private Integer puntajeComunicacionEscrita;
    private Integer puntajeIngles;

    private String competencia;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();
    


    @Column
    private Integer formulacionProyectos;

    @Column
    private Integer pensamientoCientifico;

    @Column
    private Integer disenoSoftware;

    // Getters y setters:
    public Integer getFormulacionProyectos() { return formulacionProyectos; }
    public void setFormulacionProyectos(Integer v) { this.formulacionProyectos = v; }

    public Integer getPensamientoCientifico() { return pensamientoCientifico; }
    public void setPensamientoCientifico(Integer v) { this.pensamientoCientifico = v; }

    public Integer getDisenoSoftware() { return disenoSoftware; }
    public void setDisenoSoftware(Integer v) { this.disenoSoftware = v; }
    

    public Resultado() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Estudiante getEstudiante() { return estudiante; }
    public void setEstudiante(Estudiante e) { this.estudiante = e; }
    public Simulacro getSimulacro() { return simulacro; }
    public void setSimulacro(Simulacro s) { this.simulacro = s; }
    public Integer getPuntaje() { return puntaje; }
    public void setPuntaje(Integer p) { this.puntaje = p; }
    public Integer getPuntajeLecturaCritica() { return puntajeLecturaCritica; }
    public void setPuntajeLecturaCritica(Integer p) { this.puntajeLecturaCritica = p; }
    public Integer getPuntajeRazonamientoCuantitativo() { return puntajeRazonamientoCuantitativo; }
    public void setPuntajeRazonamientoCuantitativo(Integer p) { this.puntajeRazonamientoCuantitativo = p; }
    public Integer getPuntajeCompetenciasCiudadanas() { return puntajeCompetenciasCiudadanas; }
    public void setPuntajeCompetenciasCiudadanas(Integer p) { this.puntajeCompetenciasCiudadanas = p; }
    public Integer getPuntajeComunicacionEscrita() { return puntajeComunicacionEscrita; }
    public void setPuntajeComunicacionEscrita(Integer p) { this.puntajeComunicacionEscrita = p; }
    public Integer getPuntajeIngles() { return puntajeIngles; }
    public void setPuntajeIngles(Integer p) { this.puntajeIngles = p; }
    public String getCompetencia() { return competencia; }
    public void setCompetencia(String c) { this.competencia = c; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime f) { this.fecha = f; }
}