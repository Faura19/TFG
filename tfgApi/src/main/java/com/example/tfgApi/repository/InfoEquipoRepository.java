package com.example.tfgApi.repository;

import com.example.tfgApi.model.InfoEquipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfoEquipoRepository extends JpaRepository<InfoEquipo,Long> {



}
