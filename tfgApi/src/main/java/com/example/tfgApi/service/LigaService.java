package com.example.tfgApi.service;

import com.example.tfgApi.model.Liga;
import com.example.tfgApi.repository.LigaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LigaService {

    private final LigaRepository ligaRepository;

    public LigaService(LigaRepository ligaRepository) {
        this.ligaRepository = ligaRepository;
    }

    public List<Liga> getLiga(){
        return ligaRepository.findAll();
    }

    public Liga postLiga(Liga liga){
        return ligaRepository.save(liga);
    }

    public Liga putLiga(Liga liga){
        Liga liga1=new Liga();
        liga1.setNombreEquipo(liga.getNombreEquipo());
        liga1.setPuntosEquipo(liga.getPuntosEquipo());

        ligaRepository.delete(liga);
        return ligaRepository.save(liga1);
    }

    public void deleteLiga(Long id){
        ligaRepository.deleteById(id);
    }


}
