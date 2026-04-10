package com.turnos.controller;

import com.turnos.enums.PrioridadTurno;
import com.turnos.model.AsesorRef;
import com.turnos.model.AreaRef;
import com.turnos.model.Cliente;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) que representa el cuerpo JSON de la petición
 * para crear un nuevo turno (POST /api/turnos).
 * Desacopla el JSON de entrada de la entidad interna Turno.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearTurnoRequest {

    /** Datos del cliente que solicita el turno. */
    @NotNull(message = "El cliente es obligatorio")
    @Valid
    private Cliente cliente;

    /**
     * Asesor asignado al momento de la creación.
     * Puede omitirse en el JSON; el turno quedará sin asesor hasta ser llamado.
     */
    private AsesorRef asesor;

    /** Área de atención donde se gestionará el turno. */
    @NotNull(message = "El área es obligatoria")
    @Valid
    private AreaRef area;

    /** Nivel de prioridad: PREFERENCIAL o NORMAL. */
    @NotNull(message = "La prioridad es obligatoria")
    private PrioridadTurno prioridad;

    /**
     * Razón de la prioridad preferencial (ej: "adulto mayor", "embarazo").
     * Solo requerido cuando prioridad = PREFERENCIAL.
     */
    private String motivoPrioridad;
}
