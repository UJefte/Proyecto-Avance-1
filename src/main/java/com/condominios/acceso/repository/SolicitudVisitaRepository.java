package com.condominios.acceso.repository;

import com.condominios.acceso.entity.EstadoSolicitud;
import com.condominios.acceso.entity.SolicitudVisita;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitudVisitaRepository extends JpaRepository<SolicitudVisita, Long> {

    List<SolicitudVisita> findByEstadoOrderByCreadaEnDesc(EstadoSolicitud estado);

    List<SolicitudVisita> findAllByOrderByCreadaEnDesc();
}
