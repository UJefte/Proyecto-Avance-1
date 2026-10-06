package com.condominios.acceso.service;

import com.condominios.acceso.dto.PaseResponse;
import com.condominios.acceso.dto.PaseVisitaRequest;
import com.condominios.acceso.entity.PaseQR;
import com.condominios.acceso.entity.Residente;
import com.condominios.acceso.entity.TipoPase;
import com.condominios.acceso.entity.Trabajador;
import com.condominios.acceso.entity.Visita;
import com.condominios.acceso.exception.RecursoNoEncontradoException;
import com.condominios.acceso.exception.ReglaNegocioException;
import com.condominios.acceso.repository.PaseQRRepository;
import com.condominios.acceso.repository.ResidenteRepository;
import com.condominios.acceso.repository.TrabajadorRepository;
import com.condominios.acceso.repository.VisitaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaseService {

    private final PaseQRRepository paseRepo;
    private final ResidenteRepository residenteRepo;
    private final TrabajadorRepository trabajadorRepo;
    private final VisitaRepository visitaRepo;

    @Transactional
    public PaseResponse crearParaResidente(Long residenteId) {
        Residente r = residenteRepo.findById(residenteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Residente", residenteId));
        PaseQR pase = nuevoPase(TipoPase.RESIDENTE);
        pase.setResidente(r);
        return PaseResponse.from(paseRepo.save(pase));
    }

    @Transactional
    public PaseResponse crearParaTrabajador(Long trabajadorId) {
        Trabajador t = trabajadorRepo.findById(trabajadorId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Trabajador", trabajadorId));
        PaseQR pase = nuevoPase(TipoPase.TRABAJADOR);
        pase.setTrabajador(t);
        return PaseResponse.from(paseRepo.save(pase));
    }

    @Transactional
    public PaseResponse crearParaVisita(PaseVisitaRequest req) {
        Residente anfitrion = residenteRepo.findById(req.residenteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Residente", req.residenteId()));
        if (!req.vigenteHasta().isAfter(req.vigenteDesde())) {
            throw new ReglaNegocioException("vigenteHasta debe ser posterior a vigenteDesde");
        }
        Visita v = new Visita();
        v.setNombre(req.nombreVisitante());
        v.setCorreo(req.correoVisitante());
        v.setTipo(req.tipo());
        v.setAnfitrion(anfitrion);
        visitaRepo.save(v);

        PaseQR pase = nuevoPase(TipoPase.VISITA);
        pase.setVisita(v);
        pase.setResidente(anfitrion);
        pase.setVigenteDesde(req.vigenteDesde());
        pase.setVigenteHasta(req.vigenteHasta());
        pase.setUsosMaximos(req.usosMaximos());
        pase.setDiasPermitidos(req.diasPermitidos());
        return PaseResponse.from(paseRepo.save(pase));
    }

    /** Pase de un solo uso para una visita aprobada en caseta. */
    @Transactional
    public PaseQR crearPaseTemporal(Visita visita, int horasVigencia) {
        PaseQR pase = nuevoPase(TipoPase.VISITA);
        pase.setVisita(visita);
        pase.setResidente(visita.getAnfitrion());
        pase.setVigenteDesde(LocalDateTime.now());
        pase.setVigenteHasta(LocalDateTime.now().plusHours(horasVigencia));
        pase.setUsosMaximos(1);
        return paseRepo.save(pase);
    }

    @Transactional(readOnly = true)
    public List<PaseResponse> listar() {
        return paseRepo.findAll().stream().map(PaseResponse::from).toList();
    }

    @Transactional
    public PaseResponse desactivar(Long id) {
        PaseQR pase = paseRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pase", id));
        pase.setActivo(false);
        return PaseResponse.from(paseRepo.save(pase));
    }

    private PaseQR nuevoPase(TipoPase tipo) {
        PaseQR pase = new PaseQR();
        pase.setCodigo(UUID.randomUUID().toString());
        pase.setTipo(tipo);
        return pase;
    }
}
