package com.parcial.app.controller;

import com.parcial.app.model.Estudiante;
import com.parcial.app.model.Resultado;
import com.parcial.app.model.Usuario;
import com.parcial.app.model.enums.EstadoInscripcion;
import com.parcial.app.service.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/estudiante")
public class EstudianteController {

    private final UsuarioService      usuarioService;
    private final EstudianteService   estudianteService;
    private final ResultadoService    resultadoService;
    private final ConvocatoriaService convocatoriaService;

    // Carpeta donde se guardan los comprobantes (dentro de static para que sean accesibles)
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/comprobantes/";

    public EstudianteController(UsuarioService us, EstudianteService es,
                                ResultadoService rs, ConvocatoriaService cs) {
        this.usuarioService      = us;
        this.estudianteService   = es;
        this.resultadoService    = rs;
        this.convocatoriaService = cs;
    }

    private Usuario getUsuario(UserDetails ud) {
        return usuarioService.findByEmail(ud.getUsername()).orElseThrow();
    }
    private Estudiante getEstudiante(UserDetails ud) {
        Usuario u = getUsuario(ud);
        return estudianteService.findByEmail(u.getEmail()).orElse(null);
    }
    private String nivel(int p) {
        if (p >= 241) return "Superior";
        if (p >= 201) return "Alto";
        if (p >= 161) return "Medio";
        if (p >= 121) return "Bajo";
        return "Insuficiente";
    }
    private String nivelIngles(int p) {
        if (p >= 90) return "C1";
        if (p >= 70) return "B2";
        if (p >= 50) return "B1";
        if (p >= 30) return "A2";
        return "A1";
    }

    // ── DASHBOARD ────────────────────────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails ud, Model model) {
        Usuario u    = getUsuario(ud);
        Estudiante e = getEstudiante(ud);
        model.addAttribute("usuario",    u);
        model.addAttribute("estudiante", e);

        int puntaje = 0;
        int total   = 0;
        if (e != null) {
            List<Resultado> rs = resultadoService.findByEstudiante(e);
            total   = rs.size();
            puntaje = rs.isEmpty() || rs.get(0).getPuntaje() == null ? 0 : rs.get(0).getPuntaje();
        }
        model.addAttribute("ultimoPuntaje",   puntaje);
        model.addAttribute("nivelPuntaje",    nivel(puntaje));
        model.addAttribute("totalResultados", total);
        model.addAttribute("convocatoria",    convocatoriaService.findProxima().orElse(null));
        return "dashboard/estudiante";
    }

    // ── IDENTIFICACIÓN ────────────────────────────────────────────
    @GetMapping("/identificacion")
    public String identificacion(@AuthenticationPrincipal UserDetails ud, Model model) {
        model.addAttribute("usuario",    getUsuario(ud));
        model.addAttribute("estudiante", getEstudiante(ud));
        model.addAttribute("active",     "identificacion");
        return "estudiante/identificacion";
    }

    // ── MI ÚLTIMO RESULTADO / CARGUE PAGO ────────────────────────
    @GetMapping("/mi-resultado")
    public String miResultado(@AuthenticationPrincipal UserDetails ud, Model model) {
        Usuario u    = getUsuario(ud);
        Estudiante e = getEstudiante(ud);
        model.addAttribute("usuario",    u);
        model.addAttribute("estudiante", e);
        model.addAttribute("active",     "mi-resultado");

        if (e != null) {
            resultadoService.findUltimo(e).ifPresent(r -> {
                int p = r.getPuntaje() != null ? r.getPuntaje() : 0;
                model.addAttribute("resultado",   r);
                model.addAttribute("nivel",       nivel(p));
                if (r.getPuntajeIngles() != null)
                    model.addAttribute("nivelIngles", nivelIngles(r.getPuntajeIngles()));
            });
        }
        return "estudiante/mi-resultado";
    }

    @PostMapping("/mi-resultado/cargar")
    public String cargarPago(@AuthenticationPrincipal UserDetails ud,
                             @RequestParam("archivo") MultipartFile archivo,
                             RedirectAttributes ra) {
        if (archivo.isEmpty()) {
            ra.addFlashAttribute("error", "Selecciona un archivo antes de subir.");
            return "redirect:/estudiante/mi-resultado";
        }

        Estudiante e = getEstudiante(ud);
        if (e == null) {
            ra.addFlashAttribute("error", "No se encontró tu perfil de estudiante.");
            return "redirect:/estudiante/mi-resultado";
        }

        try {
            // Crear carpeta si no existe
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

            // Nombre único para evitar colisiones
            String ext      = "";
            String original = archivo.getOriginalFilename();
            if (original != null && original.contains("."))
                ext = original.substring(original.lastIndexOf("."));
            String nombreArchivo = "comprobante_" + e.getDocumento() + "_"
                                 + UUID.randomUUID().toString().substring(0, 8) + ext;

            // Guardar archivo
            Path destino = uploadPath.resolve(nombreArchivo);
            Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

            // Guardar nombre en BD
            e.setComprobantePago(nombreArchivo);
            estudianteService.save(e);

            ra.addFlashAttribute("mensaje",
                "Comprobante subido correctamente. El coordinador revisará tu solicitud.");
        } catch (IOException ex) {
            ra.addFlashAttribute("error", "Error al subir el archivo: " + ex.getMessage());
        }

        return "redirect:/estudiante/mi-resultado";
    }

    // ── TODOS MIS RESULTADOS ──────────────────────────────────────
    @GetMapping("/mis-resultados")
    public String misResultados(@AuthenticationPrincipal UserDetails ud, Model model) {
        Usuario u    = getUsuario(ud);
        Estudiante e = getEstudiante(ud);
        model.addAttribute("usuario",    u);
        model.addAttribute("estudiante", e);
        model.addAttribute("active",     "mis-resultados");
        model.addAttribute("resultados", e != null
            ? resultadoService.findByEstudiante(e) : List.of());
        return "estudiante/mis-resultados";
    }
    
 // ── RESULTADO ÚNICO DETALLADO ─────────────────────────────────
    @GetMapping("/resultado/{id}")
    public String resultadoDetalle(@AuthenticationPrincipal UserDetails ud,
                                   @PathVariable Long id,
                                   Model model) {
        Usuario u    = getUsuario(ud);
        Estudiante e = getEstudiante(ud);
        model.addAttribute("usuario",    u);
        model.addAttribute("estudiante", e);
        model.addAttribute("active",     "mis-resultados");

        if (e != null) {
            resultadoService.findByEstudiante(e).stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .ifPresent(r -> {
                    int p = r.getPuntaje() != null ? r.getPuntaje() : 0;
                    model.addAttribute("resultado",   r);
                    model.addAttribute("nivel",       nivel(p));
                    if (r.getPuntajeIngles() != null)
                        model.addAttribute("nivelIngles", nivelIngles(r.getPuntajeIngles()));
                });
        }
        return "estudiante/resultado-detalle";
    }

    // ── BENEFICIOS OBTENIDOS ──────────────────────────────────────
    @GetMapping("/beneficios")
    public String beneficios(@AuthenticationPrincipal UserDetails ud, Model model) {
        Usuario u    = getUsuario(ud);
        Estudiante e = getEstudiante(ud);
        model.addAttribute("usuario",    u);
        model.addAttribute("estudiante", e);
        model.addAttribute("active",     "beneficios");

        boolean habilitado = e != null
            && e.getEstadoInscripcion() == EstadoInscripcion.HABILITADO;
        int puntaje = 0;
        if (e != null) {
            List<Resultado> rs = resultadoService.findByEstudiante(e);
            if (!rs.isEmpty() && rs.get(0).getPuntaje() != null)
                puntaje = rs.get(0).getPuntaje();
        }
        model.addAttribute("habilitado", habilitado);
        model.addAttribute("tieneBeca",  habilitado && puntaje >= 241);
        model.addAttribute("puntaje",    puntaje);
        model.addAttribute("nivel",      nivel(puntaje));
        return "estudiante/beneficios";
    }

    // ── RESOLUCIÓN BENEFICIOS TyI ─────────────────────────────────
    @GetMapping("/resolucion")
    public String resolucion(@AuthenticationPrincipal UserDetails ud, Model model) {
        model.addAttribute("usuario",    getUsuario(ud));
        model.addAttribute("estudiante", getEstudiante(ud));
        model.addAttribute("active",     "resolucion");
        return "estudiante/resolucion";
    }
}