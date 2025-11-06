package pe.adcomp.suma.sunat.params.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para evitar exponer mensajes internos (como JDBC exceptions)
 * Devuelve un mensaje genérico cuando hay errores de base de datos.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({DataAccessException.class, SQLException.class})
    public ResponseEntity<Map<String, String>> handleDatabaseException(Exception ex) {
        Map<String, String> body = new HashMap<>();
        // Respuesta solicitada por el usuario: sólo "error en la bd"
        body.put("mensaje", "error en la bd");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

}
