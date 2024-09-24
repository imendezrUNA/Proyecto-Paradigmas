package cr.ac.una.springbootaopmaven.controller;

import cr.ac.una.springbootaopmaven.entity.Persona;
import cr.ac.una.springbootaopmaven.repository.PersonaRepository;
import cr.ac.una.springbootaopmaven.serviceInterface.IServicioProcesador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@Tag(name = "Gestión de Personas", description = "Controlador para gestionar personas y generar reportes")
public class PersonaController {

    @Autowired
    PersonaRepository personaRepository;

    @Autowired
    IServicioProcesador servicioProcesador;

    @Operation(summary = "Obtener todas las personas", description = "Devuelve una lista con todas las personas en la base de datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/persona")
    public ResponseEntity<List<Persona>> getPersonas() {
        return ResponseEntity.ok(personaRepository.findAll());
    }

    @Operation(summary = "Guardar una nueva persona", description = "Guarda una nueva persona en la base de datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Persona creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/persona")
    public ResponseEntity<Persona> savePersona(@RequestBody Persona persona) {
        if (persona.getEdad() == null) {
            throw new RuntimeException("Edad is invalid, triggering 500");
        }

        return Optional.of(persona)
                .filter(p -> p.getNombre() != null && !p.getNombre().isEmpty())
                .filter(p -> p.getEdad() >= 0)
                .map(p -> personaRepository.save(p))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @Operation(summary = "Actualizar una persona", description = "Actualiza los datos de una persona existente en la base de datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Persona actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/persona")
    public ResponseEntity<Persona> updatePersona(@RequestBody Persona persona) {
        return Optional.ofNullable(persona)
                .filter(p -> p.getId() != null && personaRepository.existsById(p.getId()))
                .filter(p -> p.getNombre() != null && !p.getNombre().isEmpty())
                .filter(p -> p.getEdad() != null && p.getEdad() >= 0)
                .map(p -> {
                    Persona personaExistente = personaRepository.findById(p.getId()).orElse(null);
                    if (personaExistente != null) {
                        personaExistente.setNombre(p.getNombre());
                        personaExistente.setEdad(p.getEdad());
                        return personaRepository.save(personaExistente);
                    }
                    return null;
                })
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @Operation(summary = "Eliminar una persona", description = "Elimina una persona por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Persona eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Persona no encontrada")
    })
    @DeleteMapping("/persona/{id}")
    public ResponseEntity<Void> deletePersona(
            @Parameter(description = "ID de la persona a eliminar") @PathVariable Long id) {
        return Optional.ofNullable(id)
                .filter(personaRepository::existsById)
                .map(personaId -> {
                    personaRepository.deleteById(personaId);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtener una persona", description = "Devuelve los datos de una persona específica por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Persona encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Persona no encontrada")
    })
    @GetMapping("/persona/{id}")
    public ResponseEntity<Persona> getPersona(
            @Parameter(description = "ID de la persona a obtener") @PathVariable Long id) {
        return personaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Endpoints para los reportes
    @Operation(summary = "Generar reporte de errores", description = "Genera un reporte detallado sobre los errores capturados en los logs.")
    @GetMapping("/reporte/error")
    public ResponseEntity<Map<String, Object>> generarReporteError() {
        return ResponseEntity.ok(servicioProcesador.generarReporteError());
    }

    @Operation(summary = "Generar reporte de uso de endpoints", description = "Genera estadísticas sobre el uso de los endpoints de la API.")
    @GetMapping("/reporte/endpoint")
    public ResponseEntity<Map<String, Object>> generarReporteEndPoint() {
        return ResponseEntity.ok(servicioProcesador.generarReporteEndPoint());
    }

    @Operation(summary = "Generar reporte de tiempos de respuesta", description = "Analiza los tiempos de respuesta de la aplicación para identificar posibles cuellos de botella.")
    @GetMapping("/reporte/tiempo")
    public ResponseEntity<Map<String, Object>> generarRespuestaTiempoReporte() {
        return ResponseEntity.ok(servicioProcesador.generarRespuestaTiempoReporte());
    }

    @Operation(summary = "Generar reporte de estado de la aplicación", description = "Genera un resumen del estado general de la aplicación basado en el análisis de los logs.")
    @GetMapping("/reporte/status")
    public ResponseEntity<Map<String, Object>> generarReporteEstatusAplicacion() {
        return ResponseEntity.ok(servicioProcesador.generarReporteEstatusAplicacion());
    }

    @Operation(summary = "Generar reporte de errores críticos", description = "Genera un reporte sobre los errores críticos detectados en los logs.")
    @GetMapping("/reporte/errorCritico")
    public ResponseEntity<Map<String, Object>> generarReporteErrorCritico() {
        return ResponseEntity.ok(servicioProcesador.generarReporteErrorCritico());
    }
}
