package com.condominios.acceso.service;

import com.condominios.acceso.dto.SolicitudResponse;
import com.condominios.acceso.dto.VisitaSinQrRequest;
import com.condominios.acceso.entity.EstadoSolicitud;
import com.condominios.acceso.entity.Residente;
import com.condominios.acceso.entity.SolicitudVisita;
import com.condominios.acceso.entity.Visita;
import com.condominios.acceso.exception.RecursoNoEncontradoException;
import com.condominios.acceso.exception.ReglaNegocioException;
import com.condominios.acceso.repository.ResidenteRepository;
import com.condominios.acceso.repository.SolicitudVisitaRepository;
import com.condominios.acceso.repository.VisitaRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Flujo de visita sin QR: el guardia registra, el residente aprueba o rechaza. */
@Service
@RequiredArgsConstructor
public class SolicitudService {

    private static final int HORAS_VIGENCIA_PASE_TEMPORAL = 2;

    private final SolicitudVisitaRepository solicitudRepo;
    private final ResidenteRepository residenteRepo;
    private final VisitaRepository visitaRepo;
    private final PaseService paseService;

    @Transactional
    public SolicitudResponse crear(VisitaSinQrRequest req) {
        Residente anfitrion = residenteRepo.findById(req.residenteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Residente", req.residenteId()));

        Visita v = new Visita();
        v.setNombre(req.nombreVisitante());
        v.setTipo(req.tipo());
        v.setFotoUrl(req.fotoUrl());
        v.setAnfitrion(anfitrion);
        visitaRepo.save(v);

        SolicitudVisita s = new SolicitudVisita();
        s.setVisita(v);
        s.setResidente(anfitrion);
        s.setMotivo(req.motivo());
        return SolicitudResponse.from(solicitudRepo.save(s));
    }

    @Transactional(readOnly = true)
    public List<SolicitudResponse> listar(EstadoSolicitud estado) {
        List<SolicitudVisita> lista = estado == null
                ? solicitudRepo.findAllByOrderByCreadaEnDesc()
                : solicitudRepo.findByEstadoOrderByCreadaEnDesc(estado);
        return lista.stream().map(SolicitudResponse::from).toList();
    }

    @Transactional
    public SolicitudResponse resolver(Long id, boolean aprobada) {
        SolicitudVisita s = solicitudRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud", id));
        if (s.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new ReglaNegocioException("La solicitud ya fue resuelta (" + s.getEstado() + ")");
        }
        s.setResueltaEn(LocalDateTime.now());
        if (aprobada) {
            s.setEstado(EstadoSolicitud.APROBADA);
            s.setPase(paseService.crearPaseTemporal(s.getVisita(), HORAS_VIGENCIA_PASE_TEMPORAL));
        } else {
            s.setEstado(EstadoSolicitud.RECHAZADA);
        }
        return SolicitudResponse.from(solicitudRepo.save(s));
    }
}
