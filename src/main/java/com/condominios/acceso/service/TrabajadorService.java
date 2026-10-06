package com.condominios.acceso.service;

import com.condominios.acceso.dto.TrabajadorRequest;
import com.condominios.acceso.dto.TrabajadorResponse;
import com.condominios.acceso.entity.Trabajador;
import com.condominios.acceso.repository.TrabajadorRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TrabajadorService {

    private final TrabajadorRepository repo;

    @Transactional
    public TrabajadorResponse crear(TrabajadorRequest req) {
        Trabajador t = new Trabajador();
        t.setNombre(req.nombre());
        t.setPuesto(req.puesto());
        t.setHoraInicio(req.horaInicio());
        t.setHoraFin(req.horaFin());
        return TrabajadorResponse.from(repo.save(t));
    }

    @Transactional(readOnly = true)
    public List<TrabajadorResponse> listar() {
        return repo.findAll().stream().map(TrabajadorResponse::from).toList();
    }
}
