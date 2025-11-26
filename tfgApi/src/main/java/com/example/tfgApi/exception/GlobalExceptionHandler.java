package com.example.tfgApi.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String message = "Error de integridad de datos.";

        if (ex.getMessage().contains("Duplicate entry")) {
            // Detectamos qué tabla causó el error mirando el mensaje de la excepción
            if (ex.getMessage().contains("liga")) {
                message = "El nombre del equipo ya existe, no se puede registrar.";
            } else if (ex.getMessage().contains("jugador")) {
                message = "Ya existe un jugador con ese nombre en el mismo equipo.";
            }
        }

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}


