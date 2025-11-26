package com.example.tfgApi.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@Table(name = "equipoinfo")
public class InfoEquipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 1900, message = "El año de fundación no puede ser menor que 1900")
    @Max(value = 2025, message = "El año de fundación no puede ser mayor que 2025")
    private int anoFundacion;

    @Min(value = 10000, message = "La capacidad del estadio debe ser al menos 10,000")
    @Max(value = 80000, message = "La capacidad del estadio no puede ser mayor que 80,000")
    private int capacidadEstadio;

    @Min(value = 0, message = "El número de títulos no puede ser negativo")
    @Max(value = 30, message = "El número de títulos no puede superar los 30")
    private int titulos;

    @Min(value = 10, message = "El precio de entrada no puede ser menor que 10")
    @Max(value = 100, message = "El precio de entrada no puede ser mayor que 100")
    private int entradaPartido;

    // Relación OneToOne con Liga
    @OneToOne
    @JoinColumn(name = "liga_id", nullable = false, unique = true)
    @JsonIgnoreProperties({"infoEquipo"}) // evita bucle, pero muestra 'liga'
    private Liga liga;

    public int getAnoFundacion() {
        return anoFundacion;
    }

    public void setAnoFundacion(int anoFundacion) {
        this.anoFundacion = anoFundacion;
    }

    public int getCapacidadEstadio() {
        return capacidadEstadio;
    }

    public void setCapacidadEstadio(int capacidadEstadio) {
        this.capacidadEstadio = capacidadEstadio;
    }

    public int getTitulos() {
        return titulos;
    }

    public void setTitulos(int titulos) {
        this.titulos = titulos;
    }

    public int getEntradaPartido() {
        return entradaPartido;
    }

    public void setEntradaPartido(int entradaPartido) {
        this.entradaPartido = entradaPartido;
    }

    public Liga getLiga() {
        return liga;
    }

    public void setLiga(Liga liga) {
        this.liga = liga;
    }
}
