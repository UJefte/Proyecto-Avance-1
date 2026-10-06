package com.condominios.acceso.repository;

import com.condominios.acceso.entity.Residente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResidenteRepository extends JpaRepository<Residente, Long> {

    boolean existsByCorreo(String correo);
}
