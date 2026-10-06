package com.condominios.acceso.service;

import com.condominios.acceso.dto.AccesoResponse;
import com.condominios.acceso.entity.PaseQR;
import com.condominios.acceso.entity.RegistroAcceso;
import com.condominios.acceso.entity.ResultadoAcceso;
import com.condominios.acceso.entity.TipoMovimiento;
import com.condominios.acceso.entity.TipoPase;
import com.condominios.acceso.entity.Trabajador;
import com.condominios.acceso.repository.PaseQRRepository;
import com.condominios.acceso.repository.RegistroAccesoRepository;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccesoService {

    private final PaseQRRepository paseRepo;
    private final RegistroAccesoRepository registroRepo;

    /**
     * Procesa un QR escaneado en caseta. Si el pase ya tiene una entrada sin salida,
     * registra la salida; si no, valida y registra la entrada.
     */
    @Transactional
    public AccesoResponse validar(String codigo) {
        LocalDateTime ahora = LocalDateTime.now();

        Optional<PaseQR> encontrado = paseRepo.findByCodigo(codigo);
        if (encontrado.isEmpty()) {
            return guardar(null, codigo, TipoMovimiento.ENTRADA, ahora, ResultadoAcceso.DENEGADO, "Pase inexistente");
        }
        PaseQR pase = encontrado.get();

        if (!pase.isActivo()) {
            return guardar(pase, codigo, TipoMovimiento.ENTRADA, ahora, ResultadoAcceso.DENEGADO, "Pase desactivado");
        }

        Optional<RegistroAcceso> ultimo = registroRepo
                .findFirstByPaseAndResultadoOrderByFechaHoraDesc(pase, ResultadoAcceso.PERMITIDO);
        boolean estaAdentro = ultimo.isPresent() && ultimo.get().getMovimiento() == TipoMovimiento.ENTRADA;

        // La salida siempre se permite, aunque el pase ya haya vencido.
        if (estaAdentro) {
            return guardar(pase, codigo, TipoMovimiento.SALIDA, ahora, ResultadoAcceso.PERMITIDO, "Salida registrada");
        }

        String motivoDenegacion = motivoDenegacion(pase, ahora);
        if (motivoDenegacion != null) {
            return guardar(pase, codigo, TipoMovimiento.ENTRADA, ahora, ResultadoAcceso.DENEGADO, motivoDenegacion);
        }

        pase.setUsosActuales(pase.getUsosActuales() + 1);
        paseRepo.save(pase);
        return guardar(pase, codigo, TipoMovimiento.ENTRADA, ahora, ResultadoAcceso.PERMITIDO, "Entrada registrada");
    }

    @Transactional(readOnly = true)
    public List<AccesoResponse> bitacora() {
        return registroRepo.findAllByOrderByFechaHoraDesc().stream().map(AccesoResponse::from).toList();
    }

    /** Devuelve null si la entrada es válida; si no, el motivo de la denegación. */
    private String motivoDenegacion(PaseQR pase, LocalDateTime ahora) {
        if (pase.getVigenteDesde() != null && ahora.isBefore(pase.getVigenteDesde())) {
            return "Pase aún no vigente";
        }
        if (pase.getVigenteHasta() != null && ahora.isAfter(pase.getVigenteHasta())) {
            return "Pase vencido";
        }
        if (pase.getUsosMaximos() != null && pase.getUsosActuales() >= pase.getUsosMaximos()) {
            return "Pase agotado (usos máximos alcanzados)";
        }
        if (!diaPermitido(pase.getDiasPermitidos(), ahora)) {
            return "Día no permitido para este pase";
        }
        if (pase.getTipo() == TipoPase.TRABAJADOR && !dentroDeHorario(pase.getTrabajador(), ahora.toLocalTime())) {
            return "Fuera del horario permitido del trabajador";
        }
        return null;
    }

    private boolean diaPermitido(String diasPermitidos, LocalDateTime ahora) {
        if (diasPermitidos == null || diasPermitidos.isBlank()) {
            return true;
        }
        Set<String> dias = Arrays.stream(diasPermitidos.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .collect(Collectors.toSet());
        return dias.contains(ahora.getDayOfWeek().name());
    }

    private boolean dentroDeHorario(Trabajador t, LocalTime hora) {
        if (t == null) {
            return false;
        }
        LocalTime inicio = t.getHoraInicio();
        LocalTime fin = t.getHoraFin();
        if (!inicio.isAfter(fin)) {
            return !hora.isBefore(inicio) && !hora.isAfter(fin);
        }
        // Turno nocturno que cruza la medianoche
        return !hora.isBefore(inicio) || !hora.isAfter(fin);
    }

    private AccesoResponse guardar(PaseQR pase, String codigo, TipoMovimiento movimiento,
                                   LocalDateTime fechaHora, ResultadoAcceso resultado, String motivo) {
        RegistroAcceso r = new RegistroAcceso();
        r.setPase(pase);
        r.setCodigoLeido(codigo);
        r.setMovimiento(movimiento);
        r.setFechaHora(fechaHora);
        r.setResultado(resultado);
        r.setMotivo(motivo);
        return AccesoResponse.from(registroRepo.save(r));
    }
}
