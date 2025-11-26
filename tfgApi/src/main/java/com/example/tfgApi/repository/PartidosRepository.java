package com.example.tfgApi.repository;

import com.example.tfgApi.model.Partidos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartidosRepository extends JpaRepository<Partidos,Long> {
}
