package com.turnos.model;

import com.turnos.enums.TipoDocumento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Información del cliente que solicita el turno.
 * Clase embebida dentro del documento Turno (no es una colección independiente).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    /** Tipo de documento de identidad del cliente. */
    private TipoDocumento tipoDocumento;

    /** Número del documento de identidad (sin puntos ni espacios). */
    private String numeroDocumento;

    /** Nombre completo del cliente. */
    private String nombre;
}
