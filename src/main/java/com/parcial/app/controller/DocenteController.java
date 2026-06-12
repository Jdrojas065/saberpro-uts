package com.parcial.app.controller;

import com.parcial.app.model.Estudiante;
import com.parcial.app.model.Resultado;
import com.parcial.app.model.Usuario;
import com.parcial.app.model.enums.EstadoInscripcion;
import com.parcial.app.repository.DocenteRepository;
import com.parcial.app.repository.ProgramaRepository;
import com.parcial.app.service.EstudianteService;
import com.parcial.app.service.ResultadoService;
import com.parcial.app.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/docente")
public class DocenteController {

    private final UsuarioService     usuarioService;
    private final EstudianteService  estudianteService;
    private final ResultadoService   resultadoService;
    private final ProgramaRepository programaRepo;
    private final DocenteRepository  docenteRepo;

    public DocenteController(UsuarioService us, EstudianteService es,
                             ResultadoService rs, ProgramaRepository pr,
                             DocenteRepository dr) {
        this.usuarioService    = us;
        this.estudianteService = es;
        this.resultadoService  = rs;
        this.programaRepo      = pr;
        this.docenteRepo       = dr;
    }

    private Usuario getUsuarioActual(UserDetails ud) {
        return usuarioService.findByEmail(ud.getUsername()).orElseThrow();
    }

    private void addCommonAttrs(Model model, UserDetails ud, String active) {
        model.addAttribute("usuario", getUsuarioActual(ud));
        model.addAttribute("active",  active);
    }

    // ── DASHBOARD ────────────────────────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails ud, Model model) {
        addCommonAttrs(model, ud, "dashboard");

        List<Estudiante> todos = estudianteService.findAll();
        long conResult   = todos.stream()
            .filter(e -> !resultadoService.findByEstudiante(e).isEmpty()).count();
        long habilitados = estudianteService.countByEstado(EstadoInscripcion.HABILITADO);

        model.addAttribute("totalEstudiantes", todos.size());
        model.addAttribute("conResultados",    conResult);
        model.addAttribute("totalHabilitados", habilitados);
        model.addAttribute("totalProgramas",   programaRepo.findByActivoTrue().size());
        return "dashboard/docente";
    }

    // ── PERFIL (mantener para el header dropdown) ─────────────────
    @GetMapping("/perfil")
    public String perfil(@AuthenticationPrincipal UserDetails ud, Model model) {
        addCommonAttrs(model, ud, "perfil");
        return "docente/perfil";
    }

    @PostMapping("/perfil/datos")
    public String actualizarDatos(@AuthenticationPrincipal UserDetails ud,
                                  @RequestParam String nombre,
                                  @RequestParam String apellido,
                                  @RequestParam String email,
                                  RedirectAttributes ra) {
        Usuario u = getUsuarioActual(ud);
        u.setNombre(nombre);
        u.setApellido(apellido);
        u.setEmail(email);
        usuarioService.save(u);
        ra.addFlashAttribute("mensaje", "Datos actualizados correctamente.");
        return "redirect:/docente/perfil";
    }

    // ── POR FACULTAD ─────────────────────────────────────────────
    @GetMapping("/por-facultad")
    public String porFacultad(@AuthenticationPrincipal UserDetails ud, Model model,
                              @RequestParam(required = false) Long programaId) {
        addCommonAttrs(model, ud, "por-facultad");
        model.addAttribute("programas", programaRepo.findByActivoTrue());

        List<Estudiante> lista;
        if (programaId != null) {
            programaRepo.findById(programaId).ifPresent(p ->
                model.addAttribute("programaSeleccionado", p));
            lista = estudianteService.findAll().stream()
                .filter(e -> e.getPrograma() != null
                          && e.getPrograma().getId().equals(programaId))
                .collect(Collectors.toList());
        } else {
            lista = estudianteService.findAll();
        }

        model.addAttribute("estudiantes", lista);
        model.addAttribute("programaId",  programaId);
        return "docente/por-facultad";
    }

    // ── POR CÉDULA ───────────────────────────────────────────────
    @GetMapping("/por-cedula")
    public String porCedula(@AuthenticationPrincipal UserDetails ud, Model model,
                            @RequestParam(required = false) String cedula) {
        addCommonAttrs(model, ud, "por-cedula");
        model.addAttribute("cedula", cedula);

        if (cedula != null && !cedula.isBlank()) {
            Optional<Estudiante> found = estudianteService.findAll().stream()
                .filter(e -> e.getDocumento().equals(cedula.trim()))
                .findFirst();

            if (found.isPresent()) {
                Estudiante e = found.get();
                List<Resultado> resultados = resultadoService.findByEstudiante(e);
                model.addAttribute("estudianteEncontrado", e);
                model.addAttribute("resultados", resultados);

                OptionalDouble prom = resultados.stream()
                    .mapToInt(r -> r.getPuntaje() != null ? r.getPuntaje() : 0)
                    .average();
                model.addAttribute("promedio",
                    prom.isPresent() ? (long) Math.round(prom.getAsDouble()) : null);
            } else {
                model.addAttribute("noEncontrado", true);
            }
        }
        return "docente/por-cedula";
    }

    // ── INFORME TOTAL ─────────────────────────────────────────────
    @GetMapping("/informe-total")
    public String informeTotal(@AuthenticationPrincipal UserDetails ud, Model model) {
        addCommonAttrs(model, ud, "informe-total");

        List<Estudiante> todos = estudianteService.findAll();

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

        model.addAttribute("todos",        todos);
        model.addAttribute("totalGeneral", todos.size());
        model.addAttribute("habilitados",  estudianteService.countByEstado(EstadoInscripcion.HABILITADO));
        model.addAttribute("pendientes",   estudianteService.countByEstado(EstadoInscripcion.PENDIENTE));
        model.addAttribute("noHabilitados",estudianteService.countByEstado(EstadoInscripcion.NO_HABILITADO));
        model.addAttribute("porPrograma",  porPrograma);
        return "docente/informe-total";
    }

    // ── INFORME DE BENEFICIOS ─────────────────────────────────────
    @GetMapping("/informe-beneficios")
    public String informeBeneficios(@AuthenticationPrincipal UserDetails ud, Model model) {
        addCommonAttrs(model, ud, "informe-beneficios");

        List<Estudiante> habilitados = estudianteService.findAll().stream()
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
        return "docente/informe-beneficios";
    }

    // ── RESOLUCIÓN BENEFICIOS TyI ─────────────────────────────────
    @GetMapping("/beneficios")
    public String beneficios(@AuthenticationPrincipal UserDetails ud, Model model) {
        addCommonAttrs(model, ud, "beneficios");
        return "docente/beneficios";
    }

    @PostMapping("/beneficios/guardar")
    public String guardarBeneficios(RedirectAttributes ra) {
        ra.addFlashAttribute("mensaje", "Resolución guardada correctamente.");
        return "redirect:/docente/beneficios";
    }
}