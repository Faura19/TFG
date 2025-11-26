package com.example.tfgApi.repository;

import com.example.tfgApi.model.Jugador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JugadorRepository extends JpaRepository<Jugador,Long> {
    boolean existsByNombreJugadorAndLiga_NombreEquipo(String nombreJugador, String nombreEquipo);
}
