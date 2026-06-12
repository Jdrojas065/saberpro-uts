package com.parcial.app.model;

import com.parcial.app.model.enums.EstadoInscripcion;
import com.parcial.app.model.enums.TipoExamen;
import jakarta.persistence.*;

@Entity
@Table(name = "estudiantes")
public class Estudiante {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, unique = true)
    private String documento;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "programa_id")
    private Programa programa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoInscripcion estadoInscripcion = EstadoInscripcion.PENDIENTE;

    @Enumerated(EnumType.STRING)
    private TipoExamen tipoExamen;

    // Nombre del archivo del comprobante de pago
    @Column
    private String comprobantePago;

    
    public Estudiante() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
    public Programa getPrograma() { return programa; }
    public void setPrograma(Programa programa) { this.programa = programa; }
    public EstadoInscripcion getEstadoInscripcion() { return estadoInscripcion; }
    public void setEstadoInscripcion(EstadoInscripcion e) { this.estadoInscripcion = e; }
    public TipoExamen getTipoExamen() { return tipoExamen; }
    public void setTipoExamen(TipoExamen tipoExamen) { this.tipoExamen = tipoExamen; }
    public String getComprobantePago() { return comprobantePago; }
    public void setComprobantePago(String comprobantePago) { this.comprobantePago = comprobantePago; }
}