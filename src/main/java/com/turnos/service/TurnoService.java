package com.turnos.service;

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

/**
 * Servicio principal con la lógica de negocio del sistema de turnos.
 * Gestiona el ciclo de vida completo de un turno: creación, asignación,
 * atención, cancelación y consultas.
 */
@Service
public class TurnoService {

    private final TurnoRepository turnoRepository;

    /** Inyección de dependencias por constructor (buena práctica recomendada por Spring). */
    public TurnoService(TurnoRepository turnoRepository) {
        this.turnoRepository = turnoRepository;
    }

    // =========================================================================
    //  1. CREAR TURNO
    // =========================================================================

    /**
     * Crea un nuevo turno en estado EN_ESPERA con número secuencial autogenerado.
     *
     * @param cliente        Datos del cliente solicitante
     * @param asesor         Asesor asignado (puede ser null en la creación)
     * @param area           Área de atención (obligatoria)
     * @param prioridad      Nivel de prioridad del turno
     * @param motivoPrioridad Razón de prioridad preferencial (null si es NORMAL)
     * @return El turno creado y persistido en MongoDB
     */
    public Turno crearTurno(Cliente cliente, AsesorRef asesor, AreaRef area,
                            PrioridadTurno prioridad, String motivoPrioridad) {

        Turno turno = Turno.builder()
                .numeroTurno(generarNumeroTurno())
                .cliente(cliente)
                .asesor(asesor)
                .area(area)
                .estado(EstadoTurno.EN_ESPERA)
                .prioridad(prioridad)
                .motivoPrioridad(prioridad == PrioridadTurno.PREFERENCIAL ? motivoPrioridad : null)
                .fechaCreacion(LocalDateTime.now())
                .build();

        return turnoRepository.save(turno);
    }

    // =========================================================================
    //  2. ASIGNAR ASESOR  →  EN_ATENCION
    // =========================================================================

    /**
     * Asigna un asesor al turno y cambia su estado a EN_ATENCION.
     *
     * @param turnoId Identificador del turno
     * @param asesor  Asesor a asignar
     * @return El turno actualizado
     * @throws RuntimeException si el turno no existe
     */
    public Turno asignarAsesor(String turnoId, AsesorRef asesor) {
        Turno turno = buscarTurnoPorId(turnoId);

        turno.setAsesor(asesor);
        turno.setEstado(EstadoTurno.EN_ATENCION);

        return turnoRepository.save(turno);
    }

    // =========================================================================
    //  3. ATENDER TURNO  →  ATENDIDO
    // =========================================================================

    /**
     * Finaliza la atención del turno y lo marca como ATENDIDO.
     * Solo válido si el turno está en estado EN_ATENCION.
     *
     * @param turnoId Identificador del turno
     * @return El turno actualizado
     * @throws RuntimeException si el turno no existe o no está EN_ATENCION
     */
    public Turno atenderTurno(String turnoId) {
        Turno turno = buscarTurnoPorId(turnoId);

        if (turno.getEstado() != EstadoTurno.EN_ATENCION) {
            throw new RuntimeException(
                    "No se puede atender el turno '" + turnoId +
                    "'. Estado actual: " + turno.getEstado() +
                    ". Se requiere: EN_ATENCION."
            );
        }

        turno.setEstado(EstadoTurno.ATENDIDO);
        turno.setFechaAtencion(LocalDateTime.now());

        return turnoRepository.save(turno);
    }

    // =========================================================================
    //  4. CANCELAR TURNO  →  CANCELADO
    // =========================================================================

    /**
     * Cancela el turno si aún no ha sido atendido.
     *
     * @param turnoId Identificador del turno
     * @return El turno cancelado
     * @throws RuntimeException si el turno no existe o ya está ATENDIDO
     */
    public Turno cancelarTurno(String turnoId) {
        Turno turno = buscarTurnoPorId(turnoId);

        if (turno.getEstado() == EstadoTurno.ATENDIDO) {
            throw new RuntimeException(
                    "No se puede cancelar el turno '" + turnoId +
                    "' porque ya fue ATENDIDO."
            );
        }

        turno.setEstado(EstadoTurno.CANCELADO);
        turno.setFechaAtencion(LocalDateTime.now());

        return turnoRepository.save(turno);
    }

    // =========================================================================
    //  5. CAMBIAR ASESOR
    // =========================================================================

    /**
     * Reemplaza el asesor asignado a un turno que esté EN_ATENCION.
     *
     * @param turnoId     Identificador del turno
     * @param nuevoAsesor Nuevo asesor a asignar
     * @return El turno actualizado
     * @throws RuntimeException si el turno no existe o no está EN_ATENCION
     */
    public Turno cambiarAsesor(String turnoId, AsesorRef nuevoAsesor) {
        Turno turno = buscarTurnoPorId(turnoId);

        if (turno.getEstado() != EstadoTurno.EN_ATENCION) {
            throw new RuntimeException(
                    "No se puede cambiar el asesor del turno '" + turnoId +
                    "'. Estado actual: " + turno.getEstado() +
                    ". Se requiere: EN_ATENCION."
            );
        }

        turno.setAsesor(nuevoAsesor);
        return turnoRepository.save(turno);
    }

    // =========================================================================
    //  6. OBTENER TODOS LOS TURNOS ORDENADOS
    // =========================================================================

    /**
     * Retorna todos los turnos ordenados por prioridad (PREFERENCIAL primero)
     * y, dentro de cada grupo, por fecha de creación ascendente.
     *
     * @return Lista ordenada de todos los turnos
     */
    public List<Turno> obtenerTurnos() {
        return turnoRepository.findAll()
                .stream()
                .sorted(Comparator
                        // PREFERENCIAL (ordinal 0) antes que NORMAL (ordinal 1)
                        .comparingInt((Turno t) -> t.getPrioridad().ordinal())
                        // Dentro del mismo grupo, el más antiguo primero
                        .thenComparing(Turno::getFechaCreacion))
                .collect(Collectors.toList());
    }

    // =========================================================================
    //  7. OBTENER TURNOS POR ESTADO
    // =========================================================================

    /**
     * Retorna todos los turnos que se encuentren en el estado indicado.
     *
     * @param estado Estado a filtrar
     * @return Lista de turnos con ese estado
     */
    public List<Turno> obtenerTurnosPorEstado(EstadoTurno estado) {
        return turnoRepository.findByEstado(estado);
    }

    // =========================================================================
    //  8. OBTENER TURNOS POR CLIENTE
    // =========================================================================

    /**
     * Retorna el historial de turnos de un cliente por su número de documento.
     *
     * @param numeroDocumento Número de documento del cliente
     * @return Lista de turnos del cliente
     */
    public List<Turno> obtenerTurnosPorCliente(String numeroDocumento) {
        return turnoRepository.findByClienteNumeroDocumento(numeroDocumento);
    }

    // =========================================================================
    //  9. OBTENER TURNOS POR ASESOR (dashboard)
    // =========================================================================

    /**
     * Retorna los turnos activos de un asesor: únicamente EN_ESPERA y EN_ATENCION.
     * Útil para el dashboard del asesor.
     *
     * @param asesorId Identificador del asesor
     * @return Lista de turnos activos del asesor
     */
    public List<Turno> obtenerTurnosPorAsesor(String asesorId) {
        return turnoRepository.findByAsesorAsesorId(asesorId)
                .stream()
                .filter(t -> t.getEstado() == EstadoTurno.EN_ESPERA
                          || t.getEstado() == EstadoTurno.EN_ATENCION)
                .collect(Collectors.toList());
    }

    // =========================================================================
    //  10. OBTENER TURNO POR ID
    // =========================================================================

    /**
     * Busca y retorna un turno por su identificador único.
     *
     * @param id Identificador del turno
     * @return El turno encontrado
     * @throws RuntimeException si no existe ningún turno con ese id
     */
    public Turno obtenerTurnoPorId(String id) {
        return buscarTurnoPorId(id);
    }

    // =========================================================================
    //  MÉTODOS PRIVADOS DE APOYO
    // =========================================================================

    /**
     * Busca un turno por id y lanza excepción descriptiva si no se encuentra.
     * Centraliza el manejo del caso "turno no encontrado" para todos los métodos.
     */
    private Turno buscarTurnoPorId(String id) {
        return turnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Turno no encontrado con id: '" + id + "'"
                ));
    }

    /**
     * Genera el siguiente número de turno en formato T-XXXX.
     * Consulta el último turno en BD y suma 1 al número; si no hay ninguno, comienza en T-0001.
     */
    private String generarNumeroTurno() {
        return turnoRepository.findTopByOrderByNumeroTurnoDesc()
                .map(ultimo -> {
                    // Extrae la parte numérica: "T-0042" → 42
                    String parte = ultimo.getNumeroTurno().replace("T-", "");
                    int siguiente = Integer.parseInt(parte) + 1;
                    return String.format("T-%04d", siguiente);
                })
                .orElse("T-0001"); // Primer turno del sistema
    }
}
