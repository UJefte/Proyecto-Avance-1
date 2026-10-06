package com.condominios.acceso.service;

import com.condominios.acceso.dto.DepartamentoRequest;
import com.condominios.acceso.dto.DepartamentoResponse;
import com.condominios.acceso.entity.Departamento;
import com.condominios.acceso.repository.DepartamentoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DepartamentoService {

    private final DepartamentoRepository repo;

    @Transactional
    public DepartamentoResponse crear(DepartamentoRequest req) {
        Departamento d = new Departamento();
        d.setNumero(req.numero());
        d.setTorre(req.torre());
        return DepartamentoResponse.from(repo.save(d));
    }

    @Transactional(readOnly = true)
    public List<DepartamentoResponse> listar() {
        return repo.findAll().stream().map(DepartamentoResponse::from).toList();
    }
}
