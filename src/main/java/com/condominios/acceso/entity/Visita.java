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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "visitas")
@Getter
@Setter
@NoArgsConstructor
public class Visita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String correo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoVisita tipo;

    /** Ruta o URL de la foto de la identificación (la subida de archivos llega en la siguiente entrega). */
    private String fotoUrl;

    @ManyToOne(optional = false)
    @JoinColumn(name = "anfitrion_id")
    private Residente anfitrion;
}
