package com.turnos.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Referencia al asesor que atiende el turno.
 * El asesor es gestionado por el microservicio de Administración de Asesores;
 * aquí solo se almacena su id y nombre para evitar acoplamiento entre servicios.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsesorRef {

    /** Identificador del asesor en el microservicio de Administración de Asesores. */
    private String asesorId;

    /** Nombre del asesor (copiado en el momento de la asignación). */
    private String nombre;

    /** Indica si el asesor está disponible para atender turnos. */
    private boolean disponible;
}
