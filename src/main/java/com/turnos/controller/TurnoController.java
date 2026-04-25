package com.turnos.controller;

import com.turnos.dto.AsesorRequest;
import com.turnos.dto.CrearTurnoRequest;
import com.turnos.dto.SiguienteTurnoRequest;
import com.turnos.enums.EstadoTurno;
import com.turnos.model.Turno;
import com.turnos.service.TurnoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/turnos")
public class TurnoController {

    private final TurnoService turnoService;

    public TurnoController(TurnoService turnoService) {
        this.turnoService = turnoService;
    }

    @PostMapping
    public ResponseEntity<Turno> crearTurno(@Valid @RequestBody CrearTurnoRequest request) {
        Turno turno = turnoService.crearTurno(
                request.getCliente(),
                request.getArea(),
                request.getPrioridad(),
                request.getMotivoPrioridad());
        return ResponseEntity.status(HttpStatus.CREATED).body(turno);
    }

    @PutMapping("/siguiente-turno")
    public ResponseEntity<Turno> siguienteTurno(@Valid @RequestBody SiguienteTurnoRequest request) {
        Turno turno = turnoService.siguienteTurno(request.getAsesorId(), request.getNombre());
        return ResponseEntity.ok(turno);
    }

    @PutMapping("/{id}/asignar-asesor")
    public ResponseEntity<Turno> asignarAsesor(
            @PathVariable String id,
            @RequestBody AsesorRequest asesor) {

        Turno turno = turnoService.asignarAsesor(id, asesor);
        return ResponseEntity.ok(turno);
    }

    @PutMapping("/{id}/atender")
    public ResponseEntity<Turno> atenderTurno(@PathVariable String id) {
        Turno turno = turnoService.atenderTurno(id);
        return ResponseEntity.ok(turno);
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Turno> cancelarTurno(@PathVariable String id) {
        Turno turno = turnoService.cancelarTurno(id);
        return ResponseEntity.ok(turno);
    }

    @PutMapping("/{id}/cambiar-asesor")
    public ResponseEntity<Turno> cambiarAsesor(
            @PathVariable String id,
            @RequestBody AsesorRequest nuevoAsesor) {

        Turno turno = turnoService.cambiarAsesor(id, nuevoAsesor);
        return ResponseEntity.ok(turno);
    }

    @GetMapping
    public ResponseEntity<List<Turno>> obtenerTurnos() {
        return ResponseEntity.ok(turnoService.obtenerTurnos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Turno> obtenerTurnoPorId(@PathVariable String id) {
        Turno turno = turnoService.obtenerTurnoPorId(id);
        return ResponseEntity.ok(turno);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Turno>> obtenerPorEstado(@PathVariable EstadoTurno estado) {
        return ResponseEntity.ok(turnoService.obtenerTurnosPorEstado(estado));
    }

    @GetMapping("/cliente/{numeroDocumento}")
    public ResponseEntity<List<Turno>> obtenerPorCliente(@PathVariable String numeroDocumento) {
        return ResponseEntity.ok(turnoService.obtenerTurnosPorCliente(numeroDocumento));
    }

    @GetMapping("/asesor/{asesorId}")
    public ResponseEntity<List<Turno>> obtenerPorAsesor(@PathVariable String asesorId) {
        return ResponseEntity.ok(turnoService.obtenerTurnosPorAsesor(asesorId));
    }

    @GetMapping("/area/{areaId}")
    public ResponseEntity<List<Turno>> obtenerPorArea(@PathVariable String areaId){
        return ResponseEntity.ok(turnoService.obtenerTurnosPorArea(areaId));
    }
}
