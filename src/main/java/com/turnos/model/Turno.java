package com.turnos.model;

import com.turnos.enums.EstadoTurno;
import com.turnos.enums.PrioridadTurno;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Documento principal del sistema. Representa un turno en la cola de atención.
 * Se persiste en la colección "turnos" de MongoDB.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "turnos")
public class Turno {

    /** Identificador único generado automáticamente por MongoDB. */
    @Id
    private String id;

    /**
     * Número de turno visible para el cliente.
     * Formato secuencial: T-0001, T-0002, etc.
     */
    private String numeroTurno;

    /** Datos del cliente que solicita el turno (embebido, obligatorio). */
    @NotNull(message = "El cliente es obligatorio")
    private Cliente cliente;

    /**
     * Referencia al asesor asignado (embebido).
     * Puede ser null al momento de la creación; se asigna cuando el turno pasa a EN_ATENCION.
     */
    private AsesorRef asesor;

    /** Referencia al área de atención donde se gestionará el turno (embebido, obligatorio). */
    @NotNull(message = "El área es obligatoria")
    private AreaRef area;

    /**
     * Estado actual del turno dentro del ciclo de vida.
     * Valor por defecto: EN_ESPERA.
     */
    @Builder.Default
    private EstadoTurno estado = EstadoTurno.EN_ESPERA;

    /** Nivel de prioridad del turno (PREFERENCIAL o NORMAL). */
    @NotNull(message = "La prioridad es obligatoria")
    private PrioridadTurno prioridad;

    /**
     * Descripción de la razón de prioridad especial.
     * Solo aplica cuando la prioridad es PREFERENCIAL (ej: "adulto mayor", "embarazo").
     * Null si la prioridad es NORMAL.
     */
    private String motivoPrioridad;

    /** Fecha y hora en que se registró el turno en el sistema. Se asigna automáticamente. */
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    /**
     * Fecha y hora en que el turno fue atendido o cancelado.
     * Null mientras el turno está EN_ESPERA o EN_ATENCION.
     */
    private LocalDateTime fechaAtencion;
}
