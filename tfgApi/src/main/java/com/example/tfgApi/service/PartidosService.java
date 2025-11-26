package com.example.tfgApi.service;

import com.example.tfgApi.model.Liga;
import com.example.tfgApi.model.Partidos;
import com.example.tfgApi.repository.LigaRepository;
import com.example.tfgApi.repository.PartidosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartidosService {

    @Autowired
    private final PartidosRepository partidosRepository;

    @Autowired
    private LigaRepository ligaRepository;

    public PartidosService(PartidosRepository partidosRepository) {
        this.partidosRepository = partidosRepository;
    }

    public List<Partidos> getPartidos(){
        return partidosRepository.findAll();
    }

    public Partidos postPartidos(Partidos partido) {
        // Recupera la liga real desde la base de datos
        Long ligaId = partido.getLiga().getId();

        Liga liga = ligaRepository.findById(ligaId)
                .orElseThrow(() -> new RuntimeException("Liga no encontrada con id: " + ligaId));

        // Asigna la entidad gestionada por JPA
        partido.setLiga(liga);

        // Guarda el partido
        return partidosRepository.save(partido);
    }

    public Partidos updatePartido(Long id, Partidos nuevosDatos) {
        // Buscar el partido existente
        Partidos existente = partidosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el partido con id: " + id));

        // Actualizar los campos simples
        existente.setGolesFavor(nuevosDatos.getGolesFavor());
        existente.setGolesContra(nuevosDatos.getGolesContra());

        // Si el JSON incluye una liga, la asociamos correctamente
        if (nuevosDatos.getLiga() != null && nuevosDatos.getLiga().getId() != null) {
            Liga liga = ligaRepository.findById(nuevosDatos.getLiga().getId())
                    .orElseThrow(() -> new RuntimeException("No se encontró la liga con id: " + nuevosDatos.getLiga().getId()));
            existente.setLiga(liga);
        }

        // Guardar el partido actualizado
        return partidosRepository.save(existente);
    }

    public void deletePartidos(Long id){
        partidosRepository.deleteById(id);
    }

}
