package com.example.tfgApi.controller;

import com.example.tfgApi.model.Jugador;
import com.example.tfgApi.service.JugadorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jugador")
public class JugadorController {

    private final JugadorService jugadorService;

    public JugadorController(JugadorService jugadorService) {
        this.jugadorService = jugadorService;
    }

    @GetMapping()
    public List<Jugador> getJugador(){
        return jugadorService.getJugador();
    }

    @PostMapping()
    public Jugador postJugador(@Valid @RequestBody Jugador jugador){
        return jugadorService.postJugador(jugador);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> putJugador(@Valid @PathVariable Long id, @RequestBody Jugador jugador) {
        try {
            Jugador jugadorActualizado = jugadorService.putJugador(id, jugador);
            return ResponseEntity.ok(jugadorActualizado);
        } catch (IllegalArgumentException e) {
            // Si el ID o el equipo no existen, devuelve un mensaje de error
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    public void deleteJugador(@PathVariable Long id){
        jugadorService.deleteJugador(id);
    }

}
