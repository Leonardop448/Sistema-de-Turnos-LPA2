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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "turnos")
public class Turno {

    @Id
    private String id;

    private String numeroTurno;

    @NotNull(message = "El cliente es obligatorio")
    private Cliente cliente;

    private AsesorRef asesor;

    @NotNull(message = "El área es obligatoria")
    private AreaRef area;

    @Builder.Default
    private EstadoTurno estado = EstadoTurno.EN_ESPERA;

    @NotNull(message = "La prioridad es obligatoria")
    private PrioridadTurno prioridad;

    private String motivoPrioridad;

    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    private LocalDateTime fechaAtencion;
}
