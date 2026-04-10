package com.turnos.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Referencia al área de atención asociada al turno.
 * El área es gestionada por el microservicio de Monitoreo;
 * aquí solo se almacena su id y nombre para evitar acoplamiento entre servicios.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AreaRef {

    /** Identificador del área en el microservicio de Monitoreo. */
    private String areaId;

    /** Nombre del área (copiado en el momento de la creación del turno). */
    private String nombre;
}
