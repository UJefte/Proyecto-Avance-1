package com.condominios.acceso.repository;

import com.condominios.acceso.entity.PaseQR;
import com.condominios.acceso.entity.TipoPase;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaseQRRepository extends JpaRepository<PaseQR, Long> {

    Optional<PaseQR> findByCodigo(String codigo);

    List<PaseQR> findByResidenteIdAndTipoAndActivoTrue(Long residenteId, TipoPase tipo);

    List<PaseQR> findByTrabajadorIdAndActivoTrue(Long trabajadorId);
}
