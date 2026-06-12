package com.parcial.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "preguntas")
public class Pregunta {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String enunciado;

    private String opcionA;
    private String opcionB;
    private String opcionC;
    private String opcionD;

    @Column(nullable = false)
    private String respuestaCorrecta;

    private String competencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simulacro_id")
    private Simulacro simulacro;

    public Pregunta() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEnunciado() { return enunciado; }
    public void setEnunciado(String enunciado) { this.enunciado = enunciado; }
    public String getOpcionA() { return opcionA; }
    public void setOpcionA(String opcionA) { this.opcionA = opcionA; }
    public String getOpcionB() { return opcionB; }
    public void setOpcionB(String opcionB) { this.opcionB = opcionB; }
    public String getOpcionC() { return opcionC; }
    public void setOpcionC(String opcionC) { this.opcionC = opcionC; }
    public String getOpcionD() { return opcionD; }
    public void setOpcionD(String opcionD) { this.opcionD = opcionD; }
    public String getRespuestaCorrecta() { return respuestaCorrecta; }
    public void setRespuestaCorrecta(String r) { this.respuestaCorrecta = r; }
    public String getCompetencia() { return competencia; }
    public void setCompetencia(String competencia) { this.competencia = competencia; }
    public Simulacro getSimulacro() { return simulacro; }
    public void setSimulacro(Simulacro simulacro) { this.simulacro = simulacro; }
}