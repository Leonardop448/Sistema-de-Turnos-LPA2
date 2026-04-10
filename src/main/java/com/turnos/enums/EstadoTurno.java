package com.turnos.enums;

/**
 * Estado del ciclo de vida de un turno.
 */
public enum EstadoTurno {

    /** El turno fue creado y espera ser llamado. */
    EN_ESPERA,

    /** El turno está siendo atendido por un asesor. */
    EN_ATENCION,

    /** El turno fue atendido y finalizado correctamente. */
    ATENDIDO,

    /** El turno fue cancelado antes o durante la atención. */
    CANCELADO
}
