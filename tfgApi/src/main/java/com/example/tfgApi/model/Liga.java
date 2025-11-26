package com.example.tfgApi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "liga")
public class Liga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombreEquipo;

    @Min(value = 0, message = "No puedes tener menos de 0 puntos")
    private int puntosEquipo;

    // Relación OneToMany con Jugador
    @OneToMany(mappedBy = "liga", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Jugador> jugadores;

    @OneToMany(mappedBy = "liga", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Partidos> partidos;

    // Nueva relación OneToOne con InfoEquipo
    @OneToOne(mappedBy = "liga", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"liga"})
    private InfoEquipo infoEquipo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public void setNombreEquipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
    }

    public int getPuntosEquipo() {
        return puntosEquipo;
    }

    public void setPuntosEquipo(int puntosEquipo) {
        this.puntosEquipo = puntosEquipo;
    }

    public InfoEquipo getInfoEquipo() {
        return infoEquipo;
    }

    public void setInfoEquipo(InfoEquipo infoEquipo) {
        this.infoEquipo = infoEquipo;
    }
}
