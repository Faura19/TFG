package com.example.tfgApi.controller;

import com.example.tfgApi.model.Partidos;
import com.example.tfgApi.service.PartidosService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/partidos")
public class PartidosController {

    private final PartidosService partidosService;

    public PartidosController(PartidosService partidosService) {
        this.partidosService = partidosService;
    }

    @GetMapping()
    public List<Partidos> getPartidos(){
        return partidosService.getPartidos();
    }

    @PostMapping()
    public Partidos postPartidos(@Valid @RequestBody Partidos partidos){
        return partidosService.postPartidos(partidos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Partidos> updatePartido(@Valid @PathVariable Long id, @RequestBody Partidos datos) {
        Partidos actualizado = partidosService.updatePartido(id, datos);
        return ResponseEntity.ok(actualizado);
    }


    @DeleteMapping("/{id}")
    public void deletePartidos(@PathVariable Long id){
        partidosService.deletePartidos(id);
    }



}
