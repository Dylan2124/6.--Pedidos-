package cl.duoc.plataforma.ms_pedido.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * ═══════════════════════════════════════════════════
 * CLASE: GlobalExceptionHandler.java
 * Actúa como un "interceptor" global para capturar errores.
 * Permite evitar que el cliente reciba errores feos por defecto
 * de Tomcat, devolviendo en su lugar un JSON ordenado y claro.
 * ═══════════════════════════════════════════════════
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Captura específicamente los errores de validación (cuando @Valid falla).
     * Por ejemplo: Si 'cantidad' viene en 0 y el DTO exige mínimo 1.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        
        // Extraemos cada campo que falló y su mensaje de error correspondiente
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        // Construimos el JSON de respuesta personalizado
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value()); // HTTP 400
        response.put("errors", errors);

        log.warn("Se detectó un error de validación en la entrada: {}", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Captura cualquier otra excepción genérica o error de negocio
     * (por ejemplo, si intentan buscar un Pedido que no existe).
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value()); // HTTP 500
        response.put("error", ex.getMessage());

        log.error("Excepción interna capturada: {}", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
