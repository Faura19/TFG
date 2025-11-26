package com.example.tfgApi.controller;

import com.example.tfgApi.model.Liga;
import com.example.tfgApi.repository.LigaRepository;
import com.example.tfgApi.service.LigaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/liga")
@CrossOrigin(origins = "*")
public class LigaController {

    private final LigaService ligaService;

    private final LigaRepository ligaRepository;

    public LigaController(LigaService ligaService, LigaRepository ligaRepository) {
        this.ligaService = ligaService;
        this.ligaRepository = ligaRepository;
    }

    @GetMapping()
    public List<Liga> getLiga(){
        return ligaService.getLiga();
    }

    @PostMapping()
    public Liga postLiga(@Valid @RequestBody Liga liga){
        return ligaService.postLiga(liga);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarLiga(
            @PathVariable Long id,
            @Valid @RequestBody Liga nuevosDatos) {

        Optional<Liga> optLiga = ligaRepository.findById(id);
        if (optLiga.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Liga existente = optLiga.get();

        // 🔄 Actualizamos solo los campos necesarios
        existente.setNombreEquipo(nuevosDatos.getNombreEquipo());
        existente.setPuntosEquipo(nuevosDatos.getPuntosEquipo());

        // ⚠️ No tocamos el ID (Spring/JPA lo mantiene igual)
        Liga actualizada = ligaRepository.save(existente);

        return ResponseEntity.ok(actualizada);
    }


    @DeleteMapping("/{id}")
    public void deleteLiga(@PathVariable Long id){
        ligaService.deleteLiga(id);
    }

}
