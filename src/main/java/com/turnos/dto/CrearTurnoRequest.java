package com.turnos.dto;

import com.turnos.enums.PrioridadTurno;

import com.turnos.model.AreaRef;
import com.turnos.model.Cliente;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearTurnoRequest {

    @NotNull(message = "El cliente es obligatorio")
    @Valid
    private Cliente cliente;

    @NotNull(message = "El área es obligatoria")
    @Valid
    private AreaRef area;

    @NotNull(message = "La prioridad es obligatoria")
    private PrioridadTurno prioridad;

    private String motivoPrioridad;
}
