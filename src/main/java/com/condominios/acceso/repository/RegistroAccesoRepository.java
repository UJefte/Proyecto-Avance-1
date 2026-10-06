package com.condominios.acceso.repository;

import com.condominios.acceso.entity.PaseQR;
import com.condominios.acceso.entity.RegistroAcceso;
import com.condominios.acceso.entity.ResultadoAcceso;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistroAccesoRepository extends JpaRepository<RegistroAcceso, Long> {

    Optional<RegistroAcceso> findFirstByPaseAndResultadoOrderByFechaHoraDesc(PaseQR pase, ResultadoAcceso resultado);

    List<RegistroAcceso> findAllByOrderByFechaHoraDesc();
}
