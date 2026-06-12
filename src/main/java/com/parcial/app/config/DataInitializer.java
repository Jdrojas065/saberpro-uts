package com.parcial.app.config;

import com.parcial.app.model.Convocatoria;
import com.parcial.app.model.Docente;
import com.parcial.app.model.Estudiante;
import com.parcial.app.model.Programa;
import com.parcial.app.model.Rol;
import com.parcial.app.model.Usuario;
import com.parcial.app.model.enums.EstadoInscripcion;
import com.parcial.app.model.enums.TipoExamen;
import com.parcial.app.model.enums.TipoRol;
import com.parcial.app.repository.ConvocatoriaRepository;
import com.parcial.app.repository.DocenteRepository;
import com.parcial.app.repository.EstudianteRepository;
import com.parcial.app.repository.ProgramaRepository;
import com.parcial.app.repository.RolRepository;
import com.parcial.app.repository.UsuarioRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements ApplicationRunner {

    private final RolRepository        rolRepo;
    private final UsuarioRepository    usuarioRepo;
    private final ProgramaRepository   programaRepo;
    private final EstudianteRepository estudianteRepo;
    private final DocenteRepository    docenteRepo;
    private final ConvocatoriaRepository convocatoriaRepo;
    private final PasswordEncoder      encoder;

    public DataInitializer(RolRepository rolRepo,
                           UsuarioRepository usuarioRepo,
                           ProgramaRepository programaRepo,
                           EstudianteRepository estudianteRepo,
                           DocenteRepository docenteRepo,
                           ConvocatoriaRepository convocatoriaRepo,
                           PasswordEncoder encoder) {
        this.rolRepo          = rolRepo;
        this.usuarioRepo      = usuarioRepo;
        this.programaRepo     = programaRepo;
        this.estudianteRepo   = estudianteRepo;
        this.docenteRepo      = docenteRepo;
        this.convocatoriaRepo = convocatoriaRepo;
        this.encoder          = encoder;
    }

    @Override
    public void run(ApplicationArguments args) {

        // ── Roles ──────────────────────────────────────────────
        Rol rolAdmin = getOrCreateRol(TipoRol.ADMIN,       "Administrador del Sistema");
        Rol rolCoord = getOrCreateRol(TipoRol.COORDINADOR, "Coordinación Académica");
        Rol rolDoc   = getOrCreateRol(TipoRol.DOCENTE,     "Docente");
        Rol rolEst   = getOrCreateRol(TipoRol.ESTUDIANTE,  "Estudiante");

        // ── Administrador ───────────────────────────────────────
        if (usuarioRepo.findByEmail("admin@uts.edu.co").isEmpty()) {
            Usuario u = new Usuario();
            u.setNombre("Carlos");
            u.setApellido("Méndez");
            u.setEmail("admin@uts.edu.co");
            u.setPassword(encoder.encode("admin123"));
            u.setRol(rolAdmin);
            u.setActivo(true);
            usuarioRepo.save(u);
        }

        // ── Coordinador ─────────────────────────────────────────
        if (usuarioRepo.findByEmail("coordinador@uts.edu.co").isEmpty()) {
            Usuario u = new Usuario();
            u.setNombre("María");
            u.setApellido("López");
            u.setEmail("coordinador@uts.edu.co");
            u.setPassword(encoder.encode("admin123"));
            u.setRol(rolCoord);
            u.setActivo(true);
            usuarioRepo.save(u);
        }

        // ── Programas ───────────────────────────────────────────
        if (programaRepo.count() == 0) {
            programaRepo.save(crearPrograma("Ingeniería de Sistemas",      "ISI"));
            programaRepo.save(crearPrograma("Administración de Empresas",  "ADE"));
            programaRepo.save(crearPrograma("Contaduría Pública",          "COP"));
            programaRepo.save(crearPrograma("Ingeniería Electrónica",      "IEL"));
        }

        // ── Docente de muestra ──────────────────────────────────
        if (usuarioRepo.findByEmail("docente@uts.edu.co").isEmpty()) {
            Usuario u = new Usuario();
            u.setNombre("Andrés");
            u.setApellido("Ruiz");
            u.setEmail("docente@uts.edu.co");
            u.setPassword(encoder.encode("docente123"));
            u.setRol(rolDoc);
            u.setActivo(true);
            Usuario uGuardado = usuarioRepo.save(u);

            Docente d = new Docente();
            d.setUsuario(uGuardado);
            d.setEspecialidad("Matemáticas y Estadística");
            docenteRepo.save(d);
        }

        // ── Estudiante de muestra ───────────────────────────────
        if (usuarioRepo.findByEmail("estudiante1@uts.edu.co").isEmpty()) {
            Programa prog = programaRepo.findAll().get(0);

            Usuario u = new Usuario();
            u.setNombre("Laura");
            u.setApellido("García");
            u.setEmail("estudiante1@uts.edu.co");
            u.setPassword(encoder.encode("1098765432"));
            u.setRol(rolEst);
            u.setActivo(true);
            Usuario uGuardado = usuarioRepo.save(u);

            Estudiante e = new Estudiante();
            e.setUsuario(uGuardado);
            e.setDocumento("1098765432");
            e.setPrograma(prog);
            e.setEstadoInscripcion(EstadoInscripcion.HABILITADO);
            e.setTipoExamen(TipoExamen.SABER_PRO);
            estudianteRepo.save(e);
        }

        // ── Convocatoria de muestra ─────────────────────────────
        if (convocatoriaRepo.count() == 0) {
            Convocatoria c = new Convocatoria();
            c.setNombre("Saber Pro 2025-2");
            c.setTipo(TipoExamen.SABER_PRO);
            c.setFecha(LocalDate.of(2025, 11, 15));
            c.setFechaCierre(LocalDate.of(2025, 10, 31));
            c.setActiva(true);
            convocatoriaRepo.save(c);
        }
    }

    // ── Helpers ─────────────────────────────────────────────────
    private Rol getOrCreateRol(TipoRol tipo, String nombre) {
        return rolRepo.findByTipo(tipo).orElseGet(() -> {
            Rol r = new Rol();
            r.setTipo(tipo);
            r.setNombre(nombre);
            return rolRepo.save(r);
        });
    }

    private Programa crearPrograma(String nombre, String codigo) {
        Programa p = new Programa();
        p.setNombre(nombre);
        p.setCodigo(codigo);
        p.setActivo(true);
        return p;
    }
}