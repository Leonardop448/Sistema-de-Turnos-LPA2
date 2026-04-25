package com.turnos.service;

import com.turnos.dto.AsesorRequest;
import com.turnos.enums.EstadoTurno;
import com.turnos.enums.PrioridadTurno;
import com.turnos.model.AsesorRef;
import com.turnos.model.AreaRef;
import com.turnos.model.Cliente;
import com.turnos.model.Turno;
import com.turnos.repository.TurnoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TurnoService {

    private final TurnoRepository turnoRepository;

    public TurnoService(TurnoRepository turnoRepository) {
        this.turnoRepository = turnoRepository;
    }

    public Turno crearTurno(Cliente cliente, AreaRef area,
            PrioridadTurno prioridad, String motivoPrioridad) {

        Turno turno = Turno.builder()
                .numeroTurno(generarNumeroTurno())
                .cliente(cliente)
                .asesor(null)
                .area(area)
                .estado(EstadoTurno.EN_ESPERA)
                .prioridad(prioridad)
                .motivoPrioridad(prioridad == PrioridadTurno.PREFERENCIAL ? motivoPrioridad : null)
                .fechaCreacion(LocalDateTime.now())
                .build();

        return turnoRepository.save(turno);
    }

    public Turno siguienteTurno(String asesorId, String nombreAsesor) {
        List<Turno> turnos = turnoRepository.findByEstado(EstadoTurno.EN_ESPERA);

        Turno turno = turnos.stream()
                .sorted(Comparator
                        .comparingInt((Turno t) -> t.getPrioridad().ordinal())
                        .thenComparing(Turno::getFechaCreacion))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No hay turnos en espera"));

        AsesorRef asesor = AsesorRef.builder()
                .asesorId(asesorId)
                .nombre(nombreAsesor)
                .disponible(true)
                .build();

        turno.setAsesor(asesor);
        turno.setEstado(EstadoTurno.EN_ATENCION);

        return turnoRepository.save(turno);
    }

    public Turno asignarAsesor(String turnoId, AsesorRequest asesor) {
        Turno turno = buscarTurnoPorId(turnoId);
        AsesorRef asesorRef = new AsesorRef(asesor.getAsesorId(), asesor.getNombre(), asesor.isDisponible());

        turno.setAsesor(asesorRef);
        turno.setEstado(EstadoTurno.EN_ATENCION);

        return turnoRepository.save(turno);
    }

    public Turno atenderTurno(String turnoId) {
        Turno turno = buscarTurnoPorId(turnoId);

        if (turno.getEstado() != EstadoTurno.EN_ATENCION) {
            throw new RuntimeException("El turno no se puede atender");
        }

        turno.setEstado(EstadoTurno.ATENDIDO);
        turno.setFechaAtencion(LocalDateTime.now());

        return turnoRepository.save(turno);
    }

    public Turno cancelarTurno(String turnoId) {
        Turno turno = buscarTurnoPorId(turnoId);

        if (turno.getEstado() == EstadoTurno.ATENDIDO) {
            throw new RuntimeException("El turno ya fue atendido");
        }

        turno.setEstado(EstadoTurno.CANCELADO);
        turno.setFechaAtencion(LocalDateTime.now());

        return turnoRepository.save(turno);
    }

    public Turno cambiarAsesor(String turnoId, AsesorRequest nuevoAsesor) {
        Turno turno = buscarTurnoPorId(turnoId);

        if (turno.getEstado() != EstadoTurno.EN_ATENCION) {
            throw new RuntimeException("No se puede cambiar el asesor");
        }

        AsesorRef asesorRef = new AsesorRef(nuevoAsesor.getAsesorId(), nuevoAsesor.getNombre(),
                nuevoAsesor.isDisponible());

        turno.setAsesor(asesorRef);
        return turnoRepository.save(turno);
    }

    public List<Turno> obtenerTurnos() {
        return turnoRepository.findAll()
                .stream()
                .sorted(Comparator
                        // PREFERENCIAL va primero en la cola
                        .comparingInt((Turno t) -> t.getPrioridad().ordinal())
                        .thenComparing(Turno::getFechaCreacion))
                .collect(Collectors.toList());
    }

    public List<Turno> obtenerTurnosPorEstado(EstadoTurno estado) {
        return turnoRepository.findByEstado(estado);
    }

    public List<Turno> obtenerTurnosPorCliente(String numeroDocumento) {
        return turnoRepository.findByClienteNumeroDocumento(numeroDocumento);
    }

    public List<Turno> obtenerTurnosPorAsesor(String asesorId) {
        return turnoRepository.findByAsesorAsesorId(asesorId)
                .stream()
                .filter(t -> t.getEstado() == EstadoTurno.EN_ESPERA
                        || t.getEstado() == EstadoTurno.EN_ATENCION)
                .collect(Collectors.toList());
    }

    public List<Turno> obtenerTurnosPorArea(String areaId){
        return turnoRepository.findByAreaAreaId(areaId);
    }

    public Turno obtenerTurnoPorId(String id) {
        return buscarTurnoPorId(id);
    }

    private Turno buscarTurnoPorId(String id) {
        return turnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));
    }

    private String generarNumeroTurno() {
        return turnoRepository.findTopByOrderByNumeroTurnoDesc()
                .map(ultimo -> {
                    String parte = ultimo.getNumeroTurno().replace("T-", "");
                    int siguiente = Integer.parseInt(parte) + 1;
                    return String.format("T-%04d", siguiente);
                })
                .orElse("T-0001");
    }
}
