package com.condominios.acceso.web;

import com.condominios.acceso.dto.AccesoResponse;
import com.condominios.acceso.dto.ResidenteRequest;
import com.condominios.acceso.entity.EstadoSolicitud;
import com.condominios.acceso.exception.RecursoNoEncontradoException;
import com.condominios.acceso.exception.ReglaNegocioException;
import com.condominios.acceso.service.AccesoService;
import com.condominios.acceso.service.DepartamentoService;
import com.condominios.acceso.service.PaseService;
import com.condominios.acceso.service.ResidenteService;
import com.condominios.acceso.service.SolicitudService;
import com.condominios.acceso.service.TrabajadorService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.io.IOException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Pantallas web del panel de administracion.
 * Aqui no va la logica, solo se llaman los mismos services que usa el API.
 */
@Controller
@RequiredArgsConstructor
public class PanelController {

    private final DepartamentoService departamentoService;
    private final ResidenteService residenteService;
    private final TrabajadorService trabajadorService;
    private final PaseService paseService;
    private final AccesoService accesoService;
    private final SolicitudService solicitudService;
    private final Validator validator;

    @GetMapping("/")
    public String raiz() {
        return "redirect:/panel";
    }

    @GetMapping("/panel")
    public String inicio(Model model) {
        model.addAttribute("totalResidentes", residenteService.listar().size());
        model.addAttribute("pasesActivos", paseService.listar().stream().filter(p -> p.activo()).count());
        model.addAttribute("accesosHoy", accesoService.accesosDeHoy());
        model.addAttribute("pendientes", solicitudService.listar(EstadoSolicitud.PENDIENTE).size());
        return "panel/inicio";
    }

    // ---------- residentes ----------

    @GetMapping("/panel/residentes")
    public String residentes(Model model) {
        model.addAttribute("residentes", residenteService.listar());
        // los departamentos salen de la base para escogerlos de una lista
        model.addAttribute("departamentos", departamentoService.listar());
        return "panel/residentes";
    }

    @PostMapping("/panel/residentes")
    public String crearResidente(@RequestParam String nombre, @RequestParam String correo,
                                 @RequestParam Long departamentoId, RedirectAttributes flash) {
        ResidenteRequest req = new ResidenteRequest(nombre.trim(), correo.trim(), departamentoId);
        // las mismas validaciones que el API (por ejemplo que el correo este bien escrito)
        Set<ConstraintViolation<ResidenteRequest>> errores = validator.validate(req);
        if (!errores.isEmpty()) {
            ConstraintViolation<ResidenteRequest> primero = errores.iterator().next();
            flash.addFlashAttribute("error", "El campo " + primero.getPropertyPath() + " " + primero.getMessage());
            return "redirect:/panel/residentes";
        }
        try {
            residenteService.crear(req);
            flash.addFlashAttribute("ok", "Residente registrado");
        } catch (ReglaNegocioException | RecursoNoEncontradoException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/panel/residentes";
    }

    // ---------- pases ----------

    @GetMapping("/panel/pases")
    public String pases(Model model) {
        model.addAttribute("pases", paseService.listar());
        model.addAttribute("residentes", residenteService.listar());
        model.addAttribute("trabajadores", trabajadorService.listar());
        return "panel/pases";
    }

    @PostMapping("/panel/pases/residente")
    public String paseResidente(@RequestParam Long residenteId, RedirectAttributes flash) {
        paseService.crearParaResidente(residenteId);
        flash.addFlashAttribute("ok", "Pase generado");
        return "redirect:/panel/pases";
    }

    @PostMapping("/panel/pases/trabajador")
    public String paseTrabajador(@RequestParam Long trabajadorId, RedirectAttributes flash) {
        paseService.crearParaTrabajador(trabajadorId);
        flash.addFlashAttribute("ok", "Pase generado");
        return "redirect:/panel/pases";
    }

    @PostMapping("/panel/pases/{id}/desactivar")
    public String desactivar(@PathVariable Long id, RedirectAttributes flash) {
        paseService.desactivar(id);
        flash.addFlashAttribute("ok", "Pase desactivado");
        return "redirect:/panel/pases";
    }

    // ---------- caseta (lo que usa el guardia) ----------

    @GetMapping("/panel/caseta")
    public String caseta() {
        return "panel/caseta";
    }

    @PostMapping("/panel/caseta")
    public String escanear(@RequestParam String codigo, Model model) {
        model.addAttribute("resultado", accesoService.validar(codigo.trim()));
        return "panel/caseta";
    }

    // pagina que abre la camara y lee el QR con JavaScript (static/js/escaner.js)
    @GetMapping("/panel/escanear")
    public String paginaEscaner() {
        return "panel/escanear";
    }

    @PostMapping("/panel/caseta/imagen")
    public String escanearFoto(@RequestParam("imagen") MultipartFile imagen, Model model) throws IOException {
        try {
            AccesoResponse r = accesoService.validarImagen(imagen.getInputStream());
            model.addAttribute("resultado", r);
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "panel/caseta";
    }

    // ---------- bitacora ----------

    @GetMapping("/panel/bitacora")
    public String bitacora(Model model) {
        model.addAttribute("registros", accesoService.bitacora());
        return "panel/bitacora";
    }
}
