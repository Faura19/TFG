package com.example.tfgApi.service;

import com.example.tfgApi.model.Jugador;
import com.example.tfgApi.model.Liga;
import com.example.tfgApi.repository.JugadorRepository;
import com.example.tfgApi.repository.LigaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JugadorService {

    @Autowired
    private final JugadorRepository jugadorRepository;

    @Autowired
    private final LigaRepository ligaRepository;

    public JugadorService(JugadorRepository jugadorRepository, LigaRepository ligaRepository) {
        this.jugadorRepository = jugadorRepository;
        this.ligaRepository = ligaRepository;
    }

    public List<Jugador> getJugador(){
        return jugadorRepository.findAll();
    }

    // ---------- POST ----------
    public Jugador postJugador(Jugador jugador) {
        if (jugador.getLiga() == null || jugador.getLiga().getNombreEquipo() == null) {
            throw new IllegalArgumentException("Debes indicar el nombre del equipo al que pertenece el jugador.");
        }

        // Buscar la liga por nombre
        Optional<Liga> ligaOpt = ligaRepository.findByNombreEquipo(jugador.getLiga().getNombreEquipo());

        if (ligaOpt.isEmpty()) {
            throw new IllegalArgumentException("El equipo '" + jugador.getLiga().getNombreEquipo() + "' no existe en la base de datos.");
        }

        // Asociar la liga encontrada al jugador
        jugador.setLiga(ligaOpt.get());

        // 🔍 Verificar si ya existe un jugador con el mismo nombre en este equipo
        boolean existe = jugadorRepository.existsByNombreJugadorAndLiga_NombreEquipo(
                jugador.getNombreJugador(),
                jugador.getLiga().getNombreEquipo()
        );

        if (existe) {
            throw new IllegalArgumentException("Ya existe un jugador con ese nombre en el equipo " + jugador.getLiga().getNombreEquipo() + ".");
        }

        // Guardar el jugador si no hay duplicados
        return jugadorRepository.save(jugador);
    }


    // ---------- PUT ----------
    public Jugador putJugador(Long id, Jugador jugadorActualizado) {
        Jugador jugadorExistente = jugadorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró ningún jugador con el ID: " + id));

        if (jugadorActualizado.getLiga() == null || jugadorActualizado.getLiga().getNombreEquipo() == null) {
            throw new IllegalArgumentException("Debes indicar el nombre del equipo al que pertenece el jugador.");
        }

        // Buscar la liga por nombre
        Optional<Liga> ligaOpt = ligaRepository.findByNombreEquipo(jugadorActualizado.getLiga().getNombreEquipo());

        if (ligaOpt.isEmpty()) {
            throw new IllegalArgumentException("El equipo '" + jugadorActualizado.getLiga().getNombreEquipo() + "' no existe en la base de datos.");
        }

        // Actualizar datos del jugador
        jugadorExistente.setNombreJugador(jugadorActualizado.getNombreJugador());
        jugadorExistente.setDorsalJugador(jugadorActualizado.getDorsalJugador());
        jugadorExistente.setValorJugador(jugadorActualizado.getValorJugador());
        jugadorExistente.setPosicionJugador(jugadorActualizado.getPosicionJugador());
        jugadorExistente.setEdadJugador(jugadorActualizado.getEdadJugador());
        jugadorExistente.setLiga(ligaOpt.get());

        return jugadorRepository.save(jugadorExistente);
    }

    public void deleteJugador(Long id){
        jugadorRepository.deleteById(id);
    }


}
