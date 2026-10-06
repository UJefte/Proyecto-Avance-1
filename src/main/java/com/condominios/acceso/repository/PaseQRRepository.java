package com.condominios.acceso.repository;

import com.condominios.acceso.entity.PaseQR;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaseQRRepository extends JpaRepository<PaseQR, Long> {

    Optional<PaseQR> findByCodigo(String codigo);
}
