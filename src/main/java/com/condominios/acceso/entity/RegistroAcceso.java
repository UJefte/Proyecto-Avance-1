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
@Table(name = "registros_acceso")
@Getter
@Setter
@NoArgsConstructor
public class RegistroAcceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Null cuando el código leído no existe. */
    @ManyToOne
    @JoinColumn(name = "pase_id")
    private PaseQR pase;

    @Column(nullable = false)
    private String codigoLeido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimiento movimiento;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResultadoAcceso resultado;

    private String motivo;
}
