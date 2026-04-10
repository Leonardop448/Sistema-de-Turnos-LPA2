package com.turnos.controller;

import com.turnos.enums.EstadoTurno;
import com.turnos.model.AsesorRef;
import com.turnos.model.Turno;
import com.turnos.service.TurnoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST del sistema de turnos.
 * Expone los endpoints bajo el prefijo /api/turnos.
 * Los errores son manejados de forma centralizada por GlobalExceptionHandler.
 */
@RestController
@RequestMapping("/api/turnos")
public class TurnoController {

    private final TurnoService turnoService;

    /** Inyección de dependencia por constructor. */
    public TurnoController(TurnoService turnoService) {
        this.turnoService = turnoService;
    }

    // =========================================================================
    //  POST /api/turnos
    //  Crea un nuevo turno con los datos del cliente, área y prioridad.
    //  El asesor es opcional en la creación.
    //  Retorna: 201 Created con el turno persistido.
    // =========================================================================
    @PostMapping
    public ResponseEntity<Turno> crearTurno(@Valid @RequestBody CrearTurnoRequest request) {
        Turno turno = turnoService.crearTurno(
                request.getCliente(),
                request.getAsesor(),
                request.getArea(),
                request.getPrioridad(),
                request.getMotivoPrioridad()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(turno);
    }

    // =========================================================================
    //  PUT /api/turnos/{id}/asignar-asesor
    //  Asigna un asesor al turno y lo mueve al estado EN_ATENCION.
    //  Body: datos del asesor a asignar (AsesorRef).
    //  Retorna: 200 OK con el turno actualizado.
    // =========================================================================
    @PutMapping("/{id}/asignar-asesor")
    public ResponseEntity<Turno> asignarAsesor(
            @PathVariable String id,
            @RequestBody AsesorRef asesor) {

        Turno turno = turnoService.asignarAsesor(id, asesor);
        return ResponseEntity.ok(turno);
    }

    // =========================================================================
    //  PUT /api/turnos/{id}/atender
    //  Finaliza la atención del turno y lo marca como ATENDIDO.
    //  No requiere body. Solo válido si el turno está EN_ATENCION.
    //  Retorna: 200 OK con el turno actualizado.
    // =========================================================================
    @PutMapping("/{id}/atender")
    public ResponseEntity<Turno> atenderTurno(@PathVariable String id) {
        Turno turno = turnoService.atenderTurno(id);
        return ResponseEntity.ok(turno);
    }

    // =========================================================================
    //  PUT /api/turnos/{id}/cancelar
    //  Cancela el turno si aún no fue atendido.
    //  No requiere body.
    //  Retorna: 200 OK con el turno cancelado.
    // =========================================================================
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Turno> cancelarTurno(@PathVariable String id) {
        Turno turno = turnoService.cancelarTurno(id);
        return ResponseEntity.ok(turno);
    }

    // =========================================================================
    //  PUT /api/turnos/{id}/cambiar-asesor
    //  Reemplaza el asesor asignado mientras el turno está EN_ATENCION.
    //  Body: datos del nuevo asesor (AsesorRef).
    //  Retorna: 200 OK con el turno actualizado.
    // =========================================================================
    @PutMapping("/{id}/cambiar-asesor")
    public ResponseEntity<Turno> cambiarAsesor(
            @PathVariable String id,
            @RequestBody AsesorRef nuevoAsesor) {

        Turno turno = turnoService.cambiarAsesor(id, nuevoAsesor);
        return ResponseEntity.ok(turno);
    }

    // =========================================================================
    //  GET /api/turnos
    //  Retorna todos los turnos ordenados: PREFERENCIAL primero,
    //  luego NORMAL; dentro de cada grupo, por fecha de creación ascendente.
    //  Retorna: 200 OK con la lista ordenada.
    // =========================================================================
    @GetMapping
    public ResponseEntity<List<Turno>> obtenerTurnos() {
        return ResponseEntity.ok(turnoService.obtenerTurnos());
    }

    // =========================================================================
    //  GET /api/turnos/{id}
    //  Busca y retorna un turno por su identificador único de MongoDB.
    //  Retorna: 200 OK con el turno, o 404 si no existe.
    // =========================================================================
    @GetMapping("/{id}")
    public ResponseEntity<Turno> obtenerTurnoPorId(@PathVariable String id) {
        Turno turno = turnoService.obtenerTurnoPorId(id);
        return ResponseEntity.ok(turno);
    }

    // =========================================================================
    //  GET /api/turnos/estado/{estado}
    //  Filtra turnos por su estado (EN_ESPERA, EN_ATENCION, ATENDIDO, CANCELADO).
    //  Retorna: 200 OK con la lista de turnos en ese estado.
    // =========================================================================
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Turno>> obtenerPorEstado(@PathVariable EstadoTurno estado) {
        return ResponseEntity.ok(turnoService.obtenerTurnosPorEstado(estado));
    }

    // =========================================================================
    //  GET /api/turnos/cliente/{numeroDocumento}
    //  Retorna el historial completo de turnos de un cliente por su documento.
    //  Retorna: 200 OK con la lista de turnos del cliente.
    // =========================================================================
    @GetMapping("/cliente/{numeroDocumento}")
    public ResponseEntity<List<Turno>> obtenerPorCliente(@PathVariable String numeroDocumento) {
        return ResponseEntity.ok(turnoService.obtenerTurnosPorCliente(numeroDocumento));
    }

    // =========================================================================
    //  GET /api/turnos/asesor/{asesorId}
    //  Retorna los turnos activos (EN_ESPERA y EN_ATENCION) de un asesor.
    //  Diseñado para alimentar el dashboard del asesor.
    //  Retorna: 200 OK con la lista de turnos activos.
    // =========================================================================
    @GetMapping("/asesor/{asesorId}")
    public ResponseEntity<List<Turno>> obtenerPorAsesor(@PathVariable String asesorId) {
        return ResponseEntity.ok(turnoService.obtenerTurnosPorAsesor(asesorId));
    }
}
