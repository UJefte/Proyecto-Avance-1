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

@Entity
@Table(name = "pases_qr")
@Getter
@Setter
@NoArgsConstructor
public class PaseQR {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Valor que va dentro del QR. */
    @Column(nullable = false, unique = true, length = 36)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPase tipo;

    @ManyToOne
    @JoinColumn(name = "residente_id")
    private Residente residente;

    @ManyToOne
    @JoinColumn(name = "trabajador_id")
    private Trabajador trabajador;

    @ManyToOne
    @JoinColumn(name = "visita_id")
    private Visita visita;

    /** Null en pases permanentes (residente y trabajador). */
    private LocalDateTime vigenteDesde;
    private LocalDateTime vigenteHasta;

    /** Máximo de entradas. Null = ilimitado. Un pase único tiene 1. */
    private Integer usosMaximos;

    @Column(nullable = false)
    private int usosActuales = 0;

    /** Días permitidos separados por coma, ej. "MONDAY,FRIDAY". Vacío = todos. */
    private String diasPermitidos;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(nullable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    public String nombreTitular() {
        return switch (tipo) {
            case RESIDENTE -> residente != null ? residente.getNombre() : null;
            case TRABAJADOR -> trabajador != null ? trabajador.getNombre() : null;
            case VISITA -> visita != null ? visita.getNombre() : null;
        };
    }
}
