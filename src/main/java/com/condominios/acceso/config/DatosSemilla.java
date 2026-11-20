package com.condominios.acceso.config;

import com.condominios.acceso.dto.DepartamentoRequest;
import com.condominios.acceso.dto.PaseResponse;
import com.condominios.acceso.dto.ResidenteRequest;
import com.condominios.acceso.dto.TrabajadorRequest;
import com.condominios.acceso.repository.DepartamentoRepository;
import com.condominios.acceso.service.DepartamentoService;
import com.condominios.acceso.service.PaseService;
import com.condominios.acceso.service.ResidenteService;
import com.condominios.acceso.service.TrabajadorService;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Carga datos de ejemplo la primera vez que arranca el sistema,
 * asi la base no esta vacia para la demo ni para las pruebas.
 * Se apaga con DATOS_SEMILLA=false.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.datos-semilla", havingValue = "true")
public class DatosSemilla implements CommandLineRunner {

    private final DepartamentoRepository departamentoRepo;
    private final DepartamentoService departamentoService;
    private final ResidenteService residenteService;
    private final TrabajadorService trabajadorService;
    private final PaseService paseService;

    @Override
    public void run(String... args) {
        // si ya hay departamentos, ya se cargaron antes
        if (departamentoRepo.count() > 0) {
            return;
        }

        Long a101 = departamentoService.crear(new DepartamentoRequest("101", "A")).id();
        departamentoService.crear(new DepartamentoRequest("102", "A"));
        Long b201 = departamentoService.crear(new DepartamentoRequest("201", "B")).id();

        Long ana = residenteService.crear(new ResidenteRequest("Ana Lopez", "ana@correo.com", a101)).id();
        Long luis = residenteService.crear(new ResidenteRequest("Luis Hernandez", "luis@correo.com", b201)).id();

        Long pedro = trabajadorService.crear(new TrabajadorRequest(
                "Pedro Ramirez", "Jardinero", LocalTime.of(7, 0), LocalTime.of(18, 0))).id();

        PaseResponse paseAna = paseService.crearParaResidente(ana);
        paseService.crearParaResidente(luis);
        paseService.crearParaTrabajador(pedro);

        log.info("Datos de ejemplo cargados. Codigo QR de Ana Lopez: {}", paseAna.codigo());
    }
}
