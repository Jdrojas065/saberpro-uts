package com.parcial.app.controller;

import com.parcial.app.model.Usuario;
import com.parcial.app.model.enums.TipoRol;
import com.parcial.app.repository.ProgramaRepository;
import com.parcial.app.repository.RolRepository;
import com.parcial.app.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UsuarioService     usuarioService;
    private final RolRepository      rolRepo;
    private final ProgramaRepository programaRepo;
    private final PasswordEncoder    passwordEncoder;

    public AdminController(UsuarioService usuarioService,
                           RolRepository rolRepo,
                           ProgramaRepository programaRepo,
                           PasswordEncoder passwordEncoder) {
        this.usuarioService  = usuarioService;
        this.rolRepo         = rolRepo;
        this.programaRepo    = programaRepo;
        this.passwordEncoder = passwordEncoder;
    }

    private Usuario getUsuarioActual(UserDetails ud) {
        return usuarioService.findByEmail(ud.getUsername()).orElseThrow();
    }

    private void addCommonAttrs(Model model, UserDetails ud, String active) {
        model.addAttribute("usuario", getUsuarioActual(ud));
        model.addAttribute("active", active);
    }

    // ── DASHBOARD → redirige a docentes ───────────────────────
    @GetMapping("/dashboard")
    public String dashboard() {
        return "redirect:/admin/docentes";
    }

    // ── CRUD DOCENTES ──────────────────────────────────────────
    @GetMapping("/docentes")
    public String docentes(@AuthenticationPrincipal UserDetails ud, Model model) {
        addCommonAttrs(model, ud, "docentes");
        model.addAttribute("usuarios", usuarioService.findAll().stream()
            .filter(u -> u.getRol().getTipo() == TipoRol.DOCENTE)
            .toList());
        return "admin/docentes";
    }

    @PostMapping("/docentes/nuevo")
    public String nuevoDocente(@RequestParam String nombre,
                               @RequestParam String apellido,
                               @RequestParam String email,
                               @RequestParam String password,
                               RedirectAttributes ra) {
        if (usuarioService.findByEmail(email).isPresent()) {
            ra.addFlashAttribute("error", "El correo ya está registrado.");
            return "redirect:/admin/docentes";
        }
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setApellido(apellido);
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(password)); // ← CORREGIDO
        u.setRol(rolRepo.findAll().stream()
            .filter(r -> r.getTipo() == TipoRol.DOCENTE)
            .findFirst().orElseThrow());
        u.setActivo(true);
        usuarioService.save(u);
        ra.addFlashAttribute("mensaje", "Docente creado correctamente.");
        return "redirect:/admin/docentes";
    }

    @PostMapping("/docentes/{id}/editar")
    public String editarDocente(@PathVariable Long id,
                                @RequestParam String nombre,
                                @RequestParam String apellido,
                                @RequestParam String email,
                                RedirectAttributes ra) {
        usuarioService.findById(id).ifPresent(u -> {
            u.setNombre(nombre);
            u.setApellido(apellido);
            u.setEmail(email);
            usuarioService.save(u);
        });
        ra.addFlashAttribute("mensaje", "Docente actualizado.");
        return "redirect:/admin/docentes";
    }

    @PostMapping("/docentes/{id}/eliminar")
    public String eliminarDocente(@PathVariable Long id, RedirectAttributes ra) {
        usuarioService.deleteById(id);
        ra.addFlashAttribute("mensaje", "Docente eliminado.");
        return "redirect:/admin/docentes";
    }

    // ── CRUD COORDINACIÓN ──────────────────────────────────────
    @GetMapping("/coordinadores")
    public String coordinadores(@AuthenticationPrincipal UserDetails ud, Model model) {
        addCommonAttrs(model, ud, "coordinadores");
        model.addAttribute("usuarios", usuarioService.findAll().stream()
            .filter(u -> u.getRol().getTipo() == TipoRol.COORDINADOR)
            .toList());
        return "admin/coordinadores";
    }

    @PostMapping("/coordinadores/nuevo")
    public String nuevoCoordinador(@RequestParam String nombre,
                                   @RequestParam String apellido,
                                   @RequestParam String email,
                                   @RequestParam String password,
                                   RedirectAttributes ra) {
        if (usuarioService.findByEmail(email).isPresent()) {
            ra.addFlashAttribute("error", "El correo ya está registrado.");
            return "redirect:/admin/coordinadores";
        }
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setApellido(apellido);
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(password)); // ← CORREGIDO
        u.setRol(rolRepo.findAll().stream()
            .filter(r -> r.getTipo() == TipoRol.COORDINADOR)
            .findFirst().orElseThrow());
        u.setActivo(true);
        usuarioService.save(u);
        ra.addFlashAttribute("mensaje", "Coordinador creado correctamente.");
        return "redirect:/admin/coordinadores";
    }

    @PostMapping("/coordinadores/{id}/editar")
    public String editarCoordinador(@PathVariable Long id,
                                    @RequestParam String nombre,
                                    @RequestParam String apellido,
                                    @RequestParam String email,
                                    RedirectAttributes ra) {
        usuarioService.findById(id).ifPresent(u -> {
            u.setNombre(nombre);
            u.setApellido(apellido);
            u.setEmail(email);
            usuarioService.save(u);
        });
        ra.addFlashAttribute("mensaje", "Coordinador actualizado.");
        return "redirect:/admin/coordinadores";
    }

    @PostMapping("/coordinadores/{id}/eliminar")
    public String eliminarCoordinador(@PathVariable Long id, RedirectAttributes ra) {
        usuarioService.deleteById(id);
        ra.addFlashAttribute("mensaje", "Coordinador eliminado.");
        return "redirect:/admin/coordinadores";
    }

    // ── CRUD FACULTADES ────────────────────────────────────────
    @GetMapping("/facultades")
    public String facultades(@AuthenticationPrincipal UserDetails ud, Model model) {
        addCommonAttrs(model, ud, "facultades");
        model.addAttribute("facultades", programaRepo.findAll());
        return "admin/facultades";
    }

    @PostMapping("/facultades/nuevo")
    public String nuevaFacultad(@RequestParam String nombre,
                                @RequestParam String codigo,
                                RedirectAttributes ra) {
        com.parcial.app.model.Programa p = new com.parcial.app.model.Programa();
        p.setNombre(nombre);
        p.setCodigo(codigo);
        p.setActivo(true);
        programaRepo.save(p);
        ra.addFlashAttribute("mensaje", "Facultad registrada correctamente.");
        return "redirect:/admin/facultades";
    }

    @PostMapping("/facultades/{id}/editar")
    public String editarFacultad(@PathVariable Long id,
                                 @RequestParam String nombre,
                                 @RequestParam String codigo,
                                 RedirectAttributes ra) {
        programaRepo.findById(id).ifPresent(p -> {
            p.setNombre(nombre);
            p.setCodigo(codigo);
            programaRepo.save(p);
        });
        ra.addFlashAttribute("mensaje", "Facultad actualizada.");
        return "redirect:/admin/facultades";
    }

    @PostMapping("/facultades/{id}/eliminar")
    public String eliminarFacultad(@PathVariable Long id, RedirectAttributes ra) {
        programaRepo.deleteById(id);
        ra.addFlashAttribute("mensaje", "Facultad eliminada.");
        return "redirect:/admin/facultades";
    }

    // ── RESOLUCIÓN BENEFICIOS TyI ──────────────────────────────
    @GetMapping("/beneficios")
    public String beneficios(@AuthenticationPrincipal UserDetails ud, Model model) {
        addCommonAttrs(model, ud, "beneficios");
        return "admin/beneficios";
    }

    @PostMapping("/beneficios/guardar")
    public String guardarBeneficios(RedirectAttributes ra) {
        ra.addFlashAttribute("mensaje", "Resolución de beneficios guardada correctamente.");
        return "redirect:/admin/beneficios";
    }
}