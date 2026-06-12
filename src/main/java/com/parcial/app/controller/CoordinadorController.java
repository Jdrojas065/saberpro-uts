package com.parcial.app.controller;

import com.parcial.app.model.Estudiante;
import com.parcial.app.model.Resultado;
import com.parcial.app.model.Usuario;
import com.parcial.app.model.enums.EstadoInscripcion;
import com.parcial.app.model.enums.TipoExamen;
import com.parcial.app.repository.ProgramaRepository;
import com.parcial.app.repository.RolRepository;
import com.parcial.app.service.EstudianteService;
import com.parcial.app.service.ResultadoService;
import com.parcial.app.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/coordinador")
public class CoordinadorController {

    private final UsuarioService     usuarioService;
    private final EstudianteService  estudianteService;
    private final ProgramaRepository programaRepo;
    private final RolRepository      rolRepo;
    private final ResultadoService   resultadoService;
    private final PasswordEncoder    passwordEncoder;

    public CoordinadorController(UsuarioService us, EstudianteService es,
                                 ProgramaRepository pr, RolRepository rr,
                                 ResultadoService rs, PasswordEncoder pe) {
        this.usuarioService    = us;
        this.estudianteService = es;
        this.programaRepo      = pr;
        this.rolRepo           = rr;
        this.resultadoService  = rs;
        this.passwordEncoder   = pe;
    }

    // ── DASHBOARD ────────────────────────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails ud, Model model) {
        model.addAttribute("usuario", usuarioService.findByEmail(ud.getUsername()).orElseThrow());
        model.addAttribute("active", "dashboard");

        List<Estudiante> todos = estudianteService.findAll();
        long conResult = todos.stream()
            .filter(e -> resultadoService.findUltimo(e).isPresent())
            .count();

        model.addAttribute("totalEstudiantes", todos.size());
        model.addAttribute("conResultados",    conResult);
        model.addAttribute("sinResultados",    todos.size() - conResult);
        return "dashboard/coordinador";
    }

    // ── CRUD ALUMNOS ──────────────────────────────────────────────
    @GetMapping("/alumnos")
    public String alumnos(@AuthenticationPrincipal UserDetails ud, Model model,
                          @RequestParam(required = false) String filtro) {
        model.addAttribute("usuario", usuarioService.findByEmail(ud.getUsername()).orElseThrow());
        model.addAttribute("active", "alumnos");

        List<Estudiante> lista = estudianteService.findAll();
        long totalHab   = lista.stream().filter(e -> e.getEstadoInscripcion() == EstadoInscripcion.HABILITADO).count();
        long totalPend  = lista.stream().filter(e -> e.getEstadoInscripcion() == EstadoInscripcion.PENDIENTE).count();
        long totalNoHab = lista.stream().filter(e -> e.getEstadoInscripcion() == EstadoInscripcion.NO_HABILITADO).count();

        if (filtro != null && !filtro.isBlank()) {
            String f = filtro.toLowerCase();
            lista = lista.stream().filter(e ->
                e.getUsuario().getNombre().toLowerCase().contains(f) ||
                e.getUsuario().getApellido().toLowerCase().contains(f) ||
                e.getDocumento().contains(f)
            ).collect(Collectors.toList());
        }

        model.addAttribute("estudiantes", lista);
        model.addAttribute("filtro",      filtro);
        model.addAttribute("programas",   programaRepo.findByActivoTrue());
        model.addAttribute("totalHab",    totalHab);
        model.addAttribute("totalPend",   totalPend);
        model.addAttribute("totalNoHab",  totalNoHab);
        return "coordinador/alumnos";
    }

    @PostMapping("/alumnos/nuevo")
    public String nuevoAlumno(@RequestParam String documento,
                              @RequestParam String nombre,
                              @RequestParam String apellido,
                              @RequestParam String email,
                              @RequestParam String password,
                              @RequestParam Long programaId,
                              @RequestParam(required = false) String tipoExamen,
                              RedirectAttributes ra) {
        if (usuarioService.findByEmail(email).isPresent()) {
            ra.addFlashAttribute("error", "El correo ya está registrado.");
            return "redirect:/coordinador/alumnos";
        }

        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setApellido(apellido);
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(password));
        u.setRol(rolRepo.findAll().stream()
            .filter(r -> r.getTipo().name().equals("ESTUDIANTE"))
            .findFirst().orElseThrow());
        u.setActivo(true);
        usuarioService.save(u);

        Estudiante e = new Estudiante();
        e.setDocumento(documento);
        e.setUsuario(u);
        e.setPrograma(programaRepo.findById(programaId).orElse(null));
        e.setEstadoInscripcion(EstadoInscripcion.PENDIENTE);
        if (tipoExamen != null && !tipoExamen.isBlank())
            e.setTipoExamen(TipoExamen.valueOf(tipoExamen));
        estudianteService.save(e);

        ra.addFlashAttribute("mensaje", "Estudiante creado correctamente.");
        return "redirect:/coordinador/alumnos";
    }

    // ── FORMULARIO EDITAR ALUMNO ──────────────────────────────────
    @GetMapping("/alumnos/{id}/editar")
    public String formEditarAlumno(@AuthenticationPrincipal UserDetails ud,
                                   @PathVariable Long id, Model model) {
        model.addAttribute("usuario", usuarioService.findByEmail(ud.getUsername()).orElseThrow());
        model.addAttribute("active", "alumnos");
        estudianteService.findById(id).ifPresent(e ->
            model.addAttribute("estudianteEditar", e));
        model.addAttribute("programas", programaRepo.findByActivoTrue());
        return "coordinador/editar-alumno";
    }

    @PostMapping("/alumnos/{id}/editar")
    public String editarAlumno(@PathVariable Long id,
                               @RequestParam String nombre,
                               @RequestParam String apellido,
                               @RequestParam String email,
                               @RequestParam Long programaId,
                               @RequestParam(required = false) String tipoExamen,
                               RedirectAttributes ra) {
        estudianteService.findById(id).ifPresent(e -> {
            e.getUsuario().setNombre(nombre);
            e.getUsuario().setApellido(apellido);
            e.getUsuario().setEmail(email);
            e.setPrograma(programaRepo.findById(programaId).orElse(e.getPrograma()));
            if (tipoExamen != null && !tipoExamen.isBlank())
                e.setTipoExamen(TipoExamen.valueOf(tipoExamen));
            usuarioService.save(e.getUsuario());
            estudianteService.save(e);
        });
        ra.addFlashAttribute("mensaje", "Estudiante actualizado correctamente.");
        return "redirect:/coordinador/alumnos";
    }

    @PostMapping("/alumnos/{id}/toggle")
    public String toggleAlumno(@PathVariable Long id, RedirectAttributes ra) {
        estudianteService.findById(id).ifPresent(e -> {
            e.getUsuario().setActivo(!e.getUsuario().isActivo());
            usuarioService.save(e.getUsuario());
        });
        ra.addFlashAttribute("mensaje", "Estado del alumno actualizado.");
        return "redirect:/coordinador/alumnos";
    }

    // ── APROBAR ALUMNO SABER PRO ──────────────────────────────────
    @PostMapping("/alumnos/{id}/aprobar")
    public String aprobarSaberPro(@PathVariable Long id, RedirectAttributes ra) {
        estudianteService.findById(id).ifPresent(e -> {
            if (e.getComprobantePago() == null || e.getComprobantePago().isBlank()) {
                ra.addFlashAttribute("error",
                    "El alumno " + e.getUsuario().getNombre() +
                    " aún no ha subido el comprobante de pago.");
                return;
            }
            e.setEstadoInscripcion(EstadoInscripcion.HABILITADO);
            e.setTipoExamen(TipoExamen.SABER_PRO);
            estudianteService.save(e);
            ra.addFlashAttribute("mensaje", "Alumno aprobado para Saber Pro correctamente.");
        });
        return "redirect:/coordinador/alumnos";
    }

    // ── INFORME ALUMNOS TOTAL ─────────────────────────────────────
    @GetMapping("/informe-total")
    public String informeTotal(@AuthenticationPrincipal UserDetails ud, Model model) {
        model.addAttribute("usuario", usuarioService.findByEmail(ud.getUsername()).orElseThrow());
        model.addAttribute("active", "informe-total");

        List<Estudiante> todos = estudianteService.findAll();
        long habilitados  = todos.stream().filter(e -> e.getEstadoInscripcion() == EstadoInscripcion.HABILITADO).count();
        long pendientes   = todos.stream().filter(e -> e.getEstadoInscripcion() == EstadoInscripcion.PENDIENTE).count();
        long noHab        = todos.stream().filter(e -> e.getEstadoInscripcion() == EstadoInscripcion.NO_HABILITADO).count();

        List<Map<String, Object>> porPrograma = programaRepo.findByActivoTrue().stream().map(p -> {
            List<Estudiante> ests = todos.stream()
                .filter(e -> e.getPrograma() != null && e.getPrograma().getId().equals(p.getId()))
                .collect(Collectors.toList());
            long hab = ests.stream()
                .filter(e -> e.getEstadoInscripcion() == EstadoInscripcion.HABILITADO).count();
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("nombre",      p.getNombre());
            m.put("codigo",      p.getCodigo());
            m.put("total",       ests.size());
            m.put("habilitados", hab);
            m.put("pendientes",  ests.size() - hab);
            m.put("pct",         ests.isEmpty() ? 0 : (hab * 100 / ests.size()));
            return m;
        }).collect(Collectors.toList());

        model.addAttribute("todos",         todos);
        model.addAttribute("totalGeneral",  todos.size());
        model.addAttribute("habilitados",   habilitados);
        model.addAttribute("pendientes",    pendientes);
        model.addAttribute("noHabilitados", noHab);
        model.addAttribute("porPrograma",   porPrograma);
        return "coordinador/informe-total";
    }

    // ── INFORME ALUMNO ÚNICO ──────────────────────────────────────
    @GetMapping("/informe-unico")
    public String informeUnico(@AuthenticationPrincipal UserDetails ud, Model model,
                               @RequestParam(required = false) Long estudianteId) {
        model.addAttribute("usuario", usuarioService.findByEmail(ud.getUsername()).orElseThrow());
        model.addAttribute("active", "informe-unico");
        model.addAttribute("estudiantes", estudianteService.findAll());

        if (estudianteId != null) {
            estudianteService.findById(estudianteId).ifPresent(e -> {
                model.addAttribute("estudianteSeleccionado", e);
                List<Resultado> resultados = resultadoService.findByEstudiante(e);
                model.addAttribute("resultados", resultados);
                if (!resultados.isEmpty()) {
                    OptionalDouble prom = resultados.stream()
                        .mapToInt(r -> r.getPuntaje() != null ? r.getPuntaje() : 0).average();
                    model.addAttribute("promedio",
                        prom.isPresent() ? (long) Math.round(prom.getAsDouble()) : null);
                }
            });
        }
        return "coordinador/informe-unico";
    }

    // ── INFORME DE BENEFICIOS ─────────────────────────────────────
    @GetMapping("/informe-beneficios")
    public String informeBeneficios(@AuthenticationPrincipal UserDetails ud, Model model) {
        model.addAttribute("usuario", usuarioService.findByEmail(ud.getUsername()).orElseThrow());
        model.addAttribute("active", "informe-beneficios");

        List<Estudiante> todos = estudianteService.findAll();
        List<Estudiante> habilitados = todos.stream()
            .filter(e -> e.getEstadoInscripcion() == EstadoInscripcion.HABILITADO)
            .collect(Collectors.toList());

        List<Map<String, Object>> porPrograma = programaRepo.findByActivoTrue().stream().map(p -> {
            long count = habilitados.stream()
                .filter(e -> e.getPrograma() != null && e.getPrograma().getId().equals(p.getId()))
                .count();
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("nombre", p.getNombre());
            m.put("codigo", p.getCodigo());
            m.put("count",  count);
            return m;
        }).collect(Collectors.toList());

        model.addAttribute("habilitados", habilitados);
        model.addAttribute("totalHab",    habilitados.size());
        model.addAttribute("porPrograma", porPrograma);
        return "coordinador/informe-beneficios";
    }

    // ── RESOLUCIÓN BENEFICIOS ─────────────────────────────────────
    @GetMapping("/beneficios")
    public String beneficios(@AuthenticationPrincipal UserDetails ud, Model model) {
        model.addAttribute("usuario", usuarioService.findByEmail(ud.getUsername()).orElseThrow());
        model.addAttribute("active", "beneficios");
        return "coordinador/beneficios";
    }

    // ── VER FORMULARIO DE PUNTAJES ────────────────────────────────
    @GetMapping("/alumnos/{id}/puntajes")
    public String formPuntajes(@AuthenticationPrincipal UserDetails ud,
                               @PathVariable Long id, Model model) {
        model.addAttribute("usuario", usuarioService.findByEmail(ud.getUsername()).orElseThrow());
        model.addAttribute("active", "alumnos");
        estudianteService.findById(id).ifPresent(e -> {
            model.addAttribute("estudiantePuntaje", e);
            resultadoService.findUltimo(e).ifPresent(r ->
                model.addAttribute("resultadoExistente", r));
        });
        return "coordinador/puntajes-alumno";
    }
    
 // ── PAGOS ─────────────────────────────────────────────────────
    @GetMapping("/pagos")
    public String pagos(@AuthenticationPrincipal UserDetails ud, Model model,
                        @RequestParam(required = false) String filtro,
                        @RequestParam(required = false) String estado) {
        model.addAttribute("usuario", usuarioService.findByEmail(ud.getUsername()).orElseThrow());
        model.addAttribute("active", "pagos");

        List<Estudiante> todos = estudianteService.findAll();

        long conComprobante    = todos.stream().filter(e -> e.getComprobantePago() != null && !e.getComprobantePago().isBlank()).count();
        long sinComprobante    = todos.size() - conComprobante;
        long pendientesAprobar = todos.stream().filter(e ->
            e.getComprobantePago() != null && !e.getComprobantePago().isBlank() &&
            e.getEstadoInscripcion() == EstadoInscripcion.PENDIENTE).count();

        List<Estudiante> lista = todos;

        if ("con_pago".equals(estado)) {
            lista = lista.stream().filter(e -> e.getComprobantePago() != null && !e.getComprobantePago().isBlank()).collect(Collectors.toList());
        } else if ("sin_pago".equals(estado)) {
            lista = lista.stream().filter(e -> e.getComprobantePago() == null || e.getComprobantePago().isBlank()).collect(Collectors.toList());
        } else if ("pendiente".equals(estado)) {
            lista = lista.stream().filter(e ->
                e.getComprobantePago() != null && !e.getComprobantePago().isBlank() &&
                e.getEstadoInscripcion() == EstadoInscripcion.PENDIENTE).collect(Collectors.toList());
        }

        if (filtro != null && !filtro.isBlank()) {
            String f = filtro.toLowerCase();
            lista = lista.stream().filter(e ->
                e.getUsuario().getNombre().toLowerCase().contains(f) ||
                e.getUsuario().getApellido().toLowerCase().contains(f) ||
                e.getDocumento().contains(f)
            ).collect(Collectors.toList());
        }

        model.addAttribute("estudiantes",      lista);
        model.addAttribute("filtro",           filtro);
        model.addAttribute("estadoFiltro",     estado);
        model.addAttribute("conComprobante",   conComprobante);
        model.addAttribute("sinComprobante",   sinComprobante);
        model.addAttribute("pendientesAprobar",pendientesAprobar);
        return "coordinador/pagos";
    }

    @PostMapping("/pagos/{id}/aprobar")
    public String aprobarPago(@PathVariable Long id, RedirectAttributes ra) {
        estudianteService.findById(id).ifPresent(e -> {
            e.setEstadoInscripcion(EstadoInscripcion.HABILITADO);
            e.setTipoExamen(TipoExamen.SABER_PRO);
            estudianteService.save(e);
        });
        ra.addFlashAttribute("mensaje", "Pago aprobado. Estudiante habilitado correctamente.");
        return "redirect:/coordinador/pagos";
    }

    @PostMapping("/pagos/{id}/rechazar")
    public String rechazarPago(@PathVariable Long id, RedirectAttributes ra) {
        estudianteService.findById(id).ifPresent(e -> {
            e.setComprobantePago(null);
            e.setEstadoInscripcion(EstadoInscripcion.NO_HABILITADO);
            estudianteService.save(e);
        });
        ra.addFlashAttribute("mensaje", "Comprobante rechazado. Estudiante marcado como no habilitado.");
        return "redirect:/coordinador/pagos";
    }

    // ── GUARDAR PUNTAJES ──────────────────────────────────────────
    @PostMapping("/alumnos/{id}/puntajes")
    public String guardarPuntajes(@PathVariable Long id,
                                  @RequestParam Integer puntaje,
                                  @RequestParam(required=false) Integer comunicacionEscrita,
                                  @RequestParam(required=false) Integer razonamientoCuantitativo,
                                  @RequestParam(required=false) Integer lecturaCritica,
                                  @RequestParam(required=false) Integer competenciasCiudadanas,
                                  @RequestParam(required=false) Integer ingles,
                                  @RequestParam(required=false) Integer formulacionProyectos,
                                  @RequestParam(required=false) Integer pensamientoCientifico,
                                  @RequestParam(required=false) Integer disenoSoftware,
                                  RedirectAttributes ra) {
        estudianteService.findById(id).ifPresent(e -> {
            Resultado r = resultadoService.findUltimo(e)
                .orElse(new com.parcial.app.model.Resultado());
            r.setEstudiante(e);
            r.setPuntaje(puntaje);
            r.setPuntajeComunicacionEscrita(comunicacionEscrita);
            r.setPuntajeRazonamientoCuantitativo(razonamientoCuantitativo);
            r.setPuntajeLecturaCritica(lecturaCritica);
            r.setPuntajeCompetenciasCiudadanas(competenciasCiudadanas);
            r.setPuntajeIngles(ingles);
            r.setFormulacionProyectos(formulacionProyectos);
            r.setPensamientoCientifico(pensamientoCientifico);
            r.setDisenoSoftware(disenoSoftware);
            r.setFecha(java.time.LocalDateTime.now());
            resultadoService.save(r);

            if (puntaje >= 191) {
                e.setEstadoInscripcion(EstadoInscripcion.HABILITADO);
                estudianteService.save(e);
            }
        });
        ra.addFlashAttribute("mensaje", "Puntajes registrados correctamente.");
        return "redirect:/coordinador/alumnos";
    }

    @PostMapping("/beneficios/guardar")
    public String guardarBeneficios(RedirectAttributes ra) {
        ra.addFlashAttribute("mensaje", "Resolución de beneficios guardada correctamente.");
        return "redirect:/coordinador/beneficios";
    }
}