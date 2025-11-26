package com.example.tfgApi.controller;

import com.example.tfgApi.model.InfoEquipo;
import com.example.tfgApi.model.Liga;
import com.example.tfgApi.repository.InfoEquipoRepository;
import com.example.tfgApi.repository.LigaRepository;
import com.example.tfgApi.service.InfoEquipoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/equipo")
@CrossOrigin(origins = "*")
public class InfoEquipoController {

    private final InfoEquipoService infoEquipoService;

    private final InfoEquipoRepository infoEquipoRepository;

    private final LigaRepository ligaRepository;

    public InfoEquipoController(InfoEquipoService infoEquipoService, InfoEquipoRepository infoEquipoRepository, LigaRepository ligaRepository) {
        this.infoEquipoService = infoEquipoService;
        this.infoEquipoRepository = infoEquipoRepository;
        this.ligaRepository = ligaRepository;
    }

    @GetMapping()
    public List<InfoEquipo> getInfo(){
        return infoEquipoService.getInfo();
    }

    @PostMapping
    public ResponseEntity<?> crearInfoEquipo(@RequestBody InfoEquipo infoEquipo) {
        if (infoEquipo.getLiga() == null || infoEquipo.getLiga().getId() == null) {
            return ResponseEntity.badRequest().body("Debes especificar el id de la liga");
        }

        Long ligaId = infoEquipo.getLiga().getId();
        Optional<Liga> ligaOpt = ligaRepository.findById(ligaId);

        if (ligaOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("La liga especificada no existe");
        }

        Liga liga = ligaOpt.get();

        // Verificar que la liga no tenga ya un infoEquipo
        if (liga.getInfoEquipo() != null) {
            return ResponseEntity.badRequest().body("Esta liga ya tiene un InfoEquipo asociado");
        }

        infoEquipo.setLiga(liga);
        InfoEquipo nuevo = infoEquipoRepository.save(infoEquipo);

        return ResponseEntity.ok(nuevo);
    }



    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarInfoEquipo(
            @PathVariable Long id,
            @Valid @RequestBody InfoEquipo nuevosDatos) {

        Optional<InfoEquipo> optExistente = infoEquipoRepository.findById(id);
        if (optExistente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        InfoEquipo existente = optExistente.get();

        // Actualizamos solo los campos necesarios
        existente.setAnoFundacion(nuevosDatos.getAnoFundacion());
        existente.setCapacidadEstadio(nuevosDatos.getCapacidadEstadio());
        existente.setTitulos(nuevosDatos.getTitulos());
        existente.setEntradaPartido(nuevosDatos.getEntradaPartido());

        // 👇 OPCIONAL: si quieres permitir cambiar la liga
        if (nuevosDatos.getLiga() != null && nuevosDatos.getLiga().getId() != null) {
            Optional<Liga> ligaOpt = ligaRepository.findById(nuevosDatos.getLiga().getId());
            ligaOpt.ifPresent(existente::setLiga);
        }

        InfoEquipo actualizado = infoEquipoRepository.save(existente);

        return ResponseEntity.ok(actualizado);
    }


    @DeleteMapping("/{id}")
    public void deleteInfo(@PathVariable Long id){
        infoEquipoService.deleteInfo(id);
    }

}
