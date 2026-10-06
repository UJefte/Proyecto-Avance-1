package com.condominios.acceso.service;

import com.condominios.acceso.dto.ResidenteRequest;
import com.condominios.acceso.dto.ResidenteResponse;
import com.condominios.acceso.entity.Departamento;
import com.condominios.acceso.entity.Residente;
import com.condominios.acceso.exception.RecursoNoEncontradoException;
import com.condominios.acceso.exception.ReglaNegocioException;
import com.condominios.acceso.repository.DepartamentoRepository;
import com.condominios.acceso.repository.ResidenteRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResidenteService {

    private final ResidenteRepository residenteRepo;
    private final DepartamentoRepository departamentoRepo;

    @Transactional
    public ResidenteResponse crear(ResidenteRequest req) {
        if (residenteRepo.existsByCorreo(req.correo())) {
            throw new ReglaNegocioException("Ya existe un residente con ese correo");
        }
        Departamento depto = departamentoRepo.findById(req.departamentoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Departamento", req.departamentoId()));
        Residente r = new Residente();
        r.setNombre(req.nombre());
        r.setCorreo(req.correo());
        r.setDepartamento(depto);
        return ResidenteResponse.from(residenteRepo.save(r));
    }

    @Transactional(readOnly = true)
    public List<ResidenteResponse> listar() {
        return residenteRepo.findAll().stream().map(ResidenteResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ResidenteResponse obtener(Long id) {
        return ResidenteResponse.from(residenteRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Residente", id)));
    }
}
