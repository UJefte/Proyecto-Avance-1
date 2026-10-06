package com.condominios.acceso.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Visita sin QR: el guardia la registra y el residente la aprueba o rechaza. */
@Entity
@Table(name = "solicitudes_visita")
@Getter
@Setter
@NoArgsConstructor
public class SolicitudVisita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "visita_id")
    private Visita visita;

    @ManyToOne(optional = false)
    @JoinColumn(name = "residente_id")
    private Residente residente;

    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    @Column(nullable = false)
    private LocalDateTime creadaEn = LocalDateTime.now();

    private LocalDateTime resueltaEn;

    /** Pase temporal generado al aprobar. */
    @ManyToOne
    @JoinColumn(name = "pase_id")
    private PaseQR pase;
}
