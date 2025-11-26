package com.example.tfgApi.service;

import com.example.tfgApi.model.InfoEquipo;
import com.example.tfgApi.model.Liga;
import com.example.tfgApi.repository.InfoEquipoRepository;
import com.example.tfgApi.repository.LigaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InfoEquipoService {

    private final InfoEquipoRepository infoEquipoRepository;

    private final LigaRepository ligaRepository;

    public InfoEquipoService(InfoEquipoRepository infoEquipoRepository, LigaRepository ligaRepository) {
        this.infoEquipoRepository = infoEquipoRepository;
        this.ligaRepository = ligaRepository;
    }

    public List<InfoEquipo> getInfo(){
        return infoEquipoRepository.findAll();
    }

    public InfoEquipo postInfo(InfoEquipo infoEquipo){
        return infoEquipoRepository.save(infoEquipo);
    }

    public InfoEquipo actualizarInfoEquipo(Long id, InfoEquipo nuevosDatos) {
        InfoEquipo existente = infoEquipoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("InfoEquipo no encontrado"));

        existente.setAnoFundacion(nuevosDatos.getAnoFundacion());
        existente.setCapacidadEstadio(nuevosDatos.getCapacidadEstadio());
        existente.setTitulos(nuevosDatos.getTitulos());
        existente.setEntradaPartido(nuevosDatos.getEntradaPartido());

        if (nuevosDatos.getLiga() != null && nuevosDatos.getLiga().getId() != null) {
            Liga liga = ligaRepository.findById(nuevosDatos.getLiga().getId())
                    .orElseThrow(() -> new RuntimeException("Liga no encontrada"));
            existente.setLiga(liga);
        }

        return infoEquipoRepository.save(existente);
    }


    public void deleteInfo(Long id){
        infoEquipoRepository.deleteById(id);
    }



}
