package com.example.tfgApi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "liga")
@Table(
        name = "jugador",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"nombreJugador", "nombre_equipo"})
        }
)
public class Jugador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreJugador;

    @Min(value = 1, message = "El dorsal mas bajo es el 1")
    @Max(value = 99, message = "El dorsal mas alto es el 99")
    private int dorsalJugador;

    @Min(value = 1, message = "El valor mas bajo es el 1")
    @Max(value = 99, message = "El valor mas alto es el 99")
    private int valorJugador;

    @Pattern(regexp = "^(POR|DEF|MC|DC)$", message = "La posición debe ser POR, DEF, MC o DC")
    private String posicionJugador;

    @Min(value = 16, message = "La edad minima es de 16")
    @Max(value = 35, message = "La edad maxima es de 35")
    private int edadJugador;

    // Relación con Liga usando nombreEquipo en lugar de id
    @ManyToOne
    @JoinColumn(name = "nombre_equipo", referencedColumnName = "nombreEquipo")
    private Liga liga;

    public String getNombreJugador() {
        return nombreJugador;
    }

    public void setNombreJugador(String nombreJugador) {
        this.nombreJugador = nombreJugador;
    }

    public int getDorsalJugador() {
        return dorsalJugador;
    }

    public void setDorsalJugador(int dorsalJugador) {
        this.dorsalJugador = dorsalJugador;
    }

    public int getValorJugador() {
        return valorJugador;
    }

    public void setValorJugador(int valorJugador) {
        this.valorJugador = valorJugador;
    }

    public String getPosicionJugador() {
        return posicionJugador;
    }

    public void setPosicionJugador(String posicionJugador) {
        this.posicionJugador = posicionJugador;
    }

    public int getEdadJugador() {
        return edadJugador;
    }

    public void setEdadJugador(int edadJugador) {
        this.edadJugador = edadJugador;
    }

    public Liga getLiga() {
        return liga;
    }

    public void setLiga(Liga liga) {
        this.liga = liga;
    }
}
